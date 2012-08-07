/*
 * 
 */
package org.smartly.commons.i18n.impl;

import org.smartly.commons.i18n.AbstractI18nBundle;
import org.smartly.commons.util.LocaleUtils;

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
        return super.getMessage(key, locale, getClassLoader(), args);
    }

    public String getMessage(final String key, final Locale locale,
                             final Map<String, ? extends Object> args) {
        return super.getMessage(key, locale, getClassLoader(), args);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


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
