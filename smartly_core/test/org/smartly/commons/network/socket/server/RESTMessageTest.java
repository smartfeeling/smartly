package org.smartly.commons.network.socket.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.messages.multipart.Multipart;
import org.smartly.commons.network.socket.messages.rest.RESTMessage;
import org.smartly.commons.network.socket.server.handlers.impl.HandlerREST;
import org.smartly.commons.network.socket.server.helpers.SampleRESTMessage;
import org.smartly.commons.network.socket.server.helpers.SampleRESTService;
import org.smartly.commons.network.socket.server.tools.MultipartMessageUtils;
import org.smartly.commons.remoting.rest.RESTRegistry;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.util.ResourceBundle;

/**
 *
 */
public class RESTMessageTest {

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
        _simpleSocketServer = new Server(port);
        _simpleSocketServer.onStart(new Server.OnStart() {
            @Override
            public void handle(Server sender) {
                System.out.println("STARTED!!!!!");
            }
        });

        _simpleSocketServer.addHandler(HandlerREST.TYPE, HandlerREST.class);

        _simpleSocketServer.start();

        // register REST service
        RESTRegistry.register(SampleRESTService.class);
    }

    @After
    public void tearDown() throws Exception {
        _simpleSocketServer.stopServer();
    }

    @Test
    public void testMessage() throws Exception {

        final Client client = new Client();
        client.connect(host, port);

        final RESTMessage message = new RESTMessage();
        message.setPath("/test/all");

        final Object result = client.send(message);
        System.out.println(result);

    }


}
