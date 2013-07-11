package org.smartly.commons.network.socket.server.helpers;


import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;

public class FooFilter implements ISocketFilter {

    private Logger _logger;

    public FooFilter() {
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public boolean handle(final SocketRequest request, final SocketResponse response) {
        // do nothing
        _logger.info("Foo handler is ignored");
        return false;
    }

}
