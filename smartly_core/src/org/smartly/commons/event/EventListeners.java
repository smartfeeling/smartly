/*
 * 
 */
package org.smartly.commons.event;


import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread safe Map of listener's list.
 *
 * @author
 */
public class EventListeners
        implements Serializable {

    private final List<IEventListener> _listeners;

    public EventListeners() {
        _listeners = Collections.synchronizedList(new LinkedList<IEventListener>());
    }

    @Override
    protected void finalize() throws Throwable {
        if (null != _listeners) {
            this.clear();
        }
        super.finalize();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append("[");
        result.append("Items: ").append(this.size());
        result.append("]");

        return result.toString();
    }

    public int size() {
        synchronized (_listeners) {
            return _listeners.size();
        }
    }

    public boolean isEmpty() {
        synchronized (_listeners) {
            return _listeners.isEmpty();
        }
    }

    public boolean contains(final IEventListener listener) {
        synchronized (_listeners) {
            return _listeners.contains(listener);
        }
    }

    public void add(IEventListener listener) {
        synchronized (_listeners) {
            if (!_listeners.contains(listener)) {
                _listeners.add(listener);
            }
        }
    }

    public void clear() {
        synchronized (_listeners) {
            _listeners.clear();
        }
    }

    public void remove(final IEventListener listener) {
        synchronized (_listeners) {
            _listeners.remove(listener);
        }
    }

    public void remove(final int index) {
        synchronized (_listeners) {
            _listeners.remove(index);
        }
    }

    public IEventListener[] toArray() {
        synchronized (_listeners) {
            return _listeners.toArray(new IEventListener[_listeners.size()]);
        }
    }

    public int count(final String key) {
        synchronized (_listeners) {
            return _listeners.size();
        }
    }

    public String getListenerSnapshot() {
        synchronized (_listeners) {
            return this.getSnapshot();
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getSnapshot() {
        int size = _listeners.size();

        StringBuilder result = new StringBuilder();
        result.append("Total listeners: ").append(size);
        try {
            for (final IEventListener listener : _listeners) {
                result.append("\n ");
                result.append("\t").append(listener.toString());
            }
        } catch (Throwable t) {
            Logger logger = this.getLogger();
            logger.log(Level.WARNING, "Error getting listeners snapshot.", t);

            result.append("ERROR [").append(t.toString()).append("]");
        }
        return result.toString();
    }
}
