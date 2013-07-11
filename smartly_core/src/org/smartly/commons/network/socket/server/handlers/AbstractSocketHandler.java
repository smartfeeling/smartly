package org.smartly.commons.network.socket.server.handlers;

import org.smartly.IConstants;

/**
 *
 */
public abstract class AbstractSocketHandler
        implements ISocketHandler {

    private String _type = IConstants.NULL;


    @Override
    public String getType() {
        return _type;
    }

    @Override
    public void setType(final String type) {
        _type = type;
    }

    public void setType(final Class type) {
        _type = type.getName();
    }


    @Override
    public abstract void handle(SocketRequest request, SocketResponse response);

}
