/*
 * 
 */
package org.smartly.packages.velocity.impl.vtools.lang;

import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.StringUtils;

import java.util.*;

/**
 * VLCObject works like a Map and has a method to retrieve a querystring
 *
 * @author angelo.geminiani
 */
public class VLCObject implements Map<String, Object> {

    private final Map<String, Object> _map;

    public VLCObject() {
        _map = new HashMap<String, Object>();
    }

    public VLCObject(final Object args) {
        this();
        if (null != args) {
            this.init(args);
        }
    }

    @Override
    public String toString() {
        return _map.toString();
    }

    //<editor-fold defaultstate="collapsed" desc=" MAP ">
    @Override
    public int size() {
        return _map.size();
    }

    @Override
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return _map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return _map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return _map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return _map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        _map.putAll(m);
    }

    @Override
    public void clear() {
        _map.clear();
    }

    @Override
    public Set<String> keySet() {
        return _map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return _map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return _map.entrySet();
    }
    //</editor-fold>

    public final int getInt(final String key) {
        return this.getInt(key, 0);
    }

    public final int getInt(final String key, final int defaultValue) {
        return ConversionUtils.toInteger(this.get(key), defaultValue);
    }

    public final double getDouble(final String key) {
        return this.getDouble(key, 0.0);
    }

    public final double getDouble(final String key, final double defaultValue) {
        return ConversionUtils.toDouble(this.get(key), -1, defaultValue);
    }

    public final String getString(final String key) {
        return this.getString(key, "");
    }

    public final String getString(final String key, final String defaultValue) {
        return StringUtils.toString(this.get(key), defaultValue);
    }

    public final String toQueryString() {
        return StringUtils.toQueryString(_map);
    }

    public final String toQueryString(final String separator) {
        return StringUtils.toQueryString(_map, separator);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void init(final Object args) {
        int count = 0;
        if (args instanceof List) {
            final List<Object> list = (List<Object>) args;
            for (final Object arg : list) {
                if (null != arg) {
                    _map.put(count + "", arg);
                    count++;
                }
            }
        } else if (args.getClass().isArray()) {
            final Object[] list = (Object[]) args;
            for (final Object arg : list) {
                if (null != arg) {
                    _map.put(count + "", arg);
                    count++;
                }
            }
        }

    }
}
