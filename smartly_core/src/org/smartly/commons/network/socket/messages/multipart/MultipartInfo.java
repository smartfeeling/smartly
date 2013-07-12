package org.smartly.commons.network.socket.messages.multipart;

/**
 *
 */
public class MultipartInfo {

    public static enum MultipartInfoType {
        File,
        String
    }

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private MultipartInfoType _type;
    private String _partName;
    private String _parentName;
    private int _index;
    private int _length;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MultipartInfo() {
        _type = MultipartInfoType.File;
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
    }

    //-----------------------------------------------
    //             o v e r r i d e
    //-----------------------------------------------

    public String ToString() {
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

    public MultipartInfo setType(MultipartInfoType _type) {
        _type = _type;
        return this;
    }

    public String getPartName() {
        return _partName;
    }

    public MultipartInfo setPartName(String _partName) {
        _partName = _partName;
        return this;
    }

    public String getParentName() {
        return _parentName;
    }

    public MultipartInfo setParentName(String _parentName) {
        _parentName = _parentName;
        return this;
    }

    public int getIndex() {
        return _index;
    }

    public MultipartInfo setIndex(int _index) {
        _index = _index;
        return this;
    }

    public int getLength() {
        return _length;
    }

    public MultipartInfo setLength(int _length) {
        _length = _length;
        return this;
    }

    public int getPosition() {
        return this.getIndex() + 1;
    }

}
