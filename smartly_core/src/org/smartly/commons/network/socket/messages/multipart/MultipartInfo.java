package org.smartly.commons.network.socket.messages.multipart;

import org.smartly.commons.lang.CharEncoding;

import java.io.Serializable;

/**
 *
 */
public final class MultipartInfo
        implements Serializable {

    public static enum MultipartInfoType {
        File,
        String
    }

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private String _parentName;
    private MultipartInfoType _type;
    private String _partName;
    private String _encoding;
    private int _index;
    private int _length;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartInfo() {
        _type = MultipartInfoType.File;
        _encoding = CharEncoding.UTF_8;
    }

    public MultipartInfo(final String parentName,
                         final MultipartInfoType type,
                         final String partName,
                         final int index,
                         final int length) {
        _type = type;
        _parentName = parentName;
        _partName = partName;
        _index = index;
        _length = length;
        _encoding = CharEncoding.UTF_8;
    }

    //-----------------------------------------------
    //             o v e r r i d e
    //-----------------------------------------------

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("Index: ").append(this.getIndex()).append(", ");
        sb.append("Position: ").append(this.getPosition()).append(", ");
        sb.append("Lenght: ").append(this.getLength()).append(", ");
        sb.append("ParentType: ").append(this.getType()).append(", ");
        sb.append("ParentName: ").append(this.getParentName()).append(", ");
        sb.append("PartName: ").append(this.getPartName());
        sb.append("}");
        return sb.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public MultipartInfoType getType() {
        return _type;
    }

    public MultipartInfo setType(final MultipartInfoType value) {
        _type = value;
        return this;
    }

    public String getEncoding() {
        return _encoding;
    }

    public MultipartInfo setEncoding(final String value) {
        _encoding = value;
        return this;
    }

    public String getPartName() {
        return _partName;
    }

    public MultipartInfo setPartName(final String value) {
        _partName = value;
        return this;
    }

    public String getParentName() {
        return _parentName;
    }

    public MultipartInfo setParentName(final String value) {
        _parentName = value;
        return this;
    }

    public int getIndex() {
        return _index;
    }

    public MultipartInfo setIndex(final int value) {
        _index = value;
        return this;
    }

    public int getLength() {
        return _length;
    }

    public MultipartInfo setLength(final int value) {
        _length = value;
        return this;
    }

    public int getPosition() {
        return this.getIndex() + 1;
    }

}
