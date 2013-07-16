/*
 * 
 */
package org.smartly.commons.network.shorturl.impl;

import org.smartly.commons.network.URLUtils;
import org.smartly.commons.network.shorturl.IURLShortener;
import org.smartly.commons.util.FormatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class TinyUrl
        implements IURLShortener {

    private static final String API_URL = "http://tinyurl.com/api-create.php?url={url}";

    @Override
    public String getShortUrl(final String url) throws Exception {
        return this.get(url);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private String get(final String surl) throws Exception {
        final String solvedurl = this.solve(surl);
        final String response = URLUtils.getUrlContent(solvedurl);
        return response.trim();
    }

    private String solve(final String url) {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("url", url);
        return FormatUtils.formatTemplate(API_URL, "{", "}", data);
    }
}
