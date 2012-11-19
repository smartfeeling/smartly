package org.smartly.commons.network.socket.server;

import junit.framework.TestCase;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;
import org.smartly.commons.network.socket.server.handler.impl.EchoStringHandler;

import java.util.ResourceBundle;

public class TestServer extends TestCase {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    private ISocketHandler simpleSocketHandler = new EchoStringHandler();
    private Server simpleSocketServer;

    public void setUp() throws Exception {
        simpleSocketServer = Server.startServer(port, simpleSocketHandler);
    }

    public void tearDown() {
        simpleSocketServer.stopServer();
    }

    public void test1() throws Exception {
        String testString = "Hello";
        String response = Client.sendString(host, port, testString);
        assertTrue(response.indexOf("Hello") > 0);
        Client.sendString("Another String");
    }

    public void test2() throws Exception {
        String testString = "Hello World\nHow are you?";
        String response = Client.sendString(host, port, testString);
        assertTrue(response.indexOf("Hello") > 0);
        Client.sendString(host, port, "Another String");
    }

    public void test3() {
        //Force error by starting another server on same port
        Throwable ex = null;
        try {
            Server.startServer(port, new EchoStringHandler());
        } catch (Throwable t) {
            ex = t;
        }
        assertNotNull(ex);
        System.out.println("Multiple instances not allowed: " + ex.getMessage());
    }

}
