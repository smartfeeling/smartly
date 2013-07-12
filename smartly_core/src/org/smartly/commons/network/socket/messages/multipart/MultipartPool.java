package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.async.Async;

import java.util.*;

/**
 * Repository for Multipart message containers.
 * This pool checks for Timeouts in Multipart and raise Event when multipart is Full and ready.
 */
public class MultipartPool {

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnFullListener {
        public void handle(Multipart sender);
    }

    public static interface OnTimeOutListener {
        public void handle(Multipart sender);
    }

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private static final int DEFAULT_TIMEOUT = 60 * 2 * 1000; // two minute timeout

    private final Collection<OnFullListener> _listeners_full;
    private final Collection<OnTimeOutListener> _listeners_timeout;

    private final Map<String, Multipart> _data;
    private PoolGarbageCollector _gc;

    private int _timeOut;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartPool() {
        this(DEFAULT_TIMEOUT);
    }

    public MultipartPool(int timeOut) {
        _timeOut = timeOut;
        _data = Collections.synchronizedMap(new HashMap<String, Multipart>());
        _listeners_full = Collections.synchronizedCollection(new ArrayList<OnFullListener>());
        _listeners_timeout = Collections.synchronizedCollection(new ArrayList<OnTimeOutListener>());

        //-- gc for current pool --//
        _gc = new PoolGarbageCollector(this);
        _gc.start();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _gc.interrupt();
            _gc = null;
            synchronized (_listeners_full) {
                _listeners_full.clear();
            }
            synchronized (_listeners_timeout) {
                _listeners_timeout.clear();
            }
            synchronized (_data) {
                _data.clear();
            }
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public int size() {
        synchronized (_data) {
            return _data.size();
        }
    }

    public void setTimeout(final int timeOut) {
        _timeOut = timeOut;
    }

    public int getTimeout() {
        return _timeOut;
    }

    /**
     * Add part into pool and returns pool size.
     * @param part Part to add
     * @return Pool Size.
     */
    public void add(final MultipartMessagePart part) {
        if (null != part) {
            this.addPart(part);
        }
        this.size();
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onFull(final OnFullListener listener) {
        synchronized (_listeners_full) {
            _listeners_full.add(listener);
        }
    }

    public void onTimeOut(final OnTimeOutListener listener) {
        synchronized (_listeners_timeout) {
            _listeners_timeout.add(listener);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void doOnTimeOut(final Multipart multipart) {
        synchronized (_listeners_timeout) {
            for (final OnTimeOutListener listener : _listeners_timeout) {
                Async.Action(new Async.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle(multipart);
                    }
                });
            }
        }
    }

    private void doOnFull(final Multipart multipart) {
        // remove from data list
        synchronized (_data) {
            _data.remove(multipart.getUid());
        }
        // event
        synchronized (_listeners_full) {
            for (final OnFullListener listener : _listeners_full) {
                Async.Action(new Async.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle(multipart);
                    }
                });
            }
        }
    }

    private Multipart addPart(final MultipartMessagePart part) {
        synchronized (_data) {
            final String key = part.getUid();
            if (!_data.containsKey(key)) {
                // new multipart container
                final Multipart multipart = new Multipart(key, part.getPartCount());
                _data.put(key, multipart);
                multipart.onFull(new Multipart.OnFullListener() {
                    @Override
                    public void handle(Multipart sender) {
                        doOnFull(sender);
                    }
                });
            }
            final Multipart multipart = _data.get(key);
            if (null != multipart) {
                multipart.add(part);
            }
            return multipart;
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C  -  E M B E D D E D
    // --------------------------------------------------------------------

    private static class PoolGarbageCollector extends Thread {

        private final MultipartPool _multipartPool;
        private final Map<String, Multipart> _pool;

        public PoolGarbageCollector(final MultipartPool multipartPool) {
            _multipartPool = multipartPool;
            _pool = multipartPool._data;
            super.setPriority(Thread.NORM_PRIORITY);
            super.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (!super.isInterrupted()) {
                    Thread.sleep(this.getTimeout());
                    try {
                        this.garbage();
                    } catch (Throwable ignored) {
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }

        // ------------------------------------------------------------------------
        //                      p r i v a t e
        // ------------------------------------------------------------------------

        private int getTimeout() {
            return _multipartPool._timeOut;
        }

        private void doTimeout(final Multipart multipart) {
            _multipartPool.doOnTimeOut(multipart);
        }

        private void garbage() {
            synchronized (_pool) {
                final Collection<Multipart> items = _pool.values();
                for (final Multipart multipart : items) {
                    if (multipart.isExpired(this.getTimeout())) {
                        _pool.remove(multipart.getUid());
                        this.doTimeout(multipart);
                    }
                }
            }
        }

    }

}
