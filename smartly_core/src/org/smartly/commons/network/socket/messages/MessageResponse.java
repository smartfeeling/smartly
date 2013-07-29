package org.smartly.commons.network.socket.messages;

import org.json.JSONObject;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;

import java.io.Serializable;

/**
 * Returning message wrapper
 */
public class MessageResponse
        extends AbstractMessage {

    private static final String JSON_TEMPLATE = "{\"isError\":[0], \"response\":\"[1]\"}";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Serializable _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageResponse() {

    }

    public MessageResponse(final Serializable data) {
        _data = data;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public void setData(final Object value) {
        if (value instanceof Serializable) {
            this.setData((Serializable) value);
        }
    }

    public void setData(final Serializable value) {
        _data = value;
    }

    public Serializable getData() {
        return _data;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isNull() {
        return isNull(_data);
    }

    public boolean isError() {
        return isError(_data);
    }

    public boolean isJSON() {
        return isJSON(_data);
    }

    public Throwable getError() {
        if (isError(_data)) {
            return (Throwable) _data;
        }
        return null;
    }

    public JSONObject toJSON() {
        return toJSON(_data);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static boolean isNull(final Object data) {
        return null == data;
    }

    private static boolean isJSON(final Object data) {
        return StringUtils.isJSON(data);
    }

    private static boolean isError(final Object data) {
        return (data instanceof Throwable);
    }

    public static JSONObject toJSON(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toJSON();
            } else {
                if (isJSON(object)) {
                    return new JSONObject(object.toString());
                }
                return new JSONObject(FormatUtils.format("[", "]", JSON_TEMPLATE, isError(object), isNull(object) ? "" : object.toString()));
            }
        }
        return null;
    }
}
