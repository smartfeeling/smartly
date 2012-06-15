/*
 * FileResourceBundle.java
 *
 */
package org.smartly.commons.i18n.resourcebundle.impl;

import org.smartly.commons.i18n.resourcebundle.IResourceBundle;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * This ResourceBundle load properties from file.
 *
 * @author
 */
public final class FileResourceBundle
        implements IResourceBundle {

    private static final String EXTENSION_SEPARATOR = ".";
    private static final String EXTENSION = ".properties";
    private Boolean _active = false;
    private Properties _properties;
    private Throwable _error;

    /**
     * Creates a new instance of FileResourceBundle
     *
     * @param path   Valid file path. Can include extension or not.
     *               If file has an extension different from ".properties", it
     *               will be replaced with ".properties".<br>
     *               Locale's details, like Language_Country_Variant, are added automatically.
     * @param locale desired Locale.
     */
    public FileResourceBundle(final String path,
                              final Locale locale) {
        _properties = new Properties();
        if (StringUtils.hasText(path)) {
            final File file = this.solveFile(path, locale);
            if (null != file) {
                this.loadProperties(_properties, file);
            } else {
                LoggingUtils.getLogger(
                        FileResourceBundle.class.getName()).log(Level.FINEST,
                        String.format("Unable to find file [%s] for locale [%s]. "
                                + "Please, check file path or locale is not NULL.",
                                path, null != locale ? locale.toString() : "NULL"));
            }
        }
    }

    /**
     * Creates a new instance of FileResourceBundle
     */
    public FileResourceBundle(final File file) {
        _properties = new Properties();
        this.loadProperties(_properties, file);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _properties = null;
            _active = false;
        } catch (Throwable ignored) {
        }
        super.finalize();
    }

    @Override
    public Throwable getError() {
        return _error;
    }

    @Override
    public String getString(String key) {
        if (null != _properties) {
            return _properties.getProperty(key);
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
        Properties result = new Properties();
        if (null == _properties || !_active) {
            return result;
        }
        result.putAll(_properties);
        return result;
    }
    // ------------------------------------------------------------------------
    //                          p r i v a t e
    // ------------------------------------------------------------------------

    private void loadProperties(final Properties properties,
                                final File file) {
        if (null == file || !file.exists()) {
            return;
        }
        try {
            final FileInputStream reader = new FileInputStream(file);
            properties.load(reader);
            _active = true;
        } catch (Throwable t) {
            _error = t;
        }
    }

    private File solveFile(final String path, final Locale locale) {
        File file = null;
        final String clearPath = this.stripFilenameExtension(path);
        if (null != locale) {
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            final String variant = locale.getVariant();
            String testPath = null;

            // try with language_country_variant
            if (country.length() > 0 && variant.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat("_").concat(country).concat("_").concat(variant).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }

            // try with language_country
            if (country.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat("_").concat(country).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }

            // try with language
            if (language.length() > 0) {
                testPath = clearPath.concat("_").concat(language).concat(EXTENSION);
                file = new File(testPath);
                if (file.exists()) {
                    return file;
                }
            }
        }
        // if no Locale file was found, try with default one
        file = new File(clearPath.concat(EXTENSION));
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        return sepIndex != -1 ? path.substring(0, sepIndex) : path;
    }
}
