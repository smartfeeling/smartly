package org.smartly.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 *
 *
 */
public class StringUtilsTest {

    @Test
    public void testSplit() throws Exception {

        System.out.println("Test Split");

        String[] tokens = StringUtils.split("prefix_suffix", "DELIM", true);
        assertTrue(tokens.length == 1);

        tokens = StringUtils.split("prefix_suffix", "_DELIM_", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix_suffix", "_", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix!suffix", "!DELIM!", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix!suffix", "!", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.suffix", ".DELIM.", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.suffix", ".", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("prefix.DELIM.suffix", ".DELIM.", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split(" prefix suffix", " ", true);
        assertTrue(tokens.length == 2);
        assertEquals(tokens[0], "prefix");
        assertEquals(tokens[1], "suffix");

        tokens = StringUtils.split("1:2;3;", ":;", true);
        assertTrue(tokens.length == 3);
        assertEquals(tokens[0], "1");
        assertEquals(tokens[1], "2");
        assertEquals(tokens[2], "3");
    }
}
