/*
 * 
 */
package org.smartly.packages.http.impl.serialization.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartly.packages.http.impl.serialization.json.utils.JsonBeanUtils;

/**
 * Simple serializer/deserializer from java bean to json string and from
 * json string to JSONObject (or Exception or List of JSONObject)
 *
 * @author angelo.geminiani
 */
public class JsonSerializer {

    private static final String TAG_TYPE = JsonBeanUtils.TAG_TYPE;
    private static final String OBJECT = JsonBeanUtils.TYPE_OBJECT;
    private static final String ERROR = JsonBeanUtils.TYPE_ERROR;

    public static String serialize(final Object object) throws JSONException {
        final JsonBean result = new JsonBean(object);
        return result.toString();
    }

    /**
     * Deserialize JSON string. Return:<br/>
     * <ul>
     * <li>Exception</li><br/>
     * <li>JSONObject</li><br/>
     * <li>List of JSONObject</li><br/>
     * </ul>
     *
     * @param jsontext
     * @return
     */
    public static Object deserialize(final String jsontext) {
        final JsonBean result = new JsonBean(jsontext);
        return result.asObject();
    }

    public static boolean isError(final String json) {
        try {
            return isError(new JSONObject(json));
        } catch (Throwable t) {
        }
        return false;
    }

    public static boolean isError(final JSONObject json) {
        final String classType = json.optString(TAG_TYPE, null);
        if (null != classType) {
            return classType.equals(ERROR);
        }
        return false;
    }

    public static boolean isObject(final String json) {
        try {
            return isObject(new JSONObject(json));
        } catch (Throwable t) {
        }
        return false;
    }

    public static boolean isObject(final JSONObject json) {
        final String classType = json.optString(TAG_TYPE, null);
        if (null != classType) {
            return classType.equals(OBJECT);
        }
        return false;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
}
