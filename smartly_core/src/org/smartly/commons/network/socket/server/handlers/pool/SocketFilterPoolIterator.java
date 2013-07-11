package org.smartly.commons.network.socket.server.handlers.pool;

import org.smartly.commons.network.socket.server.handlers.ISocketFilter;

/**
 *
 */
public class SocketFilterPoolIterator {

    private final Class<? extends ISocketFilter>[] _items;

    private int _index;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public SocketFilterPoolIterator(final Class<? extends ISocketFilter>[] items) {
        _items = items;
        _index = 0;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public boolean hasNext() {
        return (_index + 1) < _items.length;
    }

    public ISocketFilter next() {
        if (this.hasNext()) {
            _index++;
            return getInstance(_items[_index]);
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private ISocketFilter getInstance(Class<? extends ISocketFilter> aclass) {
        try {
            if (null != aclass) {
                return aclass.newInstance();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

}
