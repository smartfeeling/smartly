package org.smartly.commons.network.socket.messages.multipart.util;

import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.network.socket.messages.multipart.Multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 *
 */
public class MultipartPoolEvents {

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

    private final Collection<OnFullListener> _listeners_full;
    private final Collection<OnTimeOutListener> _listeners_timeout;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartPoolEvents() {
        _listeners_full = Collections.synchronizedCollection(new ArrayList<OnFullListener>());
        _listeners_timeout = Collections.synchronizedCollection(new ArrayList<OnTimeOutListener>());
    }

    public void clear() {
        synchronized (_listeners_full) {
            _listeners_full.clear();
        }
        synchronized (_listeners_timeout) {
            _listeners_timeout.clear();
        }
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onFull(final MultipartPoolEvents.OnFullListener listener) {
        synchronized (_listeners_full) {
            _listeners_full.add(listener);
        }
    }

    public void onTimeOut(final MultipartPoolEvents.OnTimeOutListener listener) {
        synchronized (_listeners_timeout) {
            _listeners_timeout.add(listener);
        }
    }

    public void doOnTimeOut(final Multipart multipart) {
        synchronized (_listeners_timeout) {
            for (final OnTimeOutListener listener : _listeners_timeout) {
                Async.Action(new Delegates.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle(multipart);
                    }
                });
            }
        }
    }

    public void doOnFull(final Multipart multipart) {
        // event
        synchronized (_listeners_full) {
            for (final OnFullListener listener : _listeners_full) {
                Async.Action(new Delegates.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle(multipart);
                    }
                });
            }
        }
    }

}
