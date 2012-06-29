package org.smartly.commons.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class PathUtilsTest {


    @Test
    public void testgetAbsolutePath() throws Exception {

        String absolute = PathUtils.getAbsolutePath("./log/test.log");

        assertTrue(PathUtils.isAbsolute(absolute));
        System.out.println(absolute);

    }

    @Test
    public void testgetSplitRoot() throws Exception {

        String result = PathUtils.splitPathRoot("/test/path1/file.txt");

        assertTrue(result.equalsIgnoreCase("/path1/file.txt"));
        System.out.println(result);

    }

}
