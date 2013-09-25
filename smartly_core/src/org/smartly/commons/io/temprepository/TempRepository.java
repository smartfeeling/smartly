package org.smartly.commons.io.temprepository;

import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;

/**
 * Repository with expiration time for its content.
 * Expired files are removed
 */
public class TempRepository
        implements IFileObserverListener {

    private static final String REGISTRY_SETTINGS = "_registry_settings.json";
    private static final String REGISTRY_DATA = "_registry_data.json";


    private final String _root;
    private final String _path_data;
    private final String _path_settings;
    private final Registry _registry;
    private FileObserver _dirObserver;


    public TempRepository(final String root) throws IOException {
        _root = root;
        _path_data = PathUtils.concat(_root, REGISTRY_DATA);
        _path_settings = PathUtils.concat(_root, REGISTRY_SETTINGS);

        //-- ensure temp dir exists --//
        FileUtils.mkdirs(root);

        //-- load file registry (creates if any) --//
        _registry = new Registry(_path_settings, _path_data);
        _registry.save();

        this.startThreads();
    }

    @Override
    protected void finalize() throws Throwable {
        this.interrupt();
        super.finalize();
    }

    public String getRoot() {
        return _root;
    }

    public void setDuration(final long ms) {
        _registry.interrupt();
        _registry.setLife(ms);
        _registry.start();
    }

    public void setCheckTime(final long ms) {
        _registry.interrupt();
        _registry.setCheck(ms);
        _registry.start();
    }

    public void interrupt() {
        this.stopThreads();

        try {
            _registry.save();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Created only for test purpose.
     */
    public void join() throws InterruptedException {
        _registry.join();
    }

    public void clear() {
        //-- stop threads--//
        this.stopThreads();

        //-- clear root --//
        try {
            FileUtils.delete(_root);
            FileUtils.mkdirs(_root);
        } catch (Throwable ignored) {
        }

        //-- reset registry--//
        try {
            _registry.clear();
            _registry.save();
        } catch (Throwable ignored) {
        }

        //-- start --//
        this.startThreads();
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void startThreads() {
        try {
            this.startDirObserver();
        } catch (Throwable ignored) {
        }
        try {
            _registry.start();
        } catch (Throwable ignored) {
        }
    }

    private void stopThreads() {
        try {
            _dirObserver.interrupt();
            _dirObserver = null;
        } catch (Throwable ignored) {
        }

        try {
            _registry.interrupt();
        } catch (Throwable ignored) {
        }
    }

    private void startDirObserver() throws IOException {
        //-- file observer initialization --//
        if (null != _dirObserver) {
            _dirObserver.interrupt();
            _dirObserver = null;
        }
        _dirObserver = new FileObserver(_root, true, false, FileObserver.ALL_EVENTS, this);
        _dirObserver.startWatching();
    }

    @Override
    public void onEvent(int event, String path) {
        String sevent = "UNDEFINED";
        try {
            if (event == FileObserver.EVENT_CREATE) {
                sevent = "CREATE";
                // CREATE
                if (!_path_data.equalsIgnoreCase(path)
                        && !_path_settings.equalsIgnoreCase(path)) {
                    if (_registry.addItem(path)) {
                        _registry.save();
                    }
                }
            } else if (event == FileObserver.EVENT_MODIFY) {
                sevent = "MODIFY";
                // MODIFY
                if (_path_settings.equalsIgnoreCase(path)) {
                    _registry.reloadSettings();
                }
            } else if (event == FileObserver.EVENT_DELETE) {
                sevent = "DELETE";
                if (!_path_data.equalsIgnoreCase(path)
                        && !_path_settings.equalsIgnoreCase(path)) {
                    if (_registry.removeItem(path)) {
                        _registry.save();
                    }
                } else {
                    _registry.clear();
                    _registry.save();
                }
            }
        } catch (Throwable t) {
            final String msg = FormatUtils.format("Error on '{0}' path '{1}' to temp repository: {2}",
                    sevent, path, t);
            getLogger().severe(msg);
            //-- problem with registry. stop everything --//
            this.interrupt();
        }
    }
}
