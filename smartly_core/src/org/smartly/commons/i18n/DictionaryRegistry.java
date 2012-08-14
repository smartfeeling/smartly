package org.smartly.commons.i18n;

import org.smartly.commons.i18n.impl.ChainDictionary;
import org.smartly.commons.i18n.resourcebundle.AbstractI18nBundle;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton dictionary registry.
 * Contains read-only dictionaries.
 */
public class DictionaryRegistry {

    private final Map<String, AbstractI18nBundle> _dictionaries;

    private DictionaryRegistry() {
        _dictionaries = Collections.synchronizedMap(new LinkedHashMap<String, AbstractI18nBundle>());
    }

    public boolean containsKey(final String name) {
        return _dictionaries.containsKey(name);
    }

    public void add(final Class<? extends AbstractI18nBundle> dictionary) {
        try {
            final AbstractI18nBundle dic = dictionary.newInstance();
            this.add(dic);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    public AbstractI18nBundle get(final String name) {
        return _dictionaries.get(name);
    }

    public Collection<AbstractI18nBundle> getAll() {
        return _dictionaries.values();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void add(final AbstractI18nBundle dic) {
        if (null != dic) {
            final String key = dic.getName();
            if (_dictionaries.containsKey(key)) {
                final String msg = FormatUtils.format("'{0}'({1}) already exists and will be replaced from ({2})",
                        key, _dictionaries.get(key).getClass().getName(), dic.getClass().getName());
            }
            _dictionaries.put(dic.getName(), dic);
            if (dic instanceof ChainDictionary) {
                this.add(((ChainDictionary) dic).next());
            }
        }
    }

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    private static DictionaryRegistry __instance;

    private static DictionaryRegistry getInstance() {
        if (null == __instance) {
            __instance = new DictionaryRegistry();
        }
        return __instance;
    }

    public static void register(final Class<? extends AbstractI18nBundle> dic) {
        getInstance().add(dic);
    }

    public static boolean contains(final String key) {
        return getInstance().containsKey(key);
    }

    public static AbstractI18nBundle getDictionary(final String name) {
        return getInstance().get(name);
    }

    public static Collection<AbstractI18nBundle> getDictionaries() {
        return getInstance().getAll();
    }
}
