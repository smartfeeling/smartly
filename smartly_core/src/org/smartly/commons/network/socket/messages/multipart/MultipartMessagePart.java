package org.smartly.commons.network.socket.messages.multipart;

/**
 * Single Part of a multipart message
 */
public class MultipartMessagePart {

    private String _uid;
    private MultipartInfo _info;
    private Object _data;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartMessagePart() {
        _info = new MultipartInfo();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getUid() {
        return _uid;
    }

    void setUid(final String uid) {
        _uid = uid;
    }

    public Object getData() {
        return _data;
    }

    public void setData(final Object data) {
        _data = data;
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
            return _info.getLength();
        }
        return 0;
    }

}
