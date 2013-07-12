package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

/**
 *
 */
public class MultipartMessageHandler extends AbstractSocketHandler {

    public static final String TYPE = MultipartMessagePart.class.getName();

    @Override
    public void handle(final SocketRequest request, final SocketResponse response) {
        // add part to server pool
        if(request.isTypeOf(MultipartMessagePart.class)){
           request.getServer().addMultipartMessagePart((MultipartMessagePart)request.read());
        }
    }
}
