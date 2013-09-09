package org.smartly.commons.pools;

import org.smartly.commons.Delegates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Blocking Pool of objects.<br/>
 * Call method "lock" to get an item from pool.<br/>
 * Call method "release" to return item to pool.<br/>
 * When pool is empty, a thread will loop until next item is released.
 */
public class GenericPool<T> {


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Object _syncObj;

    private final List<T> _pool;


    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public GenericPool(final T... items) {
        this(0, null); // unlimited pool
        if (null != items && items.length > 0) {
            this.init(items);
        }
    }

    public GenericPool(final int capacity,
                       final Delegates.Function<T> callback) {
        _syncObj = new Object();
        _pool = Collections.synchronizedList(new ArrayList<T>(capacity));

        this.init(capacity, callback);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public int size() {
        synchronized (_pool) {
            return _pool.size();
        }
    }

    public T lock() {
        synchronized (_syncObj) {
            while (this.size() == 0) {
                try {
                    Thread.sleep(100);
                } catch (Throwable ignored) {
                    return null;
                }
            }
            return this.firstItem();
        }
    }

    public void release(final T item) {
        this.putItem(item);
    }

    public T[] clear() {
        synchronized (_syncObj) {
            T[] result = (T[]) new Object[_pool.size()];
            int i = 0;
            for (final T item : _pool) {
                result[i] = item;
                i++;
            }
            _pool.clear();
            return result;
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init(final T... items) {
        synchronized (_pool) {
            _pool.addAll(Arrays.asList(items));
        }
    }

    private void init(final int capacity, final Delegates.Function<T> callback) {
        if (null != callback) {
            synchronized (_pool) {
                for (int i = 0; i < capacity; i++) {
                    final T item = callback.handle(i);
                    if (null != item) {
                        _pool.add(item);
                    }
                }
            }
        }
    }

    private void putItem(final T item) {
        synchronized (_pool) {
            _pool.add(item);
        }
    }

    public T firstItem() {
        synchronized (_pool) {
            return _pool.remove(0);
        }
    }
}
