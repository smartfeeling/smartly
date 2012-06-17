package org.smartly.commons.i18n.impl;


import org.smartly.commons.i18n.AbstractI18nBundle;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChainDictionary extends BaseDictionary {

    private final AbstractI18nBundle _parentDictionary;
    private final Map<String, Map<String, String>> _dictionaries;

    public ChainDictionary() {
        _parentDictionary = this.getSuperclassInstance();
        _dictionaries = new HashMap<String, Map<String, String>>();
    }

    @Override
    public String getMessage(final String key,
                             final Locale locale, final ClassLoader classloader,
                             final Object... args) {
        // retrieve a dictionary
        final Map<String, String> dic = this.getDictionary(locale);
        // try dictionary
        String result = dic.get(key);
        // try current bundle
        if (!StringUtils.hasText(result)) {
            result = super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // try parent bundle
        if (!StringUtils.hasText(result)) {
            result = _parentDictionary.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // return formatted result
        if (StringUtils.hasText(result)) {
            if (args.length > 0) {
                return FormatUtils.format(result, args);
            } else {
                return result;
            }
        }
        return "";
    }

    @Override
    public String getMessage(final String key,
                             final Locale locale, final ClassLoader classloader,
                             final Map<String, ?> args) {
        // retrieve a dictionary
        final Map<String, String> dic = this.getDictionary(locale);
        // try dictionary
        String result = dic.get(key);
        // try current bundle
        if (!StringUtils.hasText(result)) {
            result = super.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // try parent bundle
        if (!StringUtils.hasText(result)) {
            result = _parentDictionary.getMessage(key, locale, classloader);
            dic.put(key, result);
        }
        // return formatted result
        if (StringUtils.hasText(result)) {
            if (null!=args && !args.isEmpty()) {
                return FormatUtils.format(result, args);
            } else {
                return result;
            }
        }
        return "";
    }

    @Override
    public String getMessage(final String key, final Locale locale,
                             final Object... args) {
        return this.getMessage(key,
                locale, BaseDictionary.getClassLoader(), args);
    }

    @Override
    public String getMessage(final String key, final String slocale,
                             final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    @Override
    public String getMessage(final String key, final Locale locale,
                             final Map<String, ?> args) {
        return this.getMessage(key,
                locale, BaseDictionary.getClassLoader(), args);
    }

    @Override
    public String getMessage(final String key, final String slocale,
                             final Map<String, ?> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(slocale);
        return this.getMessage(key, locale, args);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Map<String, String> getDictionary(final Locale locale) {
        final String key = locale.toString();
        if (_dictionaries.containsKey(key)) {
            return _dictionaries.get(key);
        }
        final Map<String, String> result = new HashMap<String, String>();
        _dictionaries.put(key, result);
        return result;
    }

    private AbstractI18nBundle getSuperclassInstance(){
        AbstractI18nBundle result = null;
        try{
            result = (AbstractI18nBundle) this.getClass().getSuperclass().newInstance();
        }catch(Throwable ignored){
        }
        return null!=result?result: getInstance();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------


}
