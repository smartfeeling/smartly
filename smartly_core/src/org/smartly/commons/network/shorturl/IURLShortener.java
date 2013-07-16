/*
 * 
 */
package org.smartly.commons.network.shorturl;

/**
 * @author angelo.geminiani
 */
public interface IURLShortener {

    public abstract String getShortUrl(final String url) throws Exception;

}
