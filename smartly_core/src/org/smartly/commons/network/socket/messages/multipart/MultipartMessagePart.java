package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.network.socket.messages.AbstractMessage;

/**
 * Single Part of a multipart message
 */
public class MultipartMessagePart extends AbstractMessage
        implements Comparable<MultipartMessagePart> {

    private String _uid;
    private MultipartInfo _info;
    private byte[] _data;
    private Throwable _error;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartMessagePart() {
        _info = new MultipartInfo();
        _data = new byte[0];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("UID: ").append(_uid);
        sb.append(", ");
        sb.append("Info: ").append(_info);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int compareTo(final MultipartMessagePart o) {
        if (null != o) {
            if (o.getInfo().getIndex() == this.getInfo().getIndex()) {
                return 0;
            }
            return o.getInfo().getIndex() > this.getInfo().getIndex() ? -1 : 1;
        }
        return -1;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    public void setUid(final String uid) {
        _uid = uid;
    }

    public void setError(final Throwable value) {
        _error = value;
    }

    public Throwable getError() {
        return _error;
    }

    public boolean hasError() {
        return null != _error;
    }

    public byte[] getData() {
        return _data;
    }

    public void setData(final byte[] data) {
        _data = data;
        if (null != data && null != _info) {
            _info.setPartLength(data.length);
        }
    }

    public void clearData() {
        _data = null;
        this.setData(new byte[0]);
    }

    public MultipartInfo getInfo() {
        return _info;
    }

    public void setInfo(MultipartInfo info) {
        _info = info;
    }

    public int getPartIndex() {
        if (null != _info) {
            return _info.getIndex();
        }
        return 0;
    }

    public int getPartCount() {
        if (null != _info) {
            return _info.getCount();
        }
        return 0;
    }

    public boolean hasData() {
        return null != this.getData() && this.getData().length > 0;
    }

}
