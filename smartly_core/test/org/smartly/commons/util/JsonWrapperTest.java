package org.smartly.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

/**
 *
 */
public class JsonWrapperTest {

    public JsonWrapperTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testToFlatMap() throws Exception {


        JsonWrapper json = new JsonWrapper(new JSONObject());
        JsonWrapper json1 = new JsonWrapper(new JSONObject());
        JsonWrapper json2 = new JsonWrapper(new JSONObject());

        JSONArray array = new JSONArray();
        array.put("val1");
        array.put("val2");

        json.put("json1", json1.getJSONObject());
        json.put("string", "STRING");
        json.put("int", 123);
        json.put("boolean", true);

        json1.put("json2", json2.getJSONObject());
        json1.put("string1", "STRING 1");

        json2.put("string2", "STRING 2");

        Map<String, Object> map = JsonWrapper.toFlatMap(json.getJSONObject());
        System.out.println(map);

        assertEquals(map.get("string"), "STRING");
        assertEquals(map.get("int"), 123);
        assertEquals(map.get("boolean"), true);

        assertEquals(map.get("json1.json2.string2"), "STRING 2");
        assertEquals(map.get("json1.string1"), "STRING 1");
    }
}
