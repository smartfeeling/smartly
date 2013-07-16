package org.smartly.commons.mutex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Main mutex dispenser.
 * Mutex are objects you can synchronize using "synchronize" keyword.
 * <code>synchronize(mymutex){... code ...}</code>
 * <p/>
 * To retrieve a mutex object call get().
 */
public class MutexPool {

    private static final long TIMEOUT = 30 * 1000;

    private final Map<Object, Mutex> _pool;
    private MutexGarbageCollector _gc;

    public MutexPool() {
        _pool = Collections.synchronizedMap(new HashMap<Object, Mutex>());
        _gc = new MutexGarbageCollector(this);
        _gc.start();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Throwable ignored) {
        } finally {
            super.finalize();
        }
    }

    public void close() {
        synchronized (_pool) {
            _pool.clear();
            if (null != _gc) {
                _gc.interrupt();
                _gc = null;
            }
        }
    }

    public Mutex get(final Object key) {
        synchronized (_pool) {
            if (null != _gc) {
                if (!_pool.containsKey(key)) {
                    _pool.put(key, new Mutex(TIMEOUT));
                }
                return _pool.get(key).wakeUp();
            } else {
                // never null
                return new Mutex(TIMEOUT);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


    private static class MutexGarbageCollector extends Thread {

        private final MutexPool _mutexPool;

        public MutexGarbageCollector(final MutexPool pool) {
            _mutexPool = pool;
            super.setPriority(Thread.NORM_PRIORITY);
            super.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!super.isInterrupted()) {
                    Thread.sleep(TIMEOUT);
                    this.garbage();
                }
            } catch (InterruptedException ignored) {
            }
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private void garbage() {
            synchronized (_mutexPool._pool) {
                final Iterator<Object> i = _mutexPool._pool.keySet().iterator();
                while (i.hasNext()) {
                    final Object key = i.next();
                    final Mutex mutex = _mutexPool._pool.get(key);
                    if (mutex.isExpired()) {
                        i.remove();
                        // System.out.println("removed: " + mutex);
                    }
                }
            }
        }

    }
}
