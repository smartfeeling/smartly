package org.smartly.commons.network.sftp;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * User: angelo.geminiani
 */
public class SFTPClientTest {

    public SFTPClientTest() {

    }

    @Test
    public void testConnect() throws Exception {

        String host = "192.168.40.34";
        int port = 22;
        String user = "ftp_nurith";
        String password = "Ad38!nP;z";

        SFTPClient client = new SFTPClient(host, port, user, password);

        client.connect();

        assertTrue(client.isConnected());

        Set<String> dirs = client.list(null);

        System.out.println(dirs);

        String dir = dirs.iterator().next();
        System.out.println("> cd " + dir);
        client.cd(dir);

        dirs = client.list();
        System.out.println(dirs);

        client.disconnect();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
