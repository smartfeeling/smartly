package org.smartly.commons.network.socket.messages;

import org.smartly.commons.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public abstract class AbstractMessage
        implements Serializable {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private String _userToken;

    private long _creationDate = System.currentTimeMillis();

    // ------------------------------------------------------------------------
    //                      o v e r r i d e s
    // ------------------------------------------------------------------------

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("{");
        sb.append("CreationDate: ").append(new Date(this.getCreationDate()));
        sb.append(", ");
        sb.append("Elapsed: ").append(this.getElapsedTime());
        sb.append(", ");
        sb.append("UserToken: ").append(this.getUserToken());
        sb.append("}");

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r o p e r t i e s
    // ------------------------------------------------------------------------

    public long getCreationDate() {
        return _creationDate;
    }

    public double getElapsedTime(){
        return DateUtils.dateDiff(new Date(System.currentTimeMillis()), new Date(_creationDate), DateUtils.MILLISECOND);
    }

    public String getUserToken() {
        return _userToken;
    }

    public void setUserToken(final String value) {
        _userToken = value;
    }

}
