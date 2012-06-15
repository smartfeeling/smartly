package org.smartly.commons.i18n.cache;

import org.smartly.commons.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class I18nCacheItem {

    private final Map<String, String> _resources;
    private int _length;

    public I18nCacheItem() {
        _resources = Collections.synchronizedMap(new HashMap<String, String>());
        _length = 0;
    }

    public int length() {
        return _length;
    }

    public int size() {
        return _resources.size();
    }

    public void clear() {
        synchronized (_resources) {
            _resources.clear();
        }
    }

    public void put(final String key, final String value) {
        if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
            synchronized (_resources) {
                _resources.put(key, value);
                _length += value.length();
            }
        }
    }

    public boolean contains(final String key) {
        return _resources.containsKey(key);
    }

    public String get(final String key) {
        return _resources.get(key);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
