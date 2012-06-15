/*
 * 
 */

package org.smartly.packages.mongo.impl;

/**
 * This is a managed standard exception that include a "code" field.
 *
 * @author angelo.geminiani
 */
public class StandardCodedException
        extends StandardException {

    public static final String ERROR_0_UNDEFINED = "0";
    // client
    public static final String ERROR_409 = "409";
    public static final String ERROR_401_CONNREFUSED = "401";   // connection refused
    public static final String ERROR_403_FORBIDDEN = "403";
    public static final String ERROR_404_PAGENOTFOUND = "404";
    public static final String ERROR_418_IAMTEAPOT = "418";
    // server
    public static final String ERROR_500_SERVERERROR = "500";
    public static final String ERROR_503_SERVICEUNAVAILABLE = "503";

    private String _code;
    

    public StandardCodedException() {
        _code = ERROR_0_UNDEFINED;
    }

    public StandardCodedException(final String code) {
        _code = code;
    }

    public StandardCodedException(final String code, final Object[] data) {
        super(data);
        _code = code;
    }

    public StandardCodedException(final String code, final String msg) {
        super(msg);
        _code = code;
    }

    public StandardCodedException(final String code, final Object[] data,
                                  final String msg) {
        super(data, msg);
        _code = code;
    }

    public StandardCodedException(final String code, final String msg,
                                  final Throwable cause) {
        super(msg, cause);
        _code = code;
    }

    public StandardCodedException(final String code, final Object[] data,
                                  final String msg,
                                  final Throwable cause) {
        super(data, msg, cause);
        _code = code;
    }

    public StandardCodedException(final String code, final Throwable cause) {
        super(cause);
        _code = code;
    }

    public StandardCodedException(final String code, final Object[] data,
                                  final Throwable cause) {
        super(data, cause);
        _code = code;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append("{");
        result.append("[code: ").append(_code).append("]");
        result.append(", ");
        result.append("[message: ").append(super.getMessage()).append("]");
        result.append(", ");
        result.append("[cause: ").append(super.getCause()).append("]");
        result.append(", ");
        result.append("[data: ").append(this.hasDataArray()?
            super.getDataArray().length:"0").append("]");
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
        final StandardCodedException other = (StandardCodedException) obj;
        if ((this._code == null) ? (other._code != null) : !this._code.equals(other._code)) {
            return false;
        }
        if ((super.getMessage() == null) ? (other.getMessage() != null) : !super.getMessage().equals(other.getMessage())) {
            return false;
        }
        if (super.getCause() != other.getCause() && (super.getCause() == null || !super.getCause().equals(other.getCause()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this._code != null ? this._code.hashCode() : 0);
        hash = 29 * hash + (super.getMessage() != null ? super.getMessage().hashCode() : 0);
        hash = 29 * hash + (super.getCause() != null ? super.getCause().hashCode() : 0);
        return hash;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        this._code = code;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
