package org.smartly.packages.http.impl.util.client;

import org.junit.Test;

/**
 * User: angelo.geminiani
 */
public class ClientTest {

    public ClientTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testDoGET() throws Exception {
        String url = "http://www.smartfeeling.org";
        Client client = new Client();
        String result = client.doGET(url);

        System.out.println(result);
    }
}
