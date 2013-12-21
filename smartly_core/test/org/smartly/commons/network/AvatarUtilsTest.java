package org.smartly.commons.network;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *
 */
public class AvatarUtilsTest {


    @Test
    public void testGetAvatarByEmail() throws Exception {
        final String email = "angelo.geminiani@gmail.com";

        final String url = AvatarUtils.getAvatarUrl(email, true, 0, 0, 100, 100);
        System.out.println(url);
        assertNotNull(url);

        final String base64 = AvatarUtils.getAvatarBase64(email, 2, 2, 72, 72);
        System.out.println(base64);
        assertNotNull(url);
    }

    @Test
    public void testGetGravatarAvatarByEmail() throws Exception {
        final String email = "angelo.geminiani@gmail.com";

        final String url = AvatarUtils.getGraAvatarUrl(email, false, 50, 0);
        System.out.println(url);
        assertNotNull(url);

        // 404
        final byte[] bytes = AvatarUtils.getGravatar("foo", false, 200, 0);
        assertTrue(bytes.length==0);
    }
}
