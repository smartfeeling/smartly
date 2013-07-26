package org.smartly.commons.io.jsondb;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.io.jsondb.exceptions.JsonDBInvalidItemException;
import org.smartly.commons.util.*;

import java.io.File;

/**
 * Collection
 */
public class JsonDBCollection {

    private static final String ID = IJsonDBConstants.ID;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final JsonDB _db;
    private final String _file_path;
    private final String _name;

    private JSONArray __data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public JsonDBCollection(final JsonDB db,
                            final String name) {
        _db = db;
        _file_path = PathUtils.concat(_db.getRoot(), name.concat(".json"));
        _name = name;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String getName() {
        return _name;
    }

    public void drop() {
        _db.collectionsMetadata(_name, true);
    }

    public synchronized JsonList find() {
        final JSONArray data = this.getData();
        return JsonWrapper.toListOfJSONObject(data);
    }

    public synchronized JsonList find(final String key, final Object value) {
        final JSONArray data = this.getData();
        return JsonWrapper.find(data, key, value);
    }

    public synchronized JSONObject findOne(final String key, final Object value) {
        final JSONArray data = this.getData();
        return JsonWrapper.findOne(data, key, value);
    }

    public String findOneAsString(final String key, final Object value) {
        final JSONArray data = this.getData();
        final JSONObject result = JsonWrapper.findOne(data, key, value);
        return null!=result?result.toString():"{}";
    }

    public synchronized JSONObject upsert(final Object item) throws JsonDBInvalidItemException {
        if (item instanceof String && StringUtils.isJSONObject(item)) {
            this.upsert(new JSONObject((String) item));
        } else if (item instanceof JSONObject) {
            this.upsert((JSONObject) item);
        }

        throw new JsonDBInvalidItemException("Invalid item type: " + null != item ? item.getClass().getName() : "NULL");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JSONArray getData() {
        if (null == __data) {
            final String data = this.read();
            if (StringUtils.isJSONArray(data)) {
                __data = new JSONArray(data);
            } else {
                __data = new JSONArray();
                this.save();
            }
        }
        return __data;
    }

    private JSONObject upsert(final JSONObject item) {
        //-- prepare item --//
        if (item.has(ID)) {
            // update
            final JSONObject existing = JsonWrapper.removeOne(this.getData(), ID, item.optString(ID));
            JsonWrapper.extend(existing, item, true);
            this.add(existing);
            return existing;
        } else {
            // insert
            item.put(ID, GUID.create());
            this.add(item);
            return item;
        }
    }

    private void add(final JSONObject item) {
        //-- add to store and save --/
        this.getData().put(item);
        this.save();
    }

    private String read() {
        try {
            if (PathUtils.exists(_file_path)) {
                return FileUtils.readFileToString(new File(_file_path));
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    private boolean save() {
        try {
            FileUtils.copy(this.getData().toString().getBytes(), new File(_file_path));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }
}
