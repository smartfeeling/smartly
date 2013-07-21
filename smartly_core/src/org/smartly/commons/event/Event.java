/*
 *
 */

package org.smartly.commons.event;

import org.json.JSONObject;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

/**
 *
 *
 */
public class Event {

    private static final String FLD_SENDER = "sender";
    private static final String FLD_NAME = "name";
    private static final String FLD_DATA = "data";

    private final JSONObject _json;

    public Event(final Object sender,
                 final String name) {
        _json = new JSONObject();
        this.put(FLD_SENDER, sender);
        this.put(FLD_NAME, name);
    }

    public Event(final Object sender,
                 final String name,
                 final Object data) {
        _json = new JSONObject();
        this.put(FLD_SENDER, sender);
        this.put(FLD_NAME, name);
        this.setData(data);
    }

    @Override
    public String toString() {
        return _json.toString();
    }

    public JSONObject toJSON() {
        return new JSONObject(_json);
    }

    public Object getSender() {
        return JsonWrapper.get(_json, FLD_SENDER);
    }

    /*
    public void setSender(Object sender) {
        _sender = sender;
    }
    */
    public String getName() {
        return JsonWrapper.getString(_json, FLD_DATA);
    }

    public Object getData() {
        return JsonWrapper.get(_json, FLD_DATA);
    }

    public void setData(final Object data) {
        this.put(FLD_DATA, data);
    }

    protected void put(final String key, final Object value) {
        if (StringUtils.hasText(key) && null != value) {
            JsonWrapper.put(_json, key, value);
        }
    }

    protected Object get(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.get(_json, key);
        }
        return null;
    }

    protected String getString(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getString(_json, key);
        }
        return "";
    }

    protected boolean getBoolean(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getBoolean(_json, key);
        }
        return false;
    }

    protected int getInt(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getInt(_json, key);
        }
        return 0;
    }

    protected double getDouble(final String key) {
        if (StringUtils.hasText(key)) {
            return JsonWrapper.getDouble(_json, key);
        }
        return 0.0;
    }
}
