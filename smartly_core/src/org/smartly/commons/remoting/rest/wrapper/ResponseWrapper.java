package org.smartly.commons.remoting.rest.wrapper;

import org.json.JSONObject;
import org.smartly.commons.io.serialization.json.JsonBean;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

/**
 * Wrap Response value into JSONObject
 */
public class ResponseWrapper {

    private static final String RESPONSE = "response";


    public static Object getResponse(final Object data) {
        if (StringUtils.isJSONObject(data)) {
            final JsonWrapper json = new JsonWrapper(data.toString());
            return json.get(RESPONSE);
        }
        return data;
    }

    /**
     * Serialize an object in JSON Object String.
     * Native values (int, boolean, etc..) are wrapped into JSON response object.
     *
     * @param data Data to wrap
     * @return JSONObject or JSONArray as String
     */
    public static String wrapToJSONString(final Object data) {
        if (StringUtils.isJSON(data)) {
            final JsonBean json = new JsonBean(data);
            return json.asJSONObject().toString();
        } else {
            return wrapToJSONResponse(data.toString()).toString();
        }
    }

    public static JSONObject wrapToJSONResponse(final String text) {
        final JSONObject json = new JSONObject();
        JsonWrapper.put(json, RESPONSE, text);
        return json;
    }

}
