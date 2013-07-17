/*
 * 
 */
package org.smartly.commons.remoting.rpc.util;

import org.smartly.commons.logging.Level;
import org.smartly.commons.util.DateWrapper;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.remoting.rpc.RemoteService;

import java.util.Locale;

/**
 * system REST service
 * <p/>
 * http://localhost/rest/formatter/format?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=IT&param3=19680121
 *
 * @author angelo.geminiani
 */
public class RSFormatter
        extends RemoteService {

    public static final String NAME = "formatter";

    public RSFormatter() {
        super(NAME);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Format a date passed in iso (yyyyMMdd) format.
     *
     * @param authToken
     * @param country   i.e. "IT"
     * @param isoDate   (yyyyMMdd)
     * @return Formatted Date. i.e. "21/01/1968"
     */
    public String formatDate(final String authToken,
                             final String country,
                             final String isoDate) {
        if (super.isValidToken(authToken)) {
            try {
                final Locale locale = LocaleUtils.getLocaleByCountry(country);
                final DateWrapper dt = new DateWrapper(isoDate, DateWrapper.DATEFORMAT_DEFAULT);
                return dt.toString(locale);
            } catch (Throwable t) {
                super.getLogger().log(Level.SEVERE, null, t);
            }
        }
        return "";
    }
}
