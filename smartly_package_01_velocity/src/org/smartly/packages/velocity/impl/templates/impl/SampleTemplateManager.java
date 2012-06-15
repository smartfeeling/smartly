/*
 * 
 */
package org.smartly.packages.velocity.impl.templates.impl;

import org.smartly.commons.util.LocaleUtils;
import org.smartly.packages.velocity.impl.templates.VLCTemplateManager;

import java.util.Locale;

/**
 * @author angelo.geminiani
 */
public class SampleTemplateManager extends VLCTemplateManager {

    public SampleTemplateManager(final String[] languages) {
        super(languages);
        //super(super(ConfigurationUtils.getInstance().getLanguageCodes()););
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------
    private static SampleTemplateManager __instance;

    public static SampleTemplateManager getInstance() {
        if (null == __instance) {
            __instance = new SampleTemplateManager(new String[]{"it", "en"});
        }
        return __instance;
    }

    public static String getContent(final String langCode) {
        final Locale locale = LocaleUtils.getLocaleFromString(langCode);
        return getInstance().getContent(locale);
    }

    public static String getTitle(final String langCode) {
        final Locale locale = LocaleUtils.getLocaleFromString(langCode);
        return getInstance().getTitle(locale);
    }

    public static String getDescription(final String langCode) {
        final Locale locale = LocaleUtils.getLocaleFromString(langCode);
        return getInstance().getDescription(locale);
    }
}
