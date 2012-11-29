package org.smartly.commons.io.temprepository;

import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.cryptograph.MD5;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 */
public class Registry {

    private static final long DEFAULT_LIFE = 60 * 1000; // 1 minute

    private static final String LIFE_MS = "life_ms";
    private static final String CHECK_MS = "check_ms";
    private static final String ITEMS = "items";

    private final String _path_registry;
    private final JsonWrapper _data;
    private Thread _registryThread;

    public Registry(final String path) {
        _path_registry = path;

        _data = new JsonWrapper(this.loadRegistry(_path_registry));
    }

    public void start() {
        this.startRegistryThread();
    }

    public void interrupt() {
        if (null != _registryThread) {
            _registryThread.interrupt();
            _registryThread = null;
        }
    }

    public void join() {
        if (null != _registryThread) {
            try {
                _registryThread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void clear() {
        this.reload();
        synchronized (_data) {
            _data.putSilent(ITEMS, new JSONObject());
        }
    }

    public void reload() {
        _data.parse(this.loadRegistry(_path_registry));
    }

    public long getLife() {
        final long result = _data.optLong(LIFE_MS);
        return result > 0 ? result : DEFAULT_LIFE;
    }

    public void setLife(final long value) {
        _data.putSilent(LIFE_MS, value);
        if (_data.optLong(CHECK_MS) < value) {
            _data.putSilent(CHECK_MS, value);
        }
    }

    public long getCheck() {
        final long result = _data.optLong(CHECK_MS);
        return result > getLife() ? result : getLife();
    }

    public void setCheck(final long value) {
        _data.putSilent(CHECK_MS, value);
    }

    public void save() throws IOException {
        this.saveFile();
    }

    public boolean addItem(final String path) {
        synchronized (_data) {
            final String key = getId(path);
            final JsonWrapper items = new JsonWrapper(_data.optJSONObject(ITEMS));
            if (!items.has(key)) {
                items.putSilent(key, (new RegistryItem(path)).getData());
                return true;
            }
            return false;
        }
    }

    public boolean removeItem(final String path) {
        synchronized (_data) {
            final String key = getId(path);
            return this.removeItemByKey(key);
        }
    }

    public int removeExpired() {
        synchronized (_data) {
            int count = 0;
            final long life = this.getLife();
            final JsonWrapper items = new JsonWrapper(_data.optJSONObject(ITEMS));
            final Set<String> keys = items.keys();
            for (final String key : keys) {
                final RegistryItem item = new RegistryItem(items.optJSONObject(key));
                if (item.expired(life)) {
                    items.remove(key);
                    // remove file
                    this.removeFile(item.getPath());
                    count++;
                }
            }
            return count;
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private String loadRegistry(final String fileName) {
        try {
            return FileUtils.readFileToString(new File(fileName));
        } catch (Throwable t) {
            return "{'" + LIFE_MS + "':" + DEFAULT_LIFE + ", '" +
                    CHECK_MS + "':" + DEFAULT_LIFE +
                    " ,'items':{}}";
        }
    }

    private void saveFile() throws IOException {
        FileUtils.writeStringToFile(new File(_path_registry),
                _data.toString(1),
                Smartly.getCharset());
    }

    private void removeFile(final String file) {
        try {
            FileUtils.delete(file);
        } catch (Throwable ignored) {
        }
    }

    public boolean removeItemByKey(final String key) {
        final JSONObject items = _data.optJSONObject(ITEMS);
        return null != JsonWrapper.remove(items, key);
    }

    private void startRegistryThread() {
        // creates thread that check for expired items
        _registryThread = new Thread(new Runnable() {
            boolean _interrupted = false;

            @Override
            public void run() {
                while (!_interrupted) {
                    try {
                        final long sleep = getCheck();
                        Thread.sleep(sleep);
                        //-- check registry items --//
                        if (removeExpired() > 0) {
                            try {
                                save();
                            } catch (Throwable ignored) {
                            }
                        }
                    } catch (InterruptedException ignored) {
                        _interrupted = true;
                    }
                }
            }
        });
        _registryThread.setPriority(Thread.NORM_PRIORITY);
        _registryThread.start();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getId(final String path) {
        return MD5.encode(PathUtils.toUnixPath(path));
    }

}
