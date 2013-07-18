package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

import java.util.Date;

public class FilterEchoString implements ISocketFilter {

    public boolean handle(final SocketRequest request, final SocketResponse response) {
        final String txt = this.getClass().getSimpleName() + " (" + (new Date()).toString() + "): " + request.read().toString();
        response.write(txt);
        return true;
    }
}
