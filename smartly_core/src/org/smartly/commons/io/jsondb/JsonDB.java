package org.smartly.commons.io.jsondb;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.Delegates;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.io.File;

/**
 *
 */
public class JsonDB {

    // ------------------------------------------------------------------------
    //                      e v e n t s
    // ------------------------------------------------------------------------

    private static final Class EVENT_ERROR = Delegates.ExceptionCallback.class;

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------

    private static final String COLLECTIONS = "collections";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Delegates.Handlers _eventHandlers;
    private final String _root;
    private final Object _syncObj;

    private String _db_root;
    private String _file_metadata;
    private JsonWrapper _matadata;
    private boolean _open;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonDB(final String root) {
        _eventHandlers = new Delegates.Handlers();
        _root = root;
        _syncObj = new Object();
    }

    public JsonDB open(final String name) {
        if (!_open) {
            try {
                _db_root = PathUtils.concat(_root, name);
                _file_metadata = PathUtils.concat(_db_root, "_metadata.json");
                _matadata = this.init();
                _open = true;
            } catch (Throwable t) {
                this.close();
                this.handle(t);
            }
        }
        return this;
    }

    public void close() {
        _file_metadata = null;
        _matadata = null;
        _open = false;
    }

    public boolean isOpen() {
        return _open;
    }

    public String getRoot() {
        return _db_root;
    }

    public JsonDBCollection collection(final String name) {
        this.collectionsMetadata(name, false);
        return new JsonDBCollection(this, name);
    }

    public JsonDBCollection dropCollection(final String name) {
        this.collectionsMetadata(name, true);
        return new JsonDBCollection(this, name);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void onError(final Delegates.ExceptionCallback handler) {
        _eventHandlers.add(handler);
    }

    // ------------------------------------------------------------------------
    //                      p a c k a g e
    // ------------------------------------------------------------------------


    void collectionsMetadata(final String collName, final boolean drop) {
        synchronized (_syncObj) {
            final JSONArray collections = _matadata.optJSONArray(COLLECTIONS);
            if (null != collections) {
                final int length = collections.length();
                if (drop) {
                    // remove
                    JsonWrapper.removeAll(collections, collName);
                } else {
                    // add
                    if (!JsonWrapper.contains(collections, collName)) {
                        collections.put(collName);
                    }
                }
                this.saveMetadata();
            }
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JsonWrapper init() {
        JsonWrapper result = new JsonWrapper(new JSONObject());
        try {
            if (!FileUtils.exists(_file_metadata)) {
                // create empty
                result.put(COLLECTIONS, new JSONArray());
                // save
                FileUtils.mkdirs(_file_metadata);
                this.saveMetadata();
            } else {
                result = new JsonWrapper(this.readMetadata());
            }
        } catch (Throwable t) {
            this.handle(t);
        }
        return result;
    }

    private void handle(final Throwable t) {
        if (_eventHandlers.contains(EVENT_ERROR)) {
            _eventHandlers.triggerAsync(EVENT_ERROR, t);
        } else {
            LoggingUtils.getLogger(this).log(Level.SEVERE, null, t);
        }
    }

    private String readMetadata() {
        try {
            if (PathUtils.exists(_file_metadata)) {
                return FileUtils.readFileToString(new File(_file_metadata));
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    private boolean saveMetadata() {
        try {
            FileUtils.copy(_matadata.toString().getBytes(), new File(_file_metadata));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

}
