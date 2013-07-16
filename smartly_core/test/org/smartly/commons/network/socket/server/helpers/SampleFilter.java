package org.smartly.commons.network.socket.server.helpers;


import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

import java.util.Date;

public class SampleFilter implements ISocketFilter {

    private Logger _logger;

    public SampleFilter() {
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public boolean handle(final SocketRequest request, final SocketResponse response) {
        _logger.info("SERVER: " + " (" + (new Date()).toString() + "): " + request.read());
        try {
            Thread.sleep(1000);
        } catch (Throwable ignored) {
        }
        final String txt = " (" + (new Date()).toString() + "): " + request.read().toString();
        response.write(txt);
        return true;
    }

}
