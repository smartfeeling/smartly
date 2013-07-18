package org.smartly.commons.network.socket.messages.rest;

import org.json.JSONObject;
import org.smartly.commons.util.StringUtils;

import java.io.Serializable;

/**
 * Wrapper for REST like message.
 */
public class RESTMessage implements Serializable {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private String _method;
    private String _path;
    private String _data; // json string

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public RESTMessage() {
        _method = METHOD_GET;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void setPath(final String value) {
        _path = value;
    }

    public String getPath() {
        return _path;
    }

    public void setMethod(final String value) {
        _method = value;
    }

    public String getMethod() {
        return _method;
    }

    public void setData(final String json) {
        if (StringUtils.isJSONObject(json)) {
            _data = json;
        }
    }

    public void setData(final JSONObject json) {
        _data = json.toString();
    }

    public String getData() {
        return _data;
    }

    public JSONObject getDataAsJSON() {
        if (StringUtils.isJSONObject(_data)) {
            return new JSONObject(_data);
        }
        return new JSONObject();
    }
}
