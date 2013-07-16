package org.smartly.commons.network.socket.server.handlers;

import org.smartly.IConstants;
import org.smartly.commons.network.socket.server.handlers.impl.SocketRequestServer;

/**
 * Socket request Message
 */
public class SocketRequest {

    private final SocketRequestServer _server;
    private final Object _data;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SocketRequest(final SocketRequestServer server, final Object data) {
        _data = data;
        _server = server;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public SocketRequestServer getServer() {
        return _server;
    }

    public boolean isTypeOf(final Class aclass) {
        if (null != aclass && null != _data) {
            return aclass.equals(this.getTypeClass());
        }
        return false;
    }

    public Class getTypeClass() {
        if (null != _data) {
            return _data.getClass();
        }
        return null;
    }

    public String getType() {
        if (null != _data) {
            return _data.getClass().getName();
        }
        return IConstants.NULL;
    }

    public Object read() {
        return _data;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


}
