package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.Server;

/**
 * Server Wrapper to expose only some properties and methods to request handlers
 */
public class SocketRequestServer {

    private final Server _server;

    public SocketRequestServer(Server server) {
        _server = server;
    }

    public void addMultipartMessagePart(final MultipartMessagePart part){
        _server.addMultipartMessagePart(part);
    }
}
