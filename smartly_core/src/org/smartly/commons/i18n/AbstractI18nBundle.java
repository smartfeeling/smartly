/*
 * 
 */
package org.smartly.commons.i18n;

import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;


/**
 * Extends this class for a localized class.<br>
 *
 * @author
 */
public class AbstractI18nBundle {

    private final Class _refereeClass;
    private boolean _lookupForFileResource = false;

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
                             final ClassLoader classloader,
                             final Object... args) {
        final String msg = this.validate(ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader));
        if (null != args && args.length > 0) {
            return String.format(msg, args);
        } else {
            return msg;
        }
    }

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader,
                             final Map<String, ? extends Object> args) {
        final String msg = this.validate(ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader));
        if (null != args && args.size() > 0) {
            return FormatUtils.format(msg, args);
        } else {
            return msg;
        }
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

}
