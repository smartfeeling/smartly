package org.smartly.commons.i18n;

import org.smartly.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Dictionary helper class
 */
public class Dictionary {

    public static final String NAME = "dic"; // velocity name

    public Dictionary(){
    }

    public String getName() {
        return NAME;
    }

    public String get(final String lang, final String key) {
        return Dictionary.getMessage(lang, key);
    }

    public String get(final String lang, final String key, final Object... args) {
        return Dictionary.getMessage(lang, key, args);
    }

    public String get(final String lang, final String dicName, final String key) {
        return Dictionary.getMessage(lang, dicName, key);
    }

    public String get(final String lang, final String dicName, final String key, final Object... args) {
        return Dictionary.getMessage(lang, dicName, key, args);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getMessage(final String lang,
                                    final String key,
                                    final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(lang);
        return getMessage(locale, key, args);
    }

    public static String getMessage(final String lang,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(lang);
        return getMessage(locale, key, args);
    }

    public static String getMessage(final Locale locale,
                                    final String key,
                                    final Object... args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final Locale locale,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final String resource = lookup(key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final String lang,
                                    final String dicName,
                                    final String key,
                                    final Object... args) {
        final Locale locale = LocaleUtils.getLocaleFromString(lang);
        return getMessage(locale, dicName, key, args);
    }

    public static String getMessage(final String lang,
                                    final String dicName,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final Locale locale = LocaleUtils.getLocaleFromString(lang);
        return getMessage(locale, dicName, key, args);
    }

    public static String getMessage(final Locale locale,
                                    final String dicName,
                                    final String key,
                                    final Object... args) {
        final String resource = lookup(dicName, key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    public static String getMessage(final Locale locale,
                                    final String dicName,
                                    final String key,
                                    final Map<String, ? extends Object> args) {
        final String resource = lookup(dicName, key, locale);
        return StringUtils.hasText(resource)
                ? FormatUtils.format(resource, args)
                : "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String lookup(final String key, final Locale locale) {
        final Collection<AbstractI18nBundle> list = DictionaryRegistry.getDictionaries();
        for (final AbstractI18nBundle dic : list) {
            final String resource = dic.getMessage(key, locale, dic.getClass().getClassLoader());
            if (StringUtils.hasText(resource)) {
                return resource;
            }
        }
        return "";
    }

    private static String lookup(final String dicName, final String key, final Locale locale) {
        final AbstractI18nBundle dic = DictionaryRegistry.getDictionary(dicName);
        if (null != dic) {
            return dic.getMessage(key, locale, dic.getClass().getClassLoader());
        }
        return "";
    }

}
