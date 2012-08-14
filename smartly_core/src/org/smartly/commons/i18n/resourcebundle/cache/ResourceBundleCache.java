/*
 * ResourceBundleRepository.java
 *
 */

package org.smartly.commons.i18n.resourcebundle.cache;

import org.smartly.commons.i18n.resourcebundle.bundle.IResourceBundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This singleton repository is cache for application
 * ResourceBundle.
 *
 * @author
 */
public class ResourceBundleCache {

    private final Map<String, IResourceBundle> _resourceBundleCache;

    /**
     * Creates a new instance of ResourceBundleCache
     */
    private ResourceBundleCache() {
        _resourceBundleCache = Collections.synchronizedMap(new HashMap<String, IResourceBundle>());
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (null != _resourceBundleCache) {
                _resourceBundleCache.clear();
            }
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    public String getResourceFromCache(final String cacheKey, final String resourceKey) {
        synchronized (_resourceBundleCache) {
            IResourceBundle rb = _resourceBundleCache.get(cacheKey);
            if (null == rb)
                return null;

            return rb.getString(resourceKey);
        }
    }

    public void addResourceBundleToCache(String key, IResourceBundle rb) {
        synchronized (_resourceBundleCache) {
            _resourceBundleCache.put(key, rb);
        }
    }

    public IResourceBundle getResourceBundleFromCache(String key) {
        synchronized (_resourceBundleCache) {
            return _resourceBundleCache.get(key);
        }
    }

    public void clearCache() {
        synchronized (_resourceBundleCache) {
            _resourceBundleCache.clear();
        }
    }

    // ------------------------------------------------------------------------
    //                          S T A T I C
    // ------------------------------------------------------------------------

    private static ResourceBundleCache _instance;

    public static ResourceBundleCache getInstance() {
        if (null == _instance)
            _instance = new ResourceBundleCache();
        return _instance;
    }

    public static void clear() {
        ResourceBundleCache instance = getInstance();
        instance.clearCache();
    }

    public static void add(String key, IResourceBundle rb) {
        ResourceBundleCache instance = getInstance();
        instance.addResourceBundleToCache(key, rb);
    }

    public static IResourceBundle get(String key) {
        ResourceBundleCache instance = getInstance();
        return instance.getResourceBundleFromCache(key);
    }

    public static String getLabel(String key, String name) {
        ResourceBundleCache instance = getInstance();
        return instance.getResourceFromCache(key, name);
    }

}
