package org.smartly.commons.network;

import org.junit.Test;

/**
 * User: angelo.geminiani
 */
public class URLUtilsTest {

    public URLUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testGetUrlContent() throws Exception {
        String url = "http://www.smartfeeling.org";
        String result = URLUtils.getUrlContent(url, 3000, URLUtils.TYPE_JSON);

        System.out.println(result);
    }
}
