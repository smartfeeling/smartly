package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.Delegates;

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

    private static final Class EVENT_ON_TIMEOUT = Multipart.OnTimeOutListener.class;
    private static final Class EVENT_ON_FULL = Multipart.OnFullListener.class;
    private static final Class EVENT_ON_PART = Multipart.OnPartListener.class;

    //private final MultipartPoolEvents _events;
    private final Delegates.Handlers _eventHandlers;
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
        //_events = new MultipartPoolEvents();
        _eventHandlers = new Delegates.Handlers();

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
        //_events.clear();
        _eventHandlers.clear();
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

    public void onPart(final Multipart.OnPartListener listener) {
        _eventHandlers.add(listener);
    }

    public void onFull(final Multipart.OnFullListener listener) {
        _eventHandlers.add(listener);
    }

    public void onTimeOut(final Multipart.OnTimeOutListener listener) {
        _eventHandlers.add(listener);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void doOnTimeOut(final Multipart multipart) {
        _eventHandlers.triggerAsync(EVENT_ON_TIMEOUT, multipart);
    }

    private void doOnFull(final Multipart multipart) {
        // remove from data list
        synchronized (_data) {
            _data.remove(multipart.getUid());
        }
        // event
        _eventHandlers.triggerAsync(EVENT_ON_FULL, multipart);
    }

    private void doOnPart(final Multipart multipart, final MultipartMessagePart part) {
        // event
        _eventHandlers.triggerAsync(EVENT_ON_PART, multipart, part);
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
                    public void handle(final Multipart sender) {
                        doOnFull(sender);
                    }
                });
                multipart.onPart(new Multipart.OnPartListener() {
                    @Override
                    public void handle(final Multipart sender, final MultipartMessagePart part) {
                        doOnPart(sender, part);
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
