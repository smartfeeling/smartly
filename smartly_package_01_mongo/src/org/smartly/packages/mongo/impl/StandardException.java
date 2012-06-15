/*
 * 
 */

package org.smartly.packages.mongo.impl;

/**
 * This is a managed standard exception.
 *
 * @author angelo.geminiani
 */
public class StandardException
        extends Exception {

    private String _message;
    private Throwable _cause;
    private Object[] _dataarray;

    public StandardException() {
        
    }

    public StandardException(final Object[] data) {
        _dataarray = data;
    }

    public StandardException(final String msg) {
        super(msg);
        _message = msg;
    }

    public StandardException(final Object[] data,
                             final String msg) {
        super(msg);
        _dataarray = data;
        _message = msg;
    }

    public StandardException(final String msg,
                             final Throwable cause) {
        super(msg, cause);
        _message = msg;
        _cause = cause;
    }

    public StandardException(final Object[] data,
                             final String msg,
                             final Throwable cause) {
        super(msg, cause);
        _dataarray = data;
        _message = msg;
        _cause = cause;
    }

    public StandardException(final Throwable cause) {
        super(cause);
        _cause = cause;
    }

    public StandardException(final Object[] data,
                             final Throwable cause) {
        super(cause);
        _dataarray = data;
        _cause = cause;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append("{");
        result.append("[message: ").append(super.getMessage()).append("]");
        result.append(", ");
        result.append("[cause: ").append(super.getCause()).append("]");
        result.append(", ");
        result.append("[data: ").append(this.hasDataArray()?_dataarray.length:"0").append("]");
        result.append("}");

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StandardException other = (StandardException) obj;
        if ((this._message == null) ? (other._message != null) : !this._message.equals(other._message)) {
            return false;
        }
        if (this._cause != other._cause && (this._cause == null || !this._cause.equals(other._cause))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this._message != null ? this._message.hashCode() : 0);
        hash = 29 * hash + (this._cause != null ? this._cause.hashCode() : 0);
        return hash;
    }

    public boolean hasDataArray() {
        return null!=_dataarray && _dataarray.length>0;
    }

    public Object[] getDataArray() {
        return _dataarray;
    }

    public void setDataArray(Object[] data) {
        this._dataarray = data;
    }

    @Override
    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    @Override
    public Throwable getCause() {
        return _cause;
    }

    public void setCause(Throwable cause) {
        _cause = cause;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
