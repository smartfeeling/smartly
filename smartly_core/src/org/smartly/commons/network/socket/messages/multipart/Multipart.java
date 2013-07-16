package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.util.CompareUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.StringUtils;

import java.util.*;

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
    private final String _uid;
    private final Date _creationDate;
    private final List<MultipartMessagePart> _list;

    private int _capacity;
    private Object _userData; // custom data

    //-- readonly from part --//
    private MultipartInfo.MultipartInfoType _type;
    private String _name;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Multipart(final int capacity) {
        this(null, capacity);
    }

    public Multipart(final String uid, final int capacity) {
        _uid = StringUtils.hasText(uid) ? uid : GUID.create();
        _creationDate = DateUtils.now();
        _list = Collections.synchronizedList(new ArrayList<MultipartMessagePart>(capacity));
        _listeners = Collections.synchronizedCollection(new ArrayList<OnFullListener>());
        _capacity = capacity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("UID:").append(this.getUid());
        sb.append(", ");
        sb.append("Type:").append(this.getType());
        sb.append(", ");
        sb.append("Alive Time:").append(this.getAliveTime());
        sb.append(", ");
        sb.append("Part Count:").append(this.count());
        sb.append(", ");
        sb.append("Capacity:").append(this.getCapacity());
        sb.append(", ");
        sb.append("Is Full:").append(this.isFull());
        sb.append(", ");
        sb.append("Name:").append(this.getName());
        sb.append("}");

        return sb.toString();
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

    @Override
    protected void finalize() throws Throwable {
        try {
            _userData = null;
            _listeners.clear();
            _list.clear();
        } catch (Throwable t) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    public Object getUserData() {
        return _userData;
    }

    public void setUserData(final Object value) {
        if (null != value) {
            _userData = value;
        }
    }

    public MultipartInfo.MultipartInfoType getType() {
        return _type;
    }

    public String getName() {
        return _name;
    }

    public boolean isType(final MultipartInfo.MultipartInfoType type) {
        return CompareUtils.equals(_type, type);
    }

    public boolean isTypeString() {
        return this.isType(MultipartInfo.MultipartInfoType.String);
    }

    public boolean isTypeFile() {
        return this.isType(MultipartInfo.MultipartInfoType.File);
    }

    public int getCapacity() {
        return _capacity;
    }

    public boolean hasError() {
        synchronized (_list) {
            for (final MultipartMessagePart part : _list) {
                if (part.hasError()) {
                    return true;
                }
            }
            return false;
        }
    }

    public Throwable getError() {
        synchronized (_list) {
            for (final MultipartMessagePart part : _list) {
                if (part.hasError()) {
                    return part.getError();
                }
            }
            return null;
        }
    }

    public MultipartMessagePart[] getParts() {
        synchronized (_list) {
            Collections.sort(_list);
            return _list.toArray(new MultipartMessagePart[_list.size()]);
        }
    }

    public String[] getPartNames() {
        synchronized (_list) {
            final List<String> result = new LinkedList<String>();
            Collections.sort(_list);
            for (final MultipartMessagePart part : _list) {
                result.add(part.getInfo().getPartName());
            }
            return result.toArray(new String[result.size()]);
        }
    }

    public double getAliveTime() {
        return DateUtils.dateDiff(DateUtils.now(), _creationDate, DateUtils.MILLISECOND);
    }

    public boolean isExpired(final long millisecondsTimeout) {
        return this.getAliveTime() < millisecondsTimeout;
    }

    public boolean isFull() {
        return _capacity == _list.size();
    }

    public int count() {
        synchronized (_list) {
            return _list.size();
        }
    }

    public void add(final MultipartMessagePart part) {
        synchronized (_list) {
            if (!_list.contains(part) && !this.isFull()) {
                // add uid to part
                part.setUid(this.getUid());
                // add part to internal list
                _list.add(part);
                // set parent properties from part
                this.setProperties(part);
                // check if full
                this.checkCapacity();
            }
        }
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

    private void doOnFull() {
        synchronized (_listeners) {
            for (final OnFullListener listener : _listeners) {
                Async.Action(new Delegates.AsyncActionHandler() {
                    @Override
                    public void handle(Object... args) {
                        listener.handle((Multipart) args[0]);
                    }
                }, this);
            }
        }
    }

    private void setProperties(final MultipartMessagePart part) {
        if (null != part) {
            if (null == _type) {
                _type = part.getInfo().getType();
            }
            if (null == _name) {
                _name = part.getInfo().getParentName();
            }
        }
    }
}
