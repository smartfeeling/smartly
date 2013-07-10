package org.smartly.packages.http.impl.handlers.websocket;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

/**
 */
@org.eclipse.jetty.websocket.api.annotations.WebSocket
public class ChatWebSocket {

    @OnWebSocketConnect
    public void onOpen(final Session connection) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("OPEN: " + connection.toString());
    }


    @OnWebSocketClose
    public void onClose(Session connection,
                        int statusCode, String reason) {
        // WebSocket is now disconnected
        System.out.println("CLOSE: " + reason);
    }

    @OnWebSocketMessage
    public void onTextMethod(Session connection,
                             String message) {
        // simple TEXT message received, with Connection
        // that it occurred on.
        System.out.println("MESSAGE: " + message);
        System.out.println(connection.toString());
    }

    @OnWebSocketMessage
    public void onBinaryMethod(Session connection,
                               byte data[], int offset,
                               int length) {
        // simple BINARY message received, with Connection
        // that it occurred on.
        System.out.println("BINARY");
    }
}
