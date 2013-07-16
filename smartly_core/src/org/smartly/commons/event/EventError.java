/*
 *
 */
package org.smartly.commons.event;

/**
 * @author
 */
public class EventError extends Event {

    private Throwable _error;

    public EventError(final Object sender,
                      final String name, final Object data, final Throwable error) {
        super(sender, name, data);
        _error = error;
    }

    public Throwable getError() {
        return _error;
    }

    public void setError(Throwable error) {
        this._error = error;
    }
}
