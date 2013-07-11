package org.smartly.commons.network.socket.server.handlers;

import org.smartly.IConstants;

/**
 * Socket request Message
 */
public class SocketRequest {

    private final Object _data;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SocketRequest(final Object data) {
        _data = data;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

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
