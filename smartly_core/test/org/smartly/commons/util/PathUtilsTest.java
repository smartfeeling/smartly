package org.smartly.commons.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testResolve() throws Exception {

        String root = "http://localhost/dir1/dir2/";
        String path = "../../file.html";
        String concat = root + path;
        String resolved = PathUtils.resolve(concat);

        assertTrue(resolved.equalsIgnoreCase("http://localhost/file.html"));
        System.out.println(resolved);

    }

    @Test
    public void addUriParameters() throws Exception {

        String root = "http://localhost/dir1/dir2/";
        Map<String, String> params = new HashMap<String, String>();
        params.put("param1", "value1");
        params.put("param2", "value2 value2bis");

        String resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("http://localhost/dir1/dir2/?param1=value1&param2=value2+value2bis"));
        System.out.println(resolved);

        root = "http://localhost/dir1/dir2/?param1=value1";
        params = new HashMap<String, String>();
        params.put("param2", "value2 value2bis");

        resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("http://localhost/dir1/dir2/?param1=value1&param2=value2+value2bis"));
        System.out.println(resolved);

        root = "http://localhost/dir1/dir2/?param1=value1";
        params = new HashMap<String, String>();
        params.put("param1", "value1_replaced");
        params.put("param2", "value2 value2bis");

        resolved = PathUtils.addURIParameters(root, params, true);

        assertTrue(resolved.equalsIgnoreCase("http://localhost/dir1/dir2/?param1=value1_replaced&param2=value2+value2bis"));
        System.out.println(resolved);
    }

}
