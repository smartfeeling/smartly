/*
 * 
 */
package org.smartly.packages.http.impl.util;

import org.smartly.commons.util.MimeTypeUtils;

/**
 * @author angelo.geminiani
 */
public class BinaryData {

    private byte[] _bytes;
    private String _mimetype;

    public BinaryData() {
        _mimetype = MimeTypeUtils.MIME_IMAGEPNG;
        _bytes = new byte[0];
    }

    public BinaryData(final String type) {
        _mimetype = MimeTypeUtils.getMimeType(type);
        _bytes = new byte[0];
    }

    public BinaryData(final byte[] bytes) {
        _mimetype = MimeTypeUtils.MIME_IMAGEPNG;
        _bytes = bytes;
    }

    public BinaryData(final byte[] bytes, final String type) {
        _mimetype = MimeTypeUtils.getMimeType(type);
        _bytes = bytes;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName());
        result.append("{");
        result.append("size: ").append(this.size());
        result.append(", ");
        result.append("type: ").append(_mimetype);
        result.append("}");
        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public byte[] getBytes() {
        return _bytes;
    }

    public void setBytes(byte[] bytes) {
        this._bytes = bytes;
    }

    public String getMimetype() {
        return _mimetype;
    }

    public void setMimetype(String mimetype) {
        this._mimetype = MimeTypeUtils.getMimeType(mimetype);
    }

    public int size() {
        return null != _bytes ? _bytes.length : 0;
    }
}
