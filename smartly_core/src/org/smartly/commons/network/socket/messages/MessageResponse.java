package org.smartly.commons.network.socket.messages;

import java.io.Serializable;

/**
 * Returning message wrapper
 */
public class MessageResponse
        extends AbstractMessage {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private Serializable _data;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public MessageResponse() {

    }

    public MessageResponse(final Serializable data) {
        _data = data;
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public void setData(final Object value) {
        if (value instanceof Serializable) {
            this.setData((Serializable) value);
        }
    }

    public void setData(final Serializable value) {
        _data = value;
    }

    public Serializable getData() {
        return _data;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public boolean isNull(){
        return null == _data;
    }

    public boolean isError() {
        return (_data instanceof Throwable);
    }

    public Throwable getError() {
        if (_data instanceof Throwable) {
            return (Throwable) _data;
        }
        return null;
    }

}
