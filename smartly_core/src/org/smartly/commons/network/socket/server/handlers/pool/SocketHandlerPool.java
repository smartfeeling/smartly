package org.smartly.commons.network.socket.server.handlers.pool;

import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.ISocketHandler;
import org.smartly.commons.util.CollectionUtils;

import java.util.*;

/**
 * Pool of Socket handlers
 */
public class SocketHandlerPool {

    private final List<Class<? extends ISocketFilter>> _filters;
    private final Map<String, Class<? extends ISocketHandler>> _handlers;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r s
    // --------------------------------------------------------------------

    public SocketHandlerPool() {
        _filters = Collections.synchronizedList(new LinkedList<Class<? extends ISocketFilter>>());
        _handlers = Collections.synchronizedMap(new HashMap<String, Class<? extends ISocketHandler>>());
    }

    public SocketHandlerPool(final Class<? extends ISocketFilter>[] handlers) {
        this();
        CollectionUtils.addAllNoDuplicates(_filters, handlers);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void clear() {
        synchronized (_filters) {
            _filters.clear();
        }
        synchronized (_handlers) {
            _handlers.clear();
        }
    }

    // --------------------------------------------------------------------
    //               FILTERS
    // --------------------------------------------------------------------

    public int sizeFilters() {
        return _filters.size();
    }


    public boolean hasFilters() {
        return !_filters.isEmpty();
    }


    public boolean hasFiler(final Class<ISocketFilter> o) {
        return _filters.contains(o);
    }

    public SocketFilterPoolIterator getFiltersIterator() {
        return new SocketFilterPoolIterator(this.getFilters());
    }

    public boolean addFilter(final Class<? extends ISocketFilter> o) {
        if (null != o) {
            synchronized (_filters) {
                return _filters.add(o);
            }
        }
        return false;
    }

    public void addFilters(final Class<? extends ISocketFilter>[] array) {
        if (null != array) {
            synchronized (_filters) {
                CollectionUtils.addAllNoDuplicates(_filters, array);
            }
        }
    }

    public boolean removeFilter(final Class<? extends ISocketFilter> o) {
        if (null != o) {
            synchronized (_filters) {
                return _filters.remove(o);
            }
        }
        return false;
    }

    // --------------------------------------------------------------------
    //               HANDLERS
    // --------------------------------------------------------------------

    public int sizeHandlers() {
        return _handlers.size();
    }

    public boolean hasHandlers() {
        return !_handlers.isEmpty();
    }

    public boolean hasHandler(final String key) {
        return _handlers.containsKey(key);
    }

    public void addHandler(final String key, final Class<? extends ISocketHandler> o) {
        if (null != o) {
            synchronized (_handlers) {
                _handlers.put(key, o);
            }
        }
    }

    public Class<? extends ISocketHandler> removeHandler(final String key) {
        if (null != key) {
            synchronized (_handlers) {
                return _handlers.remove(key);
            }
        }
        return null;
    }

    public ISocketHandler getHandler(final String key){
          synchronized (_handlers){
              return this.getHandlerInstance(_handlers.get(key));
          }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Class<? extends ISocketFilter>[] getFilters() {
        synchronized (_filters) {
            return _filters.toArray(new Class[_filters.size()]);
        }
    }

    private ISocketHandler getHandlerInstance(Class<? extends ISocketHandler> aclass) {
        try {
            if (null != aclass) {
                return aclass.newInstance();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
