package org.smartly.packages.velocity.impl.vtools.lang;

import org.smartly.Smartly;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class VLCI18n {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final Map<String, VLCObject> _i18n;

    private String _lang;       // current language
    private String _def_lang;   // default language

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public VLCI18n() {
        this("", "");
    }

    public VLCI18n(final String lang) {
        this(lang, "");
    }

    public VLCI18n(final String lang, final String def_lang) {
        _i18n = new HashMap<>();
        _def_lang = StringUtils.hasText(def_lang) ? getLang(def_lang) : Smartly.getLang();
        _lang = StringUtils.hasText(lang) ? getLang(lang) : _def_lang;
    }

    public void setDefaultLang(final String value) {
        _def_lang = getLang(value);
    }

    public void use(final String value) {
        _lang = getLang(value);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object get(final String key) {
        if (this.getDictionary(_lang).containsKey(key)) {
            return this.getDictionary(_lang).get(key);
        }
        return this.getDictionary(_def_lang).get(key);
    }

    public String getString(final String key) {
        if (this.getDictionary(_lang).containsKey(key)) {
            return this.getDictionary(_lang).getString(key);
        }
        return this.getDictionary(_def_lang).getString(key);
    }

    public double getDouble(final String key) {
        if (this.getDictionary(_lang).containsKey(key)) {
            return this.getDictionary(_lang).getDouble(key);
        }
        return this.getDictionary(_def_lang).getDouble(key);
    }

    public int getInt(final String key) {
        if (this.getDictionary(_lang).containsKey(key)) {
            return this.getDictionary(_lang).getInt(key);
        }
        return this.getDictionary(_def_lang).getInt(key);
    }

    public boolean getBoolean(final String key) {
        if (this.getDictionary(_lang).containsKey(key)) {
            return this.getDictionary(_lang).getBoolean(key);
        }
        return this.getDictionary(_def_lang).getBoolean(key);
    }

    public void put(final String key, final Object value) {
        this.getDictionary(_lang).put(key, value);
    }

    // ------------------------------------------------------------------------
    //                      u t i l i t y
    // ------------------------------------------------------------------------

    public boolean equals(final String lang1, final String lang2) {
        return LocaleUtils.like(lang1, lang2);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private VLCObject getDictionary(final String lang) {
        if (!_i18n.containsKey(lang)) {
            _i18n.put(lang, new VLCObject());
        }
        return _i18n.get(lang);
    }

    private String getLang(final String raw) {
        return LocaleUtils.getLocaleFromString(raw).getLanguage();
    }
}
