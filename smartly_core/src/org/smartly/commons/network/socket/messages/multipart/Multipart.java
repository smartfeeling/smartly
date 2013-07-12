package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.async.Async;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Multipart Message aggregator.
 */
public class Multipart {

    public static interface OnFullListener {
        public void handle(Multipart sender);
    }

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Collection<OnFullListener> _listeners;

    private int _capacity;

    private final String _uid;

    private final Date _creationDate;

    private final Collection<MultipartMessagePart> _list;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Multipart(final int capacity) {
        this(null, capacity);
    }

    public Multipart(final String uid, final int capacity) {
        _uid = StringUtils.hasText(uid) ? uid : GUID.create();
        _creationDate = DateUtils.now();
        _list = Collections.synchronizedCollection(new ArrayList<MultipartMessagePart>(capacity));
        _listeners = Collections.synchronizedCollection(new ArrayList<OnFullListener>());
        _capacity = capacity;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Multipart) &&
                _uid.equalsIgnoreCase(((Multipart) obj).getUid());
    }

    @Override
    public int hashCode() {
        return _uid.hashCode();
    }

// --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    public boolean isFull() {
        return _capacity == _list.size();
    }

    public void add(final MultipartMessagePart part) {
        synchronized (_list) {
            if (!_list.contains(part) && !this.isFull()) {
                // add uid to part
                part.setUid(this.getUid());
                // add part to internal list
                _list.add(part);
                // check if full
                this.checkCapacity();
            }
        }
    }

    public int count() {
        synchronized (_list) {
            return _list.size();
        }
    }

    public int getCapacity() {
        return _capacity;
    }

    public MultipartMessagePart[] getParts() {
        synchronized (_list) {
            return _list.toArray(new MultipartMessagePart[_list.size()]);
        }
    }

    public double getAliveTime() {
        return DateUtils.dateDiff(DateUtils.now(), _creationDate, DateUtils.MILLISECOND);
    }

    public boolean isExpired(final long millisecondsTimeout) {
        return this.getAliveTime() < millisecondsTimeout;
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onFull(final OnFullListener listener) {
        synchronized (_listeners) {
            _listeners.add(listener);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void checkCapacity() {
        if (_list.size() >= _capacity) {
            //-- call event listeners --//
            this.doOnFull();
        }
    }

    private void doOnFull(){
        synchronized (_listeners) {
            for (final OnFullListener listener : _listeners) {
                Async.Action(new Async.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle((Multipart) args[0]);
                    }
                }, this);
            }
        }
    }
}
