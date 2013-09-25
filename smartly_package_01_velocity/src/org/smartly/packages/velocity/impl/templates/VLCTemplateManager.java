/*
 * 
 */
package org.smartly.packages.velocity.impl.templates;


import org.smartly.Smartly;
import org.smartly.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.velocity.impl.VLCManager;

import java.util.*;

/**
 * Extend this class to create a Template Manager.
 * Template Manager should be a singleton objects.
 *
 * @author angelo.geminiani
 */
public abstract class VLCTemplateManager
        extends AbstractI18nBundle {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CONTENT = "content";
    //-- --//
    private static final Locale ROOT = Locale.ROOT;
    private final Map<Locale, Properties> _properties;
    private final Map<String, Object> _internalContex;
    private final String[] _languages;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public VLCTemplateManager(final String[] languages) {
        _properties = new HashMap<Locale, Properties>();
        _internalContex = new HashMap<String, Object>();
        _languages = languages;
        this.init();
    }

    public VLCTemplateManager(final String[] languages, final Class refererClass) {
        super(refererClass);
        _properties = new HashMap<Locale, Properties>();
        _internalContex = new HashMap<String, Object>();
        _languages = languages;
        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _properties.clear();
        } catch (Throwable t) {
        }
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------
    public VLCTemplateManager addToContext(final String key,
                                           final Object value) {
        if (StringUtils.hasText(key) && null != value) {
            if (value instanceof String) {
                final String text = this.evalText((String) value);
                _internalContex.put(key, text);
            } else {
                _internalContex.put(key, value);
            }
        }
        return this;
    }

    public VLCTemplateManager addToContext(final Map<String, Object> data) {
        if (!CollectionUtils.isEmpty(data)) {
            final Set<String> keys = data.keySet();
            for (final String key : keys) {
                this.addToContext(key, data.get(key));
            }
        }
        return this;
    }

    public String evalText(final String text) {
        return this.evalText(text, null);
    }

    public String evalText(final String text, final Map<String, Object> context) {
        if (StringUtils.hasText(text)) {
            try {
                if (!CollectionUtils.isEmpty(context)) {
                    _internalContex.putAll(context);
                }
                return VLCManager.getInstance().evaluateText(KEY_CONTENT, text, _internalContex);
            } catch (Exception ex) {
                this.getLogger().log(Level.SEVERE, null, ex);
                return text;
            }
        }
        return "";
    }

    public String get(final Locale locale, final String key, final Object... args) {
        final String value = this.getPropertyValue(locale, key);
        if (StringUtils.hasText(value)) {
            final String result = this.evalText(value, null);
            if (null != args && args.length > 0) {
                return FormatUtils.format(result, args);
            }
            return result;
        }
        return "";
    }

    public String getTitle(final Locale locale) {
        final String value = this.getPropertyValue(locale, KEY_TITLE);
        if (StringUtils.hasText(value)) {
            return this.evalText(value, null);
        }
        return "";
    }

    public String getDescription(final Locale locale) {
        final String value = this.getPropertyValue(locale, KEY_DESCRIPTION);
        if (StringUtils.hasText(value)) {
            return this.evalText(value, null);
        }
        return "";
    }

    public String getContent(final Locale locale) {
        return this.getContent(locale, null);
    }

    public String getContent(final Locale locale,
                             final Map<String, Object> optData) {
        final String value = this.getPropertyValue(locale, KEY_CONTENT);
        if (StringUtils.hasText(value)) {
            final String content = this.getContent(value);
            return this.evalText(content, optData);
        }
        return "";
    }

    /**
     * Returns default properties.
     *
     * @return Default Properties
     */
    public Properties getProperties() {
        // return super.getProperties(null, BeeRuntime.getSystemClassLoader());
        return this.getPropertiesFromCache(ROOT);
    }

    /**
     * Return properties for a specific language.
     *
     * @param langCode Locale code
     * @return Localized Properties
     */
    public Properties getProperties(final String langCode) {
        final Locale locale = this.getLocale(langCode);
        return null != locale
                ? this.getProperties(locale)
                : this.getProperties();
    }

    /**
     * Return properties for a specific language, only if exists.
     *
     * @param langCode Locale code
     * @return Localized Properties or NULL if localization does not exists.
     */
    public Properties getPropertiesIfExists(final String langCode) {
        final Locale locale = this.getLocale(langCode);
        return null != locale && !locale.equals(Locale.ROOT)
                ? this.getProperties(locale)
                : null;
    }

    /**
     * Return properties for a specific language.
     *
     * @param locale Locale
     * @return Localized Properties
     */
    public Properties getProperties(final Locale locale) {
        //return super.getProperties(locale, BeeRuntime.getSystemClassLoader());
        return this.getPropertiesFromCache(locale);
    }

    /**
     * Return properties for a specific language, whose keys start with
     * specified charSequence.
     *
     * @param langCode     language code. i.e. "en"
     * @param charSequence i.e. "key1."
     * @return Localized Properties
     */
    public Properties getPropertiesStartWith(final String langCode,
                                             final String charSequence) {
        final Locale locale = this.getLocale(langCode);
        return this.getPropertiesStartWith(locale, charSequence);
    }

    /**
     * Return properties for a specific language, whose keys start with
     * specified charSequence.
     *
     * @param locale       Locale
     * @param charSequence i.e. "key1."
     * @return Localized Properties
     */
    public Properties getPropertiesStartWith(final Locale locale,
                                             final String charSequence) {
        final Properties result = new Properties();
        final Properties properties = this.getPropertiesFromCache(locale);
        if (null != properties && properties.size() > 0) {
            final Set<String> names = properties.stringPropertyNames();
            for (final String name : names) {
                if (StringUtils.startsWithIgnoreCase(name, charSequence)) {
                    result.setProperty(name, properties.getProperty(name));
                }
            }
        }

        return result;
    }

    public String getPropertyValue(final String langCode, final String key) {
        final Locale locale = this.getLocale(langCode);
        return this.getPropertyValue(locale, key);
    }

    public String getPropertyValue(final Locale locale, final String key) {
        final Properties props = this.getProperties(locale);
        return null != props
                ? this.evalText(props.getProperty(key))
                : null;
    }

    public String setPropertyValue(final String langCode, final String key,
                                   final String value) {
        final Locale locale = this.getLocale(langCode);
        return this.setPropertyValue(locale, key, value);
    }

    public String setPropertyValue(final Locale locale, final String key,
                                   final String value) {
        final Properties props = this.getProperties(locale);
        return null != props
                ? (String) props.setProperty(key, value)
                : null;
    }

    public void setPropertyValueToAllLocales(final String key, final String value) {
        final Set<Locale> items = this.getLocales();
        if (items.size() > 0) {
            for (final Locale locale : items) {
                this.setPropertyValue(locale, key, value);
            }
        }
    }

    public Set<Locale> getLocales() {
        final Set<Locale> result = _properties.keySet();
        return null != result ? result : new HashSet<Locale>();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        // default
        this.initVariables("");
        // languages
        for (final String language : _languages) {
            this.initVariables(language);
        }
    }

    private void initVariables(final String langCode) {
        final Locale locale = this.getLocale(langCode);
        final Properties item = super.getProperties(locale, Smartly.getClassLoader());
        if (null != item) {
            this.addPropertiesToCache(locale, item);
            if (item.isEmpty()) {
                //-- empty localizations --//
                this.getLogger().log(Level.WARNING,
                        FormatUtils.format(
                                "CLASS LOCALIZATION: Empty localizations or "
                                        + "localizations not found Language [%s] "
                                        + " of class [%s]. Verify %s.properties exists or is filled.",
                                langCode, this.getClass().getSimpleName(),
                                this.getClass().getSimpleName()));
            }
        } else {
            //-- language non implemented --//
            this.getLogger().log(Level.WARNING,
                    FormatUtils.format(
                            "CLASS LOCALIZATION: Language [%s] not implemented for [%s]",
                            langCode, this.getClass().getSimpleName()));
        }
    }

    private Locale getLocale(final String langCode) {
        final Locale locale = StringUtils.hasText(langCode)
                ? LocaleUtils.getLocaleFromString(langCode)
                : Locale.ROOT;
        return locale;
    }

    private Properties getPropertiesFromCache(final Locale locale) {
        Properties result = null != locale
                ? _properties.get(locale)
                : _properties.get(ROOT);
        if (null == result) {
            // try reading from package
            result = super.getProperties(locale,
                    this.getClass().getClassLoader());
            if (null != result) {
                this.addPropertiesToCache(locale, result);
            }
        }
        return result;
    }

    private void addPropertiesToCache(final Locale locale,
                                      final Properties item) {
        if (null != locale) {
            _properties.put(locale, item);
        } else {
            _properties.put(ROOT, item);
        }
    }

    private String getContent(final String fileName) {
        return ClassLoaderUtils.getResourceAsString(null, this.getClass(), fileName);
    }
}
