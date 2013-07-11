package org.smartly.commons.network.socket.server;

import junit.framework.TestCase;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.server.helpers.FooFilter;
import org.smartly.commons.network.socket.server.helpers.SampleFilter;
import org.smartly.commons.network.socket.server.helpers.ThreadClient;

import java.util.ResourceBundle;

public class TestServer extends TestCase {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    private Server simpleSocketServer;

    public void setUp() throws Exception {
        simpleSocketServer = Server.startServer(port, new Class[]{FooFilter.class, SampleFilter.class});
        simpleSocketServer.addFilter(FooFilter.class);
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
            Server.startServer(port, null);
        } catch (Throwable t) {
            ex = t;
        }
        assertNotNull(ex);
        System.out.println("Multiple instances not allowed: " + ex.getMessage());
    }

    public void test4() throws Exception {
        Thread[] tasks = new Thread[50];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new Thread(new ThreadClient(host, port, i));
        }

        for (Thread task : tasks) {
            task.start();
        }

        for (Thread task : tasks) {
            task.join();
        }
    }

}
