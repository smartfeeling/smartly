package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

import java.util.Date;

public class FilterEchoDate implements ISocketFilter {

    public boolean handle(final SocketRequest request, final SocketResponse response) {
        response.write(new Date());
        return true;
    }
}
