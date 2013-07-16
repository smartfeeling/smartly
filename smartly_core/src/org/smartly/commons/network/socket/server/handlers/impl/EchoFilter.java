package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

/**
 * Just echo back the message that you receive. This Handler could be used
 * for testing that a communication channel is up and running.
 */
public class EchoFilter implements ISocketFilter {

    public boolean handle(final SocketRequest request, final SocketResponse response) {
        response.write(request.read());
        return true;
    }
}
