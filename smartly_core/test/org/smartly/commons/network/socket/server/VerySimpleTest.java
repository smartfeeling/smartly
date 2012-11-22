package org.smartly.commons.network.socket.server;

import junit.framework.TestCase;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;
import org.smartly.commons.network.socket.server.handler.impl.EchoHandler;

public class VerySimpleTest extends TestCase {

    private ISocketHandler _handler = new EchoHandler();
    private Server _server;

    public void setUp() throws Exception {
        _server = Server.startServer(_handler);
    }

    public void tearDown() {
        _server.stopServer();
    }

    public void testOne() throws Exception {
        String testString = "Hello";
        String response = Client.sendString(testString);
        assertEquals(response, testString);

         // try with many sync messages
        for(int i=0;i<1000;i++){
            String message = "msg: " + i;
            response = Client.sendString(message);
            assertEquals(response, message);
        }
    }

}
