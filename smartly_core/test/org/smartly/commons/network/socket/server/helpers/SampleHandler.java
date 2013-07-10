package org.smartly.commons.network.socket.server.helpers;


import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;

import java.util.Date;

public class SampleHandler implements ISocketHandler {

    private Logger _logger;

    public SampleHandler() {
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public Object handle(final Object request) {
        _logger.info("SERVER: " + " (" + (new Date()).toString() + "): " + request);
        try {
            Thread.sleep(1000);
        } catch (Throwable ignored) {
        }
        return " (" + (new Date()).toString() + "): " + request.toString();
    }

}
