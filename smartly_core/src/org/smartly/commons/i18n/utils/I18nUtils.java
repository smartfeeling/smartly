/*
 * ResourceBundleUtils.java
 *
 */
package org.smartly.commons.i18n.utils;

import org.smartly.commons.i18n.resourcebundle.bundle.IResourceBundle;
import org.smartly.commons.i18n.resourcebundle.bundle.impl.ClassResourceBundle;
import org.smartly.commons.i18n.resourcebundle.bundle.impl.FileResourceBundle;
import org.smartly.commons.i18n.resourcebundle.cache.ResourceBundleCache;

import java.util.Locale;

/**
 *
 */
public abstract class I18nUtils {

    public static IResourceBundle getOrCreateBundle(final String baseName,
                                                    final Locale locale,
                                                    final ClassLoader classloader) throws Exception {
        final String key = I18nUtils.buildResourceBundleKey(baseName, locale);

        // search rb in repository
        IResourceBundle result = ResourceBundleCache.get(key);
        // create rb and add to repository
        if (null == result) {
            result = new ClassResourceBundle(baseName, locale, classloader);
            if (result.isActive()) {
                ResourceBundleCache.add(key, result);
            } else {
                result = new FileResourceBundle(baseName, locale);
                if (result.isActive())
                    ResourceBundleCache.add(key, result);
            }
        }

        // rethrow bundle exception
        if (null != result && null != result.getError()) {
            throw new Exception(result.getError());
        }
        return result;
    }

    public static String buildResourceBundleKey(final String baseName,
                                                final Locale locale) {
        return null != locale
                ? baseName.concat(":").
                concat(locale.getDisplayCountry()).concat(":").
                concat(locale.getDisplayLanguage())
                : baseName;
    }

    public static String buildResourceKey(final String baseName,
                                          final String labelName,
                                          final Locale locale) {
        return null != locale
                ? baseName.concat(":").
                concat(labelName).concat(":").
                concat(locale.getDisplayCountry()).concat(":").
                concat(locale.getDisplayLanguage())
                : baseName.concat(":").concat(labelName);
    }

}
