package org.smartly.commons.network.socket.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.messages.multipart.Multipart;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;

import java.util.ResourceBundle;

/**
 *
 */
public class SendMultipartTest {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    private Server _simpleSocketServer;

    @Before
    public void setUp() throws Exception {
        _simpleSocketServer = Server.startServer(port, new Class[]{});
        _simpleSocketServer.onMultipartFull(new Multipart.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                System.out.println("FULL: " + sender.toString());
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        _simpleSocketServer.stopServer();
    }

    @Test
    public void testMultipart() throws Exception {
        String testString = "Hello World\nHow are you?";
        MultipartInfo info = new MultipartInfo("test", MultipartInfo.MultipartInfoType.String, "part1", 0, 1);
        MultipartMessagePart part = new MultipartMessagePart();
        part.setInfo(info);
        part.setData(testString.getBytes());
        Object response = Client.send(host, port, part);
        System.out.println(response);
    }

}
