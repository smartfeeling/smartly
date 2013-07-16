package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.network.socket.messages.multipart.util.MultipartPoolEvents;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for Multipart message containers.
 * This pool checks for Timeouts in Multipart and raise Event when multipart is Full and ready.
 */
public class MultipartPool {

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private static final int DEFAULT_TIMEOUT = 60 * 2 * 1000; // two minute timeout

    private final MultipartPoolEvents _events;
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
        _events = new MultipartPoolEvents();

        //-- gc for current pool --//
        _gc = new PoolGarbageCollector(this);
        _gc.start();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _gc.interrupt();
            _gc = null;
            this.clear();
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

    public void clear() {
        _events.clear();
        synchronized (_data) {
            _data.clear();
        }
    }

    /**
     * Add part into pool and returns pool size.
     *
     * @param part Part to add
     * @return Pool Size.
     */
    public void add(final MultipartMessagePart part) {
        this.add(part, null);
    }

    /**
     * Add part into pool and returns pool size.
     *
     * @param part     Part to Add
     * @param userData Custom data to pass to Multipart container
     */
    public void add(final MultipartMessagePart part, final Object userData) {
        if (null != part) {
            this.addPart(part, userData);
        }
        this.size();
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onFull(final MultipartPoolEvents.OnFullListener listener) {
        _events.onFull(listener);
    }

    public void onTimeOut(final MultipartPoolEvents.OnTimeOutListener listener) {
        _events.onTimeOut(listener);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void doOnTimeOut(final Multipart multipart) {
        _events.doOnTimeOut(multipart);
    }

    private void doOnFull(final Multipart multipart) {
        // remove from data list
        synchronized (_data) {
            _data.remove(multipart.getUid());
        }
        // event
        _events.doOnFull(multipart);
    }

    private Multipart addPart(final MultipartMessagePart part, final Object userData) {
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
                multipart.setUserData(userData);
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
