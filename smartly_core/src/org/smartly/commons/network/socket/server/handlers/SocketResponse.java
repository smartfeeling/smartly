package org.smartly.commons.network.socket.server.handlers;

/**
 * Response to write to socket
 */
public class SocketResponse {

    private Object _data;
    private boolean _canContinue;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r s
    // --------------------------------------------------------------------

    public SocketResponse() {
        _canContinue = true;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public boolean canHandle() {
        return _canContinue;
    }

    public void stopHandle() {
        _canContinue = false;
    }

    public void write(final Object data) {
        _data = data;
    }

    public Object read() {
        return _data;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

}
