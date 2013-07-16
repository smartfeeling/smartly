/*
 * 
 */
package org.smartly.commons.i18n.resourcebundle;

import org.smartly.commons.i18n.resourcebundle.bundle.ResourceBundleManager;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.util.Locale;
import java.util.Properties;


/**
 * Extends this class for a localized class.<br>
 *
 * @author
 */
public abstract class AbstractI18nBundle {

    private final Class _refereeClass;
    private boolean _lookupForFileResource = false;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AbstractI18nBundle() {
        _refereeClass = this.getClass();
    }

    public AbstractI18nBundle(final Class refereeClass) {
        _refereeClass = refereeClass;
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

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader) {
        return this.validate(ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader));
    }

    public Properties getProperties(final Locale locale,
                                    final ClassLoader classloader) {
        final String classPath = PathUtils.getClassPath(_refereeClass);
        try {
            return ResourceBundleManager.getProperties(classPath,
                    null != locale ? locale : Locale.ENGLISH,
                    classloader);
        } catch (Exception ex) {
            return new Properties();
        }
    }

    public abstract String getName();

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

    private String readFile(final String fileName) throws Exception {
        final String result = ClassLoaderUtils.getResourceAsString(null, this.getClass(), fileName);
        if (null == result) {
            throw new Exception("not a file");
        }
        return result;
    }

}
