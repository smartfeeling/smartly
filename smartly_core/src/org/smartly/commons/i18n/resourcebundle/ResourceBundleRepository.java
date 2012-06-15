/*
 * ResourceBundleRepository.java
 *
 */

package org.smartly.commons.i18n.resourcebundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This singleton repository is cache for application
 * ResourceBundle.
 *
 * @author
 */
public class ResourceBundleRepository {

    private final Map<String, IResourceBundle> _resourceBundleCache;

    /**
     * Creates a new instance of ResourceBundleRepository
     */
    private ResourceBundleRepository() {
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

    private static ResourceBundleRepository _instance;

    public static ResourceBundleRepository getInstance() {
        if (null == _instance)
            _instance = new ResourceBundleRepository();
        return _instance;
    }

    public static void clear() {
        ResourceBundleRepository instance = getInstance();
        instance.clearCache();
    }

    public static void add(String key, IResourceBundle rb) {
        ResourceBundleRepository instance = getInstance();
        instance.addResourceBundleToCache(key, rb);
    }

    public static IResourceBundle get(String key) {
        ResourceBundleRepository instance = getInstance();
        return instance.getResourceBundleFromCache(key);
    }

    public static String getLabel(String key, String name) {
        ResourceBundleRepository instance = getInstance();
        return instance.getResourceFromCache(key, name);
    }

}
