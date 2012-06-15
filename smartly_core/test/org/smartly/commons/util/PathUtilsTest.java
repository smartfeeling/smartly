package org.smartly.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PathUtilsTest {


    @Test
    public void testgetAbsolutePath() throws Exception {

        String absolute = PathUtils.getAbsolutePath("./log/test.log");

        assertTrue(PathUtils.isAbsolute(absolute));
        System.out.println(absolute);

    }

}
