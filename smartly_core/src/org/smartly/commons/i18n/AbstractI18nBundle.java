/*
 * 
 */
package org.smartly.commons.i18n;

import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

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

    public AbstractI18nBundle() {
        _refereeClass = this.getClass();
    }

    public AbstractI18nBundle(final Class refereeClass) {
        _refereeClass = refereeClass;

    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getMessage(final String key,
                             final Locale locale,
                             final ClassLoader classloader,
                             final Object... args) {
        final String msg = ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader);
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
        final String msg = ResourceBundleManager.getString(_refereeClass,
                key,
                null != locale ? locale : Locale.ENGLISH,
                classloader);
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


}
