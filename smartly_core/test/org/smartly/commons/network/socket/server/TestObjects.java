package org.smartly.commons.network.socket.server;

import junit.framework.TestCase;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.server.Server;
import org.smartly.commons.network.socket.server.handler.impl.EchoDateHandler;
import org.smartly.commons.network.socket.server.handler.impl.EchoHandler;
import org.smartly.commons.network.socket.server.handler.impl.EchoNullHandler;

import java.util.Date;
import java.util.ResourceBundle;

public class TestObjects extends TestCase {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = 10 + Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    public void testEcho() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new EchoHandler());
        String[] strings = {"Hello", "World"};
        String[] response = (String[]) Client.send(host, port, strings);
        assertTrue(response[0].equals("Hello"));
        simpleSocketServer.stopServer();
    }

    public void testDate() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new EchoDateHandler());
        Date serverDate = (Date) Client.send(host, port, (Object) null);
        long halfTripTimeMsec = (new Date()).getTime() - serverDate.getTime();
        System.out.println("Half Trip Time: " + halfTripTimeMsec);
        assertTrue(halfTripTimeMsec >= 0 && halfTripTimeMsec < 1000);
        simpleSocketServer.stopServer();
    }

    public void testNull() throws Exception {
        Server simpleSocketServer = Server.startServer(port, new EchoNullHandler());
        Date serverDate = (Date) Client.send(host, port, (Object) null);
        assertTrue(serverDate == null);
        simpleSocketServer.stopServer();
    }

}
