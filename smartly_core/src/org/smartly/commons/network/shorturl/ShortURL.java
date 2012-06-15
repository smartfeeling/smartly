/*
 * 
 */
package org.smartly.commons.network.shorturl;

import org.smartly.commons.network.shorturl.impl.TinyUrl;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class ShortURL {

    public static final String TINYURL = "tinyurl";
    private static final String DEFAULT_SERVICE = TINYURL;
    private final Map<String, Class<? extends IURLShortener>> _services;

    private ShortURL() {
        _services = Collections.synchronizedMap(
                new HashMap<String, Class<? extends IURLShortener>>());
        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _services.clear();
        } catch (Exception ignored) {
        }
        super.finalize();
    }

    public final String getShortUrl(final String serviceId, final String url) {
        try {
            return tryShortUrl(serviceId, url);
        } catch (Exception e) {
            return this.tryShortUrl(url);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void init() {
        //-- register services --//
        _services.put(TINYURL, TinyUrl.class);
    }

    private IURLShortener getService(final String serviceId) {
        synchronized (_services) {
            final Class srvcClass = _services.get(serviceId);
            final IURLShortener result = this.createService(srvcClass);
            return null != result ? result : new TinyUrl();
        }
    }

    private IURLShortener createService(final Class srvcClass) {
        if (null != srvcClass) {
            try {
                return (IURLShortener) srvcClass.newInstance();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private String tryShortUrl(final String url) {
        synchronized (_services) {
            if (!CollectionUtils.isEmpty(_services)) {
                final Collection<Class<? extends IURLShortener>> services = _services.values();
                for (final Class<? extends IURLShortener> srvcClass : services) {
                    final IURLShortener srvc = this.createService(srvcClass);
                    if (null != srvc) {
                        try {
                            return srvc.getShortUrl(url);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return url;
        }
    }

    private String tryShortUrl(final String serviceId, final String url)
            throws Exception {
        if (StringUtils.hasText(url)) {
            final IURLShortener srvc = this.getService(serviceId);
            if (null != serviceId) {
                final String shorturi = srvc.getShortUrl(url);
                if (StringUtils.hasText(shorturi)) {
                    if (shorturi.length() < url.length()) {
                        return shorturi;
                    }
                }
            }
        }
        return url;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static ShortURL __instance;

    private static ShortURL getInstance() {
        if (null == __instance) {
            __instance = new ShortURL();
        }
        return __instance;
    }

    public static String get(final String url) {
        final ShortURL instance = getInstance();
        return instance.getShortUrl(DEFAULT_SERVICE, url);
    }

    public static String get(final String serviceId, final String url) {
        final ShortURL instance = getInstance();
        return instance.getShortUrl(serviceId, url);
    }
}
