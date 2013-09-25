package org.smartly.commons.io.temprepository;

import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.IOException;

/**
 * Repository with expiration time for its content.
 * Expired files are removed
 */
public class TempRepository
        implements IFileObserverListener {

    private static final String REGISTRY_LOG = "_registry.log";
    private static final String REGISTRY_SETTINGS = "_registry_settings.json";
    private static final String REGISTRY_DATA = "_registry_data.json";

    private static final String LOGGER_PREFIX = "[" + TempRepository.class.getName() + "] ";

    private static final int MAX_ERRORS = 10;

    private final String _root;
    private final String _path_data;
    private final String _path_settings;
    private final String _path_log;
    private final Registry _registry;
    private FileObserver _dirObserver;
    private int _countErrors;
    private boolean _debugMode;

    public TempRepository(final String root) throws IOException {
        _root = root;
        _path_data = PathUtils.concat(_root, REGISTRY_DATA);
        _path_settings = PathUtils.concat(_root, REGISTRY_SETTINGS);
        _path_log = PathUtils.concat(_root, REGISTRY_LOG);

        LoggingRepository.getInstance().setAbsoluteLogFileName(REGISTRY_LOG, _path_log);

        //-- ensure temp dir exists --//
        FileUtils.mkdirs(root);

        //-- load file registry (creates if any) --//
        _registry = new Registry(_path_settings, _path_data);
        _registry.save();

        _countErrors = 0;
        _debugMode = false;

        this.startThreads();
    }

    @Override
    protected void finalize() throws Throwable {
        this.interrupt();
        super.finalize();
    }

    @Override
    public void onEvent(int event, final String path) {
        this.handle(event, path);
    }

    public String getRoot() {
        return _root;
    }

    public boolean isDebugMode() {
        return _debugMode;
    }

    public void setDebugMode(final boolean value) {
        _debugMode = value;
        this.debug("Debug Mode: " + value);
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
        return LoggingUtils.getLogger(REGISTRY_LOG);
    }

    private void startThreads() {
        try {
            this.startDirObserver();
            this.debug("Started Path Observer");
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        try {
            _registry.start();
            this.debug("Started Registry");
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
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

    private void handle(final int event, final String path) {
        if (!_path_log.equalsIgnoreCase(path)) {
            String sevent = "UNDEFINED";
            try {
                if (event == FileObserver.EVENT_CREATE) {
                    sevent = "CREATE";
                    // CREATE
                    if (!_path_data.equalsIgnoreCase(path)
                            && !_path_settings.equalsIgnoreCase(path)) {
                        if (_registry.addItem(path)) {
                            _registry.save();
                            this.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                        }
                    }
                } else if (event == FileObserver.EVENT_MODIFY) {
                    sevent = "MODIFY";
                    // MODIFY
                    if (_path_settings.equalsIgnoreCase(path)) {
                        _registry.reloadSettings();
                        this.debug("Changed Settings: reload all settings from file.");
                    }
                } else if (event == FileObserver.EVENT_DELETE) {
                    sevent = "DELETE";
                    if (!_path_data.equalsIgnoreCase(path)
                            && !_path_settings.equalsIgnoreCase(path)) {
                        if (_registry.removeItem(path)) {
                            _registry.save();
                            this.debug(FormatUtils.format("Action '{0}' on '{1}'", sevent, path));
                        }
                    } else {
                        _registry.clear();
                        _registry.save();
                        this.debug("Removed DATA file: reset of registry.");
                    }
                }
            } catch (final Throwable t) {
                final String msg = FormatUtils.format("Error on '{0}' path '{1}' to temp repository: {2}",
                        sevent, path, t);
                this.handleError(msg, t);
            }
        }
    }

    public void handleError(final String message,
                            final Throwable t) {
        if (_countErrors < MAX_ERRORS) {
            _countErrors++;
            if (StringUtils.hasText(message)) {
                this.getLogger().log(Level.SEVERE, LOGGER_PREFIX.concat(message), t);
            } else {
                this.getLogger().log(Level.SEVERE,
                        LOGGER_PREFIX.concat(FormatUtils.format("{0}", t)),
                        t);
            }

        } else {
            this.interrupt();
        }
    }

    public void debug(final String message) {
        if (_debugMode) {
            this.getLogger().log(Level.INFO, LOGGER_PREFIX.concat(message));
        }
    }
}
