package org.smartly.packages.velocity.impl.vtools;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.commons.util.JsonWrapper;

import java.util.Collection;
import java.util.Map;

/**
 * JSON helper for VTL expressions.
 */
public class JSONTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "JSON";

    public JSONTool() {

    }

    public String toString(final Object item) {
        return item.toString();
    }

    public String stringify(final Object item) {
        return item.toString();
    }

    public JsonWrapper parse(final Object item) {
        if (null != item) {
            if (item instanceof JsonWrapper) {
                return (JsonWrapper) item;
            } else if (item instanceof String) {
                return JsonWrapper.wrap((String) item);
            } else if (item instanceof JSONObject) {
                return JsonWrapper.wrap((JSONObject) item);
            } else if (item instanceof JSONArray) {
                return JsonWrapper.wrap((JSONArray) item);
            } else if (item instanceof Map) {
                return JsonWrapper.wrap((Map) item);
            } else if (item instanceof Collection) {
                return JsonWrapper.wrap((Collection) item);
            }
        }
        return new JsonWrapper(new JSONObject());
    }

    public JSONArray parseJSONArray(final Object item) {
        final JsonWrapper json = this.parse(item);
        if (json.isJSONArray()) {
            return json.getJSONArray();
        } else {
            return new JSONArray();
        }
    }

    public JSONObject parseJSONObject(final Object item) {
        final JsonWrapper json = this.parse(item);
        if (json.isJSONObject()) {
            return json.getJSONObject();
        } else {
            return new JSONObject();
        }
    }

    public boolean has(final Object item, final String name) {
        final JsonWrapper json = this.parse(item);
        return json.has(name);
    }

    public Object get(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        final Object result = json.deep(path);
        return null != result ? result : IConstants.NULL;
    }

    public boolean getBoolean(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        return json.deepBoolean(path);
    }

    public int getInt(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        return json.deepInteger(path);
    }

    public long getLong(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        return json.deepLong(path);
    }

    public double getDouble(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        return json.deepDouble(path);
    }

    public String getString(final Object item, final String path) {
        final JsonWrapper json = this.parse(item);
        return json.deepString(path);
    }

    public Object put(final Object item, final String path, final Object value) {
        final JsonWrapper json = this.parse(item);
        return json.putDeep(path, value);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
