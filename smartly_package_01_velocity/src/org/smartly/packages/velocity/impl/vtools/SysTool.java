/*
 * VLCSystem.java
 *
 *
 */
package org.smartly.packages.velocity.impl.vtools;


import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.URLUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.velocity.impl.vtools.lang.VLCObject;

import java.util.*;

/**
 *
 */
public class SysTool
        implements IVLCTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "sys";
    private static final String DEFAULT_IMAGE = "/images/not_found.png";

    // --------------------------------------------------------------------
    //               Fields
    // --------------------------------------------------------------------

    private final ConsoleTool _console;
    private final ConvertTool _convert;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public SysTool() {
        _console = new ConsoleTool();
        _convert = new ConvertTool();
    }

    @Override
    public String getName() {
        return NAME;
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------

    /**
     * Return a system property
     *
     * @param property Property name. ex: "user.home"
     * @return String
     */
    public String getSystemProperty(String property) {
        try {
            return java.lang.System.getProperty(property);
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            java.lang.System.err.println(
                    "Caught a SecurityException reading the system property '" + property
                            + "'; the SystemUtils property value will default to null.");
            return "";
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" HTML (encodeURIComponent, decodeURIComponent)">
    public String encode(final Object data) {
        return _convert.encode(data);
    }

    public String encodeURIComponent(final Object data) {
        return _convert.encodeURIComponent(data);
    }

    public String decode(final Object data) {
        return _convert.decode(data);
    }

    public String decodeURIComponent(final Object data) {
        return _convert.decodeURIComponent(data);
    }

    public String encodeHTML(final Object data) {
        return _convert.encodeHTML(data);
    }

    public String decodeHTML(final Object data) {
        return _convert.decodeHTML(data);
    }

    public String toHTML(final String text) {
        return _convert.toHTML(text);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" CALENDAR DATE (getNow, getTomorrow, isToday...)">

    /**
     * Return current date time.
     *
     * @return DateTime
     */
    public Date getNow() {
        return new Date();
    }

    public Date getTomorrow() {
        return DateUtils.postpone(this.getNow(), DateUtils.DAY, 1);
    }

    public Date getYesterday() {
        return DateUtils.postpone(this.getNow(), DateUtils.DAY, -1);
    }

    public boolean isToday(final Date date) {
        return DateUtils.isToday(date);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" NULL AND TYPE CHECK (isNull, isEmpty, isZero, notNull, notNullList...)">

    public boolean equals(final Object obj1, final Object obj2) {
        return StringUtils.equals(obj1, obj2);
    }

    public boolean isNull(final Object obj) {
        return StringUtils.isNULL(obj);
    }

    public boolean isNotNull(final Object obj) {
        return !StringUtils.isNULL(obj);
    }

    public boolean isZero(final Object obj) {
        final boolean isnull = StringUtils.isNULL(obj);
        return isnull
                ? isnull
                : obj.toString().equals("0") || obj.toString().equals("-1");
    }

    public boolean isTrue(final Object obj) {
        final boolean value = ConversionUtils.toBoolean(obj, false);
        return value;
    }

    public boolean isFalse(final Object obj) {
        return !this.isTrue(obj);
    }

    public Object notNull(final Object obj) {
        return this.notNull(obj, "");
    }

    public Object notNullList(final Object obj) {
        return this.notNull(obj, new ArrayList());
    }

    public Object notNullArray(final Object obj) {
        return this.notNull(obj, new Object[0]);
    }

    public Object notNull(final Object obj, final Object defaultValue) {
        if (StringUtils.isNULL(obj)) {
            return defaultValue;
        }
        return obj;
    }

    /**
     * Return first value not empty.
     *
     * @param values Variable array of values
     * @return first value not empty.
     */
    public Object first(final Object... values) {
        try {
            for (final Object value : values) {
                if (null != value && StringUtils.hasText(value.toString())) {
                    return value;
                }
            }
        } catch (Throwable ignored) {
        }
        return "";
    }

    public Object first(final List values) {
        try {
            for (final Object value : values) {
                if (null != value && StringUtils.hasText(value.toString())) {
                    return value;
                }
            }
        } catch (Throwable ignored) {
        }
        return "";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" COLLECTION UTILS (isEmpty, size...)">
    public boolean isEmpty(final Object obj) {
        if (null != obj) {
            if (obj instanceof Collection) {
                return CollectionUtils.isEmpty((Collection) obj);
            } else if (obj instanceof Map) {
                return CollectionUtils.isEmpty((Map) obj);
            } else if (obj.getClass().isArray()) {
                return CollectionUtils.isEmpty((Object[]) obj);
            } else if (obj instanceof JSONObject) {
                return ((JSONObject) obj).length() == 0;
            } else if (obj instanceof JSONArray) {
                return ((JSONArray) obj).length() == 0;
            } else if (obj instanceof JsonWrapper) {
                return ((JsonWrapper) obj).length() == 0;
            } else if (obj instanceof String) {
                return StringUtils.isNULL(obj);
            }
            return false;
        }
        return true;
    }

    public boolean isNotEmpty(final Object obj) {
        return !this.isEmpty(obj);
    }

    /**
     * Return size of collection, array, lenght of string or value of number.
     *
     * @param obj Array, Collection, Map, String or number
     * @return
     */
    public int size(final Object obj) {
        if (null != obj) {
            if (obj instanceof Collection) {
                return ((Collection) obj).size();
            } else if (obj instanceof Map) {
                return ((Map) obj).size();
            } else if (obj.getClass().isArray()) {
                return ((Object[]) obj).length;
            } else if (obj instanceof String) {
                return ((String) obj).length();
            } else if (obj instanceof JSONObject) {
                return ((JSONObject) obj).length();
            } else if (obj instanceof JSONArray) {
                return ((JSONArray) obj).length();
            } else {
                // try with number
                return ConversionUtils.toInteger(obj);
            }
        }
        return 0;
    }

    /**
     * Convert Array, JSONObject, Map, into list
     *
     * @param item
     * @return
     */
    public List<Object> toList(final Object item) {
        return CollectionUtils.toList(item);
    }

    public JSONArray toJSONArray(final Object item) {
        return JsonWrapper.toJSONArray(item);
    }

    /**
     * Return new array containig all common fields in both arrays, list or
     * maps. You can compare 2
     *
     * @param targetvalues
     * @param checkvalues
     * @return
     */
    public Collection<Object> match(final Object targetvalues,
                                    final Object checkvalues) {
        final List<Object> target = this.toList(targetvalues);
        final List<Object> check = this.toList(checkvalues);
        return CollectionUtils.match(target, check);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TEXT UTILS (hasText, toString)">
    public boolean hasText(final Object obj) {
        return !StringUtils.isNULL(obj);
    }

    public String toString(final Object obj) {
        if (null != obj) {
            if (obj instanceof Collection) {
                CollectionUtils.toCommaDelimitedString((Collection) obj);
            }
            return obj.toString();
        }
        return "";
    }

    public String concat(final Object... args) {
        return StringUtils.concatArgs(args);
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" GET BEAN VALUE (get, getList, getInt, getString, getImage, ...)">

    public Set<?> keys(final Object object) {
        if (!StringUtils.isNULL(object)) {
            if (object instanceof Map) {
                return ((Map) object).keySet();
            } else if (object instanceof JSONObject) {
                final Set<Object> result = new HashSet<Object>();
                final Iterator iter = ((JSONObject) object).keys();
                while (iter.hasNext()) {
                    result.add(iter.next());
                }
                return result;
            } else if (object instanceof JsonWrapper) {
                return ((JsonWrapper) object).keys();
            }
        }
        return new HashSet<Object>();
    }

    /**
     * Search for value in array or property in object.
     *
     * @param object
     * @param path
     * @return
     */
    public boolean has(final Object object,
                       final Object path) {
        if (null != object) {
            if (object instanceof Collection) {
                for (final Object item : (Collection) object) {
                    if (CompareUtils.equals(path, item)) {
                        return true;
                    }
                }
            } else if (object instanceof JSONArray) {
                final JSONArray array = (JSONArray) object;
                for (int i = 0; i < array.length(); i++) {
                    final Object item = array.opt(i);
                    if (CompareUtils.equals(path, item)) {
                        return true;
                    }
                }
            } else if (object.getClass().isArray()) {
                final Object[] array = (Object[]) object;
                for (final Object item : array) {
                    if (CompareUtils.equals(path, item)) {
                        return true;
                    }
                }
            } else {
                if (path instanceof String) {
                    return null != this.get(object, (String) path, null);
                }
            }
        }

        return false;
    }

    /**
     * Return first valid value of a field (array of field names) in passed object.
     * Never returns NULL object.
     *
     * @param object Object instance to return value from passed fieldName
     * @param paths  Names of field to return value
     * @return Value of fieldName or empty string
     */
    public Object getOne(final Object object,
                         final String... paths) {
        Object result = null;
        for (final String path : paths) {
            result = this.get(object, path, null);
            if (this.isNotNull(result)) {
                break;
            }
        }
        return null != result ? result : "";
    }

    public Object get(final Object object,
                      final String path) {
        return this.get(object, path, "");
    }

    public Object get(final Object object,
                      final String path,
                      final Object def) {
        try {
            if (object instanceof JsonWrapper) {
                final Object result = ((JsonWrapper) object).deep(path);
                if (null != result) {
                    return result;
                }
            } else {
                final Object result = BeanUtils.getValue(object, path);
                if (null != result) {
                    return result;
                }
            }
        } catch (Throwable ignored) {
        }
        return def;
    }

    /**
     * Return String value of a field in passed object. Never NULL object.
     *
     * @param object    Object instance to return value from passed fieldName
     * @param fieldName Name of field to return value
     * @return Value of fieldName or ZERO
     */
    public String getString(final Object object, final String fieldName) {
        return this.getString(object, fieldName, "");
    }

    public String getString(final Object object,
                            final String fieldName,
                            final String defaultValue) {
        final Object value = this.get(object, fieldName);
        if (null != value) {
            return value.toString();
        }
        return defaultValue;
    }

    public String getImage(final Object object, final String fieldName) {
        final String path = this.getString(object, fieldName);
        return this.getImage("", path, DEFAULT_IMAGE);
    }

    /**
     * Return a String containig an image path or path to default image.
     *
     * @param object       Object instance to return value from passed fieldName
     * @param fieldName    Name of field to return value
     * @param defaultImage
     * @return
     */
    public String getImage(final Object object, final String fieldName,
                           final String defaultImage) {
        final String path = this.getString(object, fieldName);
        return this.getImage(path, path, defaultImage);
    }

    /**
     * Return integer value of a field in passed object. Never NULL object.
     *
     * @param object    Object instance to return value from passed fieldName
     * @param fieldName Name of field to return value
     * @return Value of fieldName or ZERO
     */
    public int getInt(final Object object, final String fieldName) {
        final Object value = this.get(object, fieldName);
        if (null != value) {
            return ConversionUtils.toInteger(value, 0);
        }
        return 0;
    }

    public boolean getBoolean(final Object object, final String fieldName) {
        final Object value = this.get(object, fieldName);
        if (null != value) {
            return ConversionUtils.toBoolean(value);
        }
        return false;
    }

    /**
     * Return long value of a field in passed object. Never NULL object.
     *
     * @param object    Object instance to return value from passed fieldName
     * @param fieldName Name of field to return value
     * @return Value of fieldName or ZERO
     */
    public long getLong(final Object object, final String fieldName) {
        final Object value = this.get(object, fieldName);
        if (null != value) {
            return ConversionUtils.toLong(value, 0L);
        }
        return 0L;
    }

    /**
     * Return double value of a field in passed object. Never NULL object.
     *
     * @param object    Object instance to return value from passed fieldName
     * @param fieldName Name of field to return value
     * @return Value of fieldName or ZERO
     */
    public double getDouble(final Object object, final String fieldName) {
        return this.getDouble(object, fieldName, 0d);
    }

    public double getDouble(final Object object, final String fieldName, final double def) {
        final Object value = this.get(object, fieldName);
        if (null != value) {
            return ConversionUtils.toDouble(value, -1, def);
        }
        return def;
    }

    /**
     * Return property value as List. If original value is Array will be
     * converted in list. If original value is Map or JSON, only values will be
     * returned If original value is not List or Array, a List containing value
     * instance will be returned
     *
     * @param object
     * @param path
     * @return List
     */
    public List getList(final Object object, final String path) {
        List result = null;
        try {
            final Object item = this.get(object, path);
            result = this.toList(item);
        } catch (Throwable t) {
        }
        return result;
    }

    public void put(final Object object,
                    final String path,
                    final Object value) {
        if (object instanceof JsonWrapper) {
            ((JsonWrapper) object).putDeep(path, value);
        } else {
            BeanUtils.setValueIfAny(object, path, value);
        }
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" PATH (getFilename, ...)">
    public String getFilename(final String path) {
        return PathUtils.getFilename(path);
    }

    public String getFilename(final String path, final boolean incExt) {
        return PathUtils.getFilename(path, incExt);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" FS, URL (getUrlContent, ...)">
    public String getUrlContent(final String uri) {
        return URLUtils.getUrlContent(uri);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" LOG ">
    public void log(final Object item) {
        _console.log(item);
    }

    public void info(final Object item) {
        _console.info(item);
    }

    public void warn(final Object item) {
        _console.warn(item);
    }

    public void error(final Object item) {
        _console.error(item);
    }

    public void debug(final Object item) {
        _console.debug(item);
    }
    //</editor-fold>

    public Locale getLocale(final String locale) {
        return LocaleUtils.getLocaleFromString(locale);
    }

    public Locale getLocale(final String lang, final String country) {
        return LocaleUtils.getLocale(new Locale(lang, country));
    }

    public String[] split(final String string) {
        return this.split(string, ",");
    }

    public String[] split(final String string, final String delim) {
        return StringUtils.split(string, delim);
    }

    public Object getFirst(final Object array) {
        if (null != array) {
            if (array.getClass().isArray()) {
                return CollectionUtils.getFirst((Object[]) array);
            } else if (array instanceof Collection) {
                return CollectionUtils.getFirst((Collection) array);
            }
        }
        return null;
    }

    public Object getLast(final Object array) {
        if (null != array) {
            if (array.getClass().isArray()) {
                return CollectionUtils.getLast((Object[]) array);
            } else if (array instanceof Collection) {
                return CollectionUtils.getLast((Collection) array);
            }
        }
        return null;
    }

    public String getImage(final String path) {
        return this.getImage("", path);
    }

    public String getImage(final String root, final String path) {
        return this.getImage(root, path, DEFAULT_IMAGE);
    }

    public String getImage(final String root, final String path,
                           final String defaultImage) {
        if (StringUtils.hasText(path)) {
            if (StringUtils.hasText(root)) {
                return PathUtils.join(root, path);
            } else {
                return path;
            }
        } else {
            return StringUtils.hasText(defaultImage) ? defaultImage : DEFAULT_IMAGE;
        }
    }

    /**
     * Return UID.
     *
     * @return Unique Identifier
     */
    public String getUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Return GUID
     *
     * @return Unique Identifier
     */
    public String getGuid() {
        return UUID.randomUUID().toString();
    }

    public String getRandom(final Object size) {
        if (null != size) {
            return RandomUtils.random(ConversionUtils.toInteger(size),
                    RandomUtils.CHARS_LOW_NUMBERS);
        }
        return RandomUtils.random(6,
                RandomUtils.CHARS_LOW_NUMBERS);
    }

    public String getRandom() {
        return this.getRandom(6);
    }

    /**
     * Create empty basic Object.
     *
     * @return
     */
    public VLCObject newObject() {
        return new VLCObject();
    }

    /**
     * Creates Map object adding values. Values keys are insert index.
     *
     * @param args
     * @return
     */
    public VLCObject newObject(final Object args) {
        return new VLCObject(args);
    }

    public Counter newCounter() {
        return new Counter(this, 0);
    }

    public Counter newCounter(final int start) {
        return new Counter(this, start);
    }

    /**
     * Returns array of integers to use in #foreach loops.
     * i.e.:
     * #set($iterator=$sys.newIterator(5))
     * #foreach($i in $iterator)
     * ...
     * #end
     *
     * @param size
     * @return
     */
    public int[] newIterator(final int size) {
        final int[] items = new int[size];
        for (int i = 0; i < size; i++) {
            items[i] = i;
        }
        return items;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    // ------------------------------------------------------------------------
    //                      CLASS
    // ------------------------------------------------------------------------
    public class Counter {

        private final SysTool _sys;
        private int _count = 0;

        public Counter(final SysTool sys, final int start) {
            _sys = sys;
            _count = start;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this._count;
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (null != obj) {
                return _count == _sys.size(obj);
            }
            return false;
        }

        public boolean gt(final Object obj) {
            if (null != obj) {
                return _count > _sys.size(obj);
            }
            return false;
        }

        public boolean lt(final Object obj) {
            if (null != obj) {
                return _count < _sys.size(obj);
            }
            return false;
        }

        /**
         * Compares its value with another value. Returns a negative integer,
         * zero, or a positive integer as the value is less than, equal to, or
         * greater than the argument.
         *
         * @param obj
         * @return
         */
        public int compare(final Object obj) {
            if (null != obj) {
                final int objval = _sys.size(obj);
                if (_count == objval) {
                    return 0;
                } else if (_count > objval) {
                    return 1;
                } else {
                    return -1;
                }
            }
            return 1;
        }

        public void reset() {
            _count = 0;
        }

        public void inc() {
            _count++;
        }

        public void inc(final int val) {
            _count += val;
        }

        public void dec() {
            _count--;
        }

        public void dec(final int val) {
            _count -= val;
        }

        public int getVal() {
            return _count;
        }
    }


}
