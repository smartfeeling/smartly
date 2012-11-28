package org.smartly.commons.io.temprepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class TempRepository {

    private static final String REGISTRY = "registry.json";
    private static final String REGISTRY_LIFE_MS = "life_ms";
    private static final String REGISTRY_CHECK_MS = "check_ms";
    private static final String REGISTRY_ITEMS = "items";
    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private final String _root;
    private final String _path_registry;
    private final JsonWrapper _registry;
    private FileObserver _dirObserver;
    private FileObserver _registryObserver;
    private Thread _registryThread;

    public TempRepository(final String root) throws IOException {
        _root = root;
        _path_registry = PathUtils.concat(_root, REGISTRY);

        //-- ensure temp dir exists --//
        FileUtils.mkdirs(root);

        //-- load file registry (creates if any) --//
        _registry = new JsonWrapper(this.loadRegistry(_path_registry));
        this.saveRegistry();

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

    public void interrupt() {
        this.stopThreads();

        try {
            this.saveRegistry();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Created only for test porpoise.
     */
    public void join() throws InterruptedException {
        if (null != _registryThread) {
            _registryThread.join();
        }
    }

    public void clear() {
        synchronized (_registry) {
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
                _registry.putSilent(REGISTRY_ITEMS, new JSONObject());
                this.saveRegistry();
            } catch (Throwable ignored) {
            }

            //-- start --//
            this.startThreads();
        }
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
            this.startRegistryObserver();
        } catch (Throwable ignored) {
        }
        try {
            this.startRegistryThread();
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
            _registryObserver.interrupt();
            _registryObserver = null;
        } catch (Throwable ignored) {
        }

        try {
            _registryThread.interrupt();
            _registryThread = null;
        } catch (Throwable ignored) {
        }
    }

    private void startDirObserver() throws IOException {
        //-- file observer initialization --//
        if (null != _dirObserver) {
            _dirObserver.interrupt();
            _dirObserver = null;
        }
        _dirObserver = new FileObserver(_root, true, false, FileObserver.EVENT_CREATE) {
            @Override
            protected void onEvent(final int event, final String path) {
                try {
                    addToRegistry(path);
                } catch (Throwable t) {
                    final String msg = FormatUtils.format("Error adding '{0}' to temp repository: {1}", path, t);
                    getLogger().severe(msg);
                    //-- problem with registry. stop everything --//
                    _dirObserver.interrupt();
                }
            }
        };
        _dirObserver.startWatching();
    }

    private void startRegistryObserver() throws IOException {
        //-- file observer initialization --//
        if (null != _registryObserver) {
            _registryObserver.interrupt();
            _registryObserver = null;
        }
        _registryObserver = new FileObserver(_path_registry, false, false, FileObserver.EVENT_MODIFY) {
            @Override
            protected void onEvent(final int event, final String path) {
                try {
                    // reload registry
                    synchronized (_registry) {
                        final String text = loadRegistry(_path_registry);
                        if (StringUtils.isJSONObject(text)) {
                            _registry.parse(text);
                        }
                    }
                } catch (Throwable t) {
                    final String msg = FormatUtils.format("Error adding '{0}' to temp repository: {1}", path, t);
                    getLogger().severe(msg);
                    //-- problem with registry. stop everything --//
                    _registryObserver.interrupt();
                }
            }
        };
        _registryObserver.startWatching();
    }

    private void startRegistryThread() {
        // creates thread that check for expired items
        _registryThread = new Thread(new Runnable() {
            boolean _interrupted = false;

            @Override
            public void run() {
                while (!_interrupted) {
                    try {
                        final long sleep = _registry.optLong(REGISTRY_CHECK_MS);
                        Thread.sleep(sleep > 0 ? sleep : DEFAULT_LIFE * 2);
                        //-- check registry items --//
                        garbage();
                    } catch (InterruptedException ignored) {
                        _interrupted = true;
                    }
                }
            }
        });
        _registryThread.setPriority(Thread.NORM_PRIORITY);
        _registryThread.start();
    }

    private String loadRegistry(final String fileName) {
        try {
            return FileUtils.readFileToString(new File(fileName));
        } catch (Throwable t) {
            return "{'" + REGISTRY_LIFE_MS + "':" + DEFAULT_LIFE + ", '" + REGISTRY_CHECK_MS + "':" + DEFAULT_LIFE + " ,'items':[]}";
        }
    }

    private void saveRegistry() throws IOException {
        if (null != _registryObserver) {
            _registryObserver.pause();
        }
        FileUtils.writeStringToFile(new File(_path_registry),
                _registry.toString(1),
                Smartly.getCharset());
        if (null != _registryObserver) {
            _registryObserver.resume();
        }
    }

    private void addToRegistry(final String path) throws IOException {
        synchronized (_registry) {
            try {
                if (PathUtils.isFile(path)) {
                    final RegistryItem item = new RegistryItem();
                    item.setPath(path);
                    _registry.getJSONArray(REGISTRY_ITEMS).put(item.getData());
                    this.saveRegistry();
                }
            } catch (Throwable t) {
                this.getLogger().warning(FormatUtils.format("Unable to add '{0}' to temp repository: {1}", path, t));
            }
        }
    }

    private void garbage() {
        synchronized (_registry) {
            final long duration = _registry.optLong(REGISTRY_LIFE_MS);
            try {
                final JSONArray items = _registry.optJSONArray(REGISTRY_ITEMS);
                final int len = items.length();
                for (int i = len - 1; i > -1; i--) {
                    try {
                        final RegistryItem item = new RegistryItem(items.getJSONObject(i));
                        if (item.expired(duration)) {
                            this.removeFile(item.getPath());
                            items.remove(i);
                        }
                    } catch (Throwable t) {
                        this.getLogger().warning(FormatUtils.format("Item '#{0}' force removing from temp repository due error: {1}", i, t));
                        items.remove(i);
                    }
                }
                this.saveRegistry();
            } catch (Throwable t) {
                this.getLogger().warning(FormatUtils.format("Garbage Error in temp repository: {0}", t));
            }
        }
    }

    private void removeFile(final String file) {
        try {
            FileUtils.delete(file);
        } catch (Throwable ignored) {
        }
    }

    // --------------------------------------------------------------------
    //               EMBEDDED
    // --------------------------------------------------------------------

    private static final class RegistryItem {

        private static final String PATH = "path";
        private static final String TIMESTAMP = "timestamp";

        private final JsonWrapper _data;
        private String _path;

        public RegistryItem() {
            _data = new JsonWrapper(new JSONObject());
        }

        public RegistryItem(final JSONObject item) {
            _data = new JsonWrapper(item);
        }

        public JSONObject getData() {
            return _data.getJSONObject();
        }

        public void setPath(final String path) {
            _data.putSilent(PATH, path);
            _data.putSilent(TIMESTAMP, System.currentTimeMillis());
        }

        public String getPath() {
            return _data.optString(PATH);
        }

        public long getTimestamp() {
            return _data.optLong(TIMESTAMP);
        }

        public boolean expired(final long duration) {
            final long now = System.currentTimeMillis();
            return now - this.getTimestamp() > duration;
        }

    }


}
