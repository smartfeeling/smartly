package org.smartly.packages.velocity.impl.vtools.lang;

import org.smartly.commons.util.StringUtils;

/**
 *
 */
public class VLCString {

    private StringBuilder _buffer;

    public VLCString() {
        _buffer = new StringBuilder();
    }

    @Override
    public String toString() {
        return _buffer.toString();
    }

    public String trim() {
        return this.toString().trim();
    }

    public String toUpperCase() {
        return this.toString().toUpperCase();
    }

    public String toLowerCase() {
        return this.toString().toLowerCase();
    }

    public String[] split(final String sep){
        return StringUtils.split(this.toString(), sep);
    }

    public void concat(final Object... args) {
        _buffer.append(StringUtils.concatArgs(args));
    }

    public void concatEx(final String sep, final Object... args) {
        if (_buffer.length() > 0) {
            _buffer.append(sep);
        }
        _buffer.append(StringUtils.concatArgsEx(sep, args));
    }

    public void concatDot(final Object... args) {
        if (_buffer.length() > 0) {
            _buffer.append(".");
        }
        _buffer.append(StringUtils.concatDot(args));
    }

    public void concatComma(final Object... args) {
        if (_buffer.length() > 0) {
            _buffer.append(",");
        }
        _buffer.append(StringUtils.concatArgsEx(",", args));
    }

    public void replaceCR(final Object with) {
        this.replace("\n", with);
    }

    public void replace(final Object what, final Object with) {
        if (null != what) {
            _buffer = new StringBuilder(StringUtils.replace(_buffer.toString(),
                    what.toString(),
                    null != with ? with.toString() : ""));
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
