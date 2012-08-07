/*
 * 
 */
package org.smartly.commons.i18n.impl;

import org.smartly.commons.i18n.AbstractI18nBundle;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * This is a default Dictionary. All dictionaries inherited from BaseDictionary
 * contains also items of this dictionary.<br/>
 * Note: To create new dictionary extend BaseDictionary.
 *
 * @author angelo.geminiani
 */
public class BaseDictionary extends AbstractI18nBundle {

    private boolean _lookupForFileResource = false;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public BaseDictionary(final Class refererClass) {
        super(refererClass);
    }

    public BaseDictionary() {
        super();
    }

    // --------------------------------------------------------------------
    //               p r o p e r t i e s
    // --------------------------------------------------------------------

    public void setLookupForFileResource(final boolean value) {
        _lookupForFileResource = value;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getMessage(final String key, final String slocale,
                             final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    public String getMessage(final String key, final String slocale,
                             final Map<String, ? extends Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    public String getMessage(final String key, final Locale locale,
                             final Object... args) {
        return this.validate(
                super.getMessage(key, locale, getClassLoader(), args));
    }

    public String getMessage(final String key, final Locale locale,
                             final Map<String, ? extends Object> args) {
        return this.validate(
                super.getMessage(key, locale, getClassLoader(), args));
    }



    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private String validate(final String value) {
        // should check if value is a file resource?
        if (_lookupForFileResource) {
            if (StringUtils.hasText(PathUtils.getFilenameExtension(value))) {
                try {
                    return this.readFile(value);
                } catch (Throwable ignored) {
                }
            }
        }

        return value;
    }

    private String readFile(final String fileName) throws Exception{
        final String result = ClassLoaderUtils.getResourceAsString(null, this.getClass(), fileName);
        if(null==result){
            throw new Exception("not a file");
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static ClassLoader __classLoader;
    private static BaseDictionary __instance;

    public static BaseDictionary getInstance() {
        if (null == __instance) {
            __instance = new BaseDictionary();
        }
        return __instance;
    }

    public static ClassLoader getClassLoader() {
        if (null == __classLoader) {
            __classLoader = Thread.currentThread().getContextClassLoader();
        }
        return __classLoader;
    }
}
