package org.smartly.commons.network.socket.messages;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.remoting.rest.wrapper.ResponseWrapper;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;
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

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageResponse() {

    }

    public MessageResponse(final Serializable data) {
        super.setData(data);
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public void setData(final Object value) {
        if (value instanceof Serializable) {
            super.setData((Serializable) value);
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isNull() {
        return super.getDataLength()==0;
    }

    public boolean isError() {
        return isError(super.getData());
    }

    public boolean isJSON() {
        return isJSON(super.getData());
    }

    public Throwable getError() {
        final Serializable data = super.getData();
        if (isError(data)) {
            return (Throwable) data;
        }
        return null;
    }

    public JSONObject toJSONObject() {
        return toJSONObject(super.getData());
    }

    public JSONArray toJSONArray() {
        return toJSONArray(super.getData());
    }

    public int toInteger() {
        return toInteger(super.getData());
    }


    public boolean toBoolean() {
        return toBoolean(super.getData());
    }

    public double toDouble() {
        return toDouble(super.getData());
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

    private static boolean isJSONObject(final Object data) {
        return StringUtils.isJSONObject(data);
    }

    private static boolean isJSONArray(final Object data) {
        return StringUtils.isJSONArray(data);
    }

    private static boolean isError(final Object data) {
        return (data instanceof Throwable);
    }

    public static JSONObject toJSONObject(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toJSONObject();
            } else {
                if (isJSONObject(object)) {
                    return new JSONObject(object.toString());
                }
                return new JSONObject(FormatUtils.format("[", "]", JSON_TEMPLATE, isError(object), isNull(object) ? "" : object.toString()));
            }
        }
        return null;
    }

    public static JSONArray toJSONArray(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toJSONArray();
            } else {
                if (isJSONArray(object)) {
                    return new JSONArray(object.toString());
                }
            }
        }
        return null;
    }

    public static int toInteger(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toInteger();
            } else {
                return ConversionUtils.toInteger(ResponseWrapper.getResponse(object));
            }
        }
        return 0;
    }

    public static double toDouble(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toDouble();
            } else {
                return ConversionUtils.toDouble(ResponseWrapper.getResponse(object));
            }
        }
        return 0;
    }

    public static boolean toBoolean(final Object object) {
        if (null != object) {
            if (object instanceof MessageResponse) {
                return ((MessageResponse) object).toBoolean();
            } else {
                return ConversionUtils.toBoolean(ResponseWrapper.getResponse(object));
            }
        }
        return false;
    }
}
