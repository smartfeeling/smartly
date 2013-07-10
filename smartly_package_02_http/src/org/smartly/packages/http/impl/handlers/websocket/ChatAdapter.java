package org.smartly.packages.http.impl.handlers.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 */
public class ChatAdapter extends WebSocketAdapter {

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        super.onWebSocketBinary(payload, offset, len);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        System.out.println("CONNECT: " + sess.toString());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        System.out.println("MESSAGE: " + message);
        try {
            Thread.sleep(5000);
            super.getRemote().sendStringByFuture("hello");
        } catch(Throwable t){
            System.out.println(t);
        }

    }
}
