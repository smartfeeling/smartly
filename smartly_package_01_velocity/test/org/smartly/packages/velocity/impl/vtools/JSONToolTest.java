package org.smartly.packages.velocity.impl.vtools;

import org.junit.Test;
import org.smartly.commons.util.JsonWrapper;

import static org.junit.Assert.*;

/**
 * User: angelo.geminiani
 */
public class JSONToolTest {

    public JSONToolTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testParse() throws Exception {

        final JSONTool tool = new JSONTool();

        String array = "['error', 'foo', 'NULL']";

        JsonWrapper result = tool.parse(array);
        assertTrue(result.length()==3);
        assertEquals(result.get(0), "error");
        assertEquals(result.get(1), "foo");
        assertEquals(result.get(2), "NULL");

        array = "[error, foo, NULL]";

        result = tool.parse(array);
        assertTrue(result.length()==3);
        assertEquals(result.get(0), "error");
        assertEquals(result.get(1), "foo");
        assertEquals(result.get(2), null);

        array = "['error, foo, NULL']";

        result = tool.parse(array);
        assertTrue(result.length()==1);
        assertEquals(result.get(0), "error, foo, NULL");

    }
}
