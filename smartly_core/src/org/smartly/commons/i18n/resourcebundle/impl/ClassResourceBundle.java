/*
 * ClassResourceBundle.java
 *
 */
package org.smartly.commons.i18n.resourcebundle.impl;

import org.smartly.commons.i18n.resourcebundle.IResourceBundle;
import org.smartly.commons.util.LocaleUtils;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Wrapper for a standard ResourceBundle.
 *
 * @author
 */
public final class ClassResourceBundle
        implements IResourceBundle {

    private String _baseName;
    private Locale _locale;
    private Boolean _active;
    private ResourceBundle _rb;
    private Throwable _error;

    /**
     * Creates a new instance of ClassResourceBundle
     */
    public ClassResourceBundle(final String baseName,
                               final Locale locale, final ClassLoader classloader) {
        _active = false;
        _baseName = baseName;
        _locale = locale;
        final ClassLoader cl = null != classloader
                ? classloader
                : ClassLoader.getSystemClassLoader();
        try {
            _rb = null != locale
                    ? ResourceBundle.getBundle(baseName, locale, cl)
                    : ResourceBundle.getBundle(baseName, Locale.getDefault(), cl);
            if (null != locale && null != _rb) {
                final boolean match = LocaleUtils.like(locale, _rb.getLocale());
                if (!match) {
                    _rb = ResourceBundle.getBundle(baseName, Locale.getDefault(), cl);
                }
            }

            if (null == _rb) {
                throw new Exception("Unable to retrieve resources " +
                        "for current base name: " + baseName);
            }
            _active = true;
        } catch (Throwable t) {
            // if ResourceBundle not found
            _error = t;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _rb = null;
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Base Name: ").append(_baseName).
                append("; Locale: ").append(_locale.toString());
        if (null != _rb) {
            result.append("; Resources: ").append(_rb.toString());
        }

        return result.toString();
    }

    @Override
    public Throwable getError() {
        return _error;
    }

    @Override
    public String getString(String key) {
        if (null != _rb) {
            return _rb.getString(key);
        } else {
            return null;
        }
    }

    @Override
    public boolean isActive() {
        return _active;
    }

    @Override
    public Properties getProperties() {
        final Properties result = new Properties();
        if (null == _rb || !_active) {
            return result;
        }
        final Enumeration<String> keys = _rb.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            result.setProperty(key, _rb.getString(key));
        }
        return result;
    }
}
