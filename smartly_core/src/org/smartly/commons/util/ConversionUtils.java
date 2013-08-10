/*
 * ConversionUtil.java
 *
 */
package org.smartly.commons.util;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.*;
import java.util.*;

/**
 * @author Angelo Geminiani ( angelo.geminiani@gmail.com )
 */
public abstract class ConversionUtils {

    public static final double KBYTE = 1024L;
    public static final double MBYTE = KBYTE * 1024L;

    private static final int STYLE_NUMBER = 0;
    private static final int STYLE_CURRENCY = 1;
    private static final int STYLE_PERCENT = 2;
    //NOTE: '3' belongs to a non-public "scientific" style
    private static final int STYLE_INTEGER = 4;

    /**
     * Returns a {@link NumberFormat} instance for the specified
     * format and {@link Locale}.  If the format specified is a standard
     * style pattern, then a number instance
     * will be returned with the number style set to the
     * specified style.  If it is a custom format, then a customized
     * {@link NumberFormat} will be returned.
     *
     * @param format the custom or standard formatting pattern to be used
     * @param locale the {@link Locale} to be used
     * @return an instance of {@link NumberFormat}
     * @see NumberFormat
     */
    public static NumberFormat getNumberFormat(String format, Locale locale) {
        if (format == null || locale == null) {
            return null;
        }

        NumberFormat nf = null;
        int style = getNumberStyleAsInt(format);
        if (style < 0) {
            // we have a custom format
            nf = new DecimalFormat(format, new DecimalFormatSymbols(locale));
        } else {
            // we have a standard format
            nf = getNumberFormat(style, locale);
        }
        return nf;
    }

    /**
     * Returns a {@link NumberFormat} instance for the specified
     * number style and {@link Locale}.
     *
     * @param numberStyle the number style (number will be ignored if this is
     *                    less than zero or the number style is not recognized)
     * @param locale      the {@link Locale} to be used
     * @return an instance of {@link NumberFormat} or <code>null</code>
     *         if an instance cannot be constructed with the given
     *         parameters
     */
    public static NumberFormat getNumberFormat(int numberStyle, Locale locale) {
        try {
            NumberFormat nf;
            switch (numberStyle) {
                case STYLE_NUMBER:
                    nf = NumberFormat.getNumberInstance(locale);
                    break;
                case STYLE_CURRENCY:
                    nf = NumberFormat.getCurrencyInstance(locale);
                    break;
                case STYLE_PERCENT:
                    nf = NumberFormat.getPercentInstance(locale);
                    break;
                case STYLE_INTEGER:
                    nf = NumberFormat.getIntegerInstance(locale);
                    break;
                default:
                    // invalid style was specified, return null
                    nf = null;
            }
            return nf;
        } catch (Exception suppressed) {
            // let it go...
            return null;
        }
    }

    /**
     * Checks a string to see if it matches one of the standard
     * NumberFormat style patterns:
     * number, currency, percent, integer, or default.
     * if it does it will return the integer constant for that pattern.
     * if not, it will return -1.
     *
     * @param style the string to be checked
     * @return the int identifying the style pattern
     * @see NumberFormat
     */
    public static int getNumberStyleAsInt(String style) {
        // avoid needlessly running through all the string comparisons
        if (style == null || style.length() < 6 || style.length() > 8) {
            return -1;
        }
        if (style.equalsIgnoreCase("default")) {
            //NOTE: java.text.NumberFormat returns "number" instances
            //      as the default (at least in Java 1.3 and 1.4).
            return STYLE_NUMBER;
        }
        if (style.equalsIgnoreCase("number")) {
            return STYLE_NUMBER;
        }
        if (style.equalsIgnoreCase("currency")) {
            return STYLE_CURRENCY;
        }
        if (style.equalsIgnoreCase("percent")) {
            return STYLE_PERCENT;
        }
        if (style.equalsIgnoreCase("integer")) {
            return STYLE_INTEGER;
        }
        // ok, it's not any of the standard patterns
        return -1;
    }


    // ----------------- number conversion methods ---------------

    public static BigDecimal toBigDecimal(final Object value) {
        try {
            final Number number = toNumber(value);
            return new BigDecimal(number.toString());
        } catch (Throwable ignored) {
        }
        return BigDecimal.ZERO;
    }

    /**
     * Attempts to convert an unidentified {@link Object} into a {@link Number},
     * just short of turning it into a string and parsing it.  In other words,
     * this will convert to {@link Number} from a {@link Number}, {@link Calendar},
     * or {@link Date}.  If it can't do that, it will get the string value and have
     * {@link #toNumber(String, String, Locale)} try to parse it using the
     * default Locale and format.
     *
     * @param obj - the object to convert
     */
    public static Number toNumber(final Object obj) {
        return toNumber(obj, true);
    }

    /**
     * Just like {@link #toNumber(Object)} except that you can tell
     * this to attempt parsing the object as a String by passing {@code true}
     * as the second parameter.  If you do so, then it will have
     * {@link #toNumber(String, String, Locale)} try to parse it using the
     * default Locale and format.
     */
    public static Number toNumber(Object obj, boolean handleStrings) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Number) {
            return (Number) obj;
        }
        if (obj instanceof Date) {
            return ((Date) obj).getTime();
        }
        if (obj instanceof Calendar) {
            Date date = ((Calendar) obj).getTime();
            return date.getTime();
        }
        if (handleStrings) {
            // try parsing with default format and locale
            return toNumber(obj.toString(), "default", Locale.getDefault());
        }
        return null;
    }

    /**
     * Converts a string to an instance of {@link Number} using the
     * specified format and {@link Locale} to parse it.
     *
     * @param value  - the string to convert
     * @param format - the format the number is in
     * @param locale - the {@link Locale}
     * @return the string as a {@link Number} or <code>null</code> if no
     *         conversion is possible
     * @see NumberFormat#parse
     */
    public static Number toNumber(String value, String format, Locale locale) {
        if (value == null || format == null || locale == null) {
            return null;
        }
        try {
            NumberFormat parser = getNumberFormat(format, locale);
            return parser.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts an object to an instance of {@link Number} using the
     * specified format and {@link Locale} to parse it, if necessary.
     *
     * @param value  - the object to convert
     * @param format - the format the number is in
     * @param locale - the {@link Locale}
     * @return the object as a {@link Number} or <code>null</code> if no
     *         conversion is possible
     * @see NumberFormat#parse
     */
    public static Number toNumber(Object value, String format, Locale locale) {
        // first try the easy stuff
        Number number = toNumber(value, false);
        if (number != null) {
            return number;
        }

        // turn it into a string and try parsing it
        return toNumber(String.valueOf(value), format, locale);
    }

    /**
     * Convert any value in a "long" number
     *
     * @param val Any value representing a number
     * @return a Long number.
     */
    public static long toLong(final Object val) {
        return toLong(val, 0L);
    }

    /**
     * Convert any value in a "long" number
     *
     * @param val      Any value representing a number
     * @param defValue Value
     * @return a Long number.
     */
    public static long toLong(final Object val, final Long defValue) {
        if (null == val) {
            return defValue;
        }
        String s = val.toString();
        int i = s.indexOf('.');
        if (i > 0) {
            s = s.substring(0, i);
        }

        try {
            return Long.parseLong(s);
        } catch (Throwable t) {
            return defValue;
        }
    }

    /**
     * Convert any value in an "Integer" number
     *
     * @param val Any value representing a number
     * @return an integer value
     */
    public static int toInteger(final Object val) {
        return toInteger(val, 0);
    }

    /**
     * Convert any value in an "Integer" number
     *
     * @param val      Any value representing a number
     * @param defValue Value
     * @return an integer value
     */
    public static int toInteger(final Object val, final Integer defValue) {
        if (null == val) {
            return defValue;
        }
        String s = val.toString();
        int i = s.indexOf('.');
        if (i > 0) {
            s = s.substring(0, i);
        }

        try {
            return Integer.parseInt(s);
        } catch (Throwable t) {
            return defValue;
        }
    }

    public static Double toDouble(final Object val) {
        return toDouble(val, -1, 0d);
    }

    public static Double toDouble(final Object val, final int decimalPlace) {
        return toDouble(val, decimalPlace, 0d);
    }

    public static Double toDouble(final Object val,
                                  final int decimalPlace, final Double defValue) {
        if (null == val) {
            return defValue;
        }
        final String s = removeFormat(val.toString(), defValue.toString());

        try {
            Double result = Double.parseDouble(s);
            if (decimalPlace > -1) {
                BigDecimal bd = new BigDecimal(result);
                bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
                result = bd.doubleValue();
            }
            return result;
        } catch (Throwable t) {
            return defValue;
        }
    }

    public static Boolean toBoolean(final Object val) {
        return toBoolean(val, false);
    }

    /**
     * Converts any Object to a boolean using {@link #toString(Object)}
     * and {@link Boolean#valueOf(String)}.
     *
     * @param value the object to be converted
     * @return a {@link Boolean} object for the specified value or
     *         <code>null</code> if the value is null or the conversion failed
     */
    public static Boolean toBoolean(final Object value, final Boolean defValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        try {
            final String s = value.toString();
            return (s != null) ? Boolean.valueOf(s) : defValue;
        } catch (Throwable ignored) {
            return defValue;
        }
    }

    // -------------------------- DateFormat creation methods --------------

    /**
     * Returns a {@link DateFormat} instance for the specified
     * format, {@link Locale}, and {@link TimeZone}.  If the format
     * specified is a standard style pattern, then a date-time instance
     * will be returned with both the date and time styles set to the
     * specified style.  If it is a custom format, then a customized
     * {@link SimpleDateFormat} will be returned.
     *
     * @param format   the custom or standard formatting pattern to be used
     * @param locale   the {@link Locale} to be used
     * @param timezone the {@link TimeZone} to be used
     * @return an instance of {@link DateFormat}
     * @see SimpleDateFormat
     * @see DateFormat
     */
    public static DateFormat getDateFormat(String format, Locale locale,
                                           TimeZone timezone) {
        if (format == null) {
            return null;
        }

        DateFormat df = null;
        // do they want a date instance
        if (format.endsWith("_date")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = getDateStyleAsInt(fmt);
            df = getDateFormat(style, -1, locale, timezone);
        }
        // do they want a time instance?
        else if (format.endsWith("_time")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = getDateStyleAsInt(fmt);
            df = getDateFormat(-1, style, locale, timezone);
        }
        // ok, they either want a custom or date-time instance
        else {
            int style = getDateStyleAsInt(format);
            if (style < 0) {
                // we have a custom format
                df = new SimpleDateFormat(format, locale);
                df.setTimeZone(timezone);
            } else {
                // they want a date-time instance
                df = getDateFormat(style, style, locale, timezone);
            }
        }
        return df;
    }

    /**
     * Returns a {@link DateFormat} instance for the specified
     * date style, time style, {@link Locale}, and {@link TimeZone}.
     *
     * @param dateStyle the date style
     * @param timeStyle the time style
     * @param locale    the {@link Locale} to be used
     * @param timezone  the {@link TimeZone} to be used
     * @return an instance of {@link DateFormat}
     * @see #getDateFormat(int timeStyle, int dateStyle, Locale locale, TimeZone timezone)
     */
    public static DateFormat getDateFormat(final String dateStyle,
                                           final String timeStyle,
                                           final Locale locale,
                                           final TimeZone timezone) {
        int ds = getDateStyleAsInt(dateStyle);
        int ts = getDateStyleAsInt(timeStyle);
        return getDateFormat(ds, ts, locale, timezone);
    }

    /**
     * Returns a {@link DateFormat} instance for the specified
     * time style, date style, {@link Locale}, and {@link TimeZone}.
     *
     * @param dateStyle the date style (date will be ignored if this is
     *                  less than zero and the date style is not)
     * @param timeStyle the time style (time will be ignored if this is
     *                  less than zero and the date style is not)
     * @param locale    the {@link Locale} to be used
     * @param timezone  the {@link TimeZone} to be used
     * @return an instance of {@link DateFormat} or <code>null</code>
     *         if an instance cannot be constructed with the given
     *         parameters
     */
    public static DateFormat getDateFormat(final int dateStyle,
                                           final int timeStyle,
                                           final Locale locale,
                                           final TimeZone timezone) {
        try {
            DateFormat df;
            if (dateStyle < 0 && timeStyle < 0) {
                // no style was specified, use default instance
                df = DateFormat.getInstance();
            } else if (timeStyle < 0) {
                // only a date style was specified
                df = DateFormat.getDateInstance(dateStyle, locale);
            } else if (dateStyle < 0) {
                // only a time style was specified
                df = DateFormat.getTimeInstance(timeStyle, locale);
            } else {
                df = DateFormat.getDateTimeInstance(dateStyle, timeStyle,
                        locale);
            }
            df.setTimeZone(timezone);
            return df;
        } catch (Exception suppressed) {
            // let it go...
            return null;
        }
    }

    /**
     * Checks a string to see if it matches one of the standard DateFormat
     * style patterns: full, long, medium, short, or default.  If it does,
     * it will return the integer constant for that pattern.  If not, it
     * will return -1.
     *
     * @param style the string to be checked
     * @return the int identifying the style pattern
     * @see DateFormat
     */
    public static int getDateStyleAsInt(String style) {
        // avoid needlessly running through all the string comparisons
        if (style == null || style.length() < 4 || style.length() > 7) {
            return -1;
        }
        if (style.equalsIgnoreCase("full")) {
            return DateFormat.FULL;
        }
        if (style.equalsIgnoreCase("long")) {
            return DateFormat.LONG;
        }
        if (style.equalsIgnoreCase("medium")) {
            return DateFormat.MEDIUM;
        }
        if (style.equalsIgnoreCase("short")) {
            return DateFormat.SHORT;
        }
        if (style.equalsIgnoreCase("default")) {
            return DateFormat.DEFAULT;
        }
        // ok, it's not any of the standard patterns
        return -1;
    }


    // ----------------- date conversion methods ---------------

    /**
     * Attempts to convert an unidentified {@link Object} into a {@link Date},
     * just short of turning it into a string and parsing it.  In other words,
     * this will convert to {@link Date} from a {@link Date}, {@link Calendar},
     * or {@link Number}.  If it can't do that, it will return {@code null}.
     *
     * @param obj - the object to convert
     */
    public static Date toDate(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof Calendar) {
            return ((Calendar) obj).getTime();
        }
        if (obj instanceof Number) {
            Date d = new Date();
            d.setTime(((Number) obj).longValue());
            return d;
        }
        return null;
    }

    /**
     * Converts an object to an instance of {@link Date} using the
     * specified format, {@link Locale}, and {@link TimeZone} if the
     * object is not already an instance of Date, Calendar, or Long.
     *
     * @param obj      - the date to convert
     * @param format   - the format the date is in
     * @param locale   - the {@link Locale}
     * @param timezone - the {@link TimeZone}
     * @return the object as a {@link Date} or <code>null</code> if no
     *         conversion is possible
     * @see #getDateFormat
     * @see SimpleDateFormat#parse
     */
    public static Date toDate(final Object obj,
                              final String format,
                              final Locale locale,
                              final TimeZone timezone) {
        // first try the easy stuff
        final Date date = toDate(obj);
        if (date != null) {
            return date;
        }

        // turn it into a string and try parsing it
        return toDate(String.valueOf(obj), format, locale, timezone);
    }

    /**
     * Converts an object to an instance of {@link Date} using the
     * specified format, {@link Locale}, and {@link TimeZone} if the
     * object is not already an instance of Date, Calendar, or Long.
     *
     * @param str      - the string to parse
     * @param format   - the format the date is in
     * @param locale   - the {@link Locale}
     * @param timezone - the {@link TimeZone}
     * @return the string as a {@link Date} or <code>null</code> if the
     *         parsing fails
     * @see #getDateFormat
     * @see SimpleDateFormat#parse
     */
    public static Date toDate(final String str,
                              final String format,
                              final Locale locale,
                              final TimeZone timezone) {
        try {
            //try parsing w/a customized SimpleDateFormat
            final DateFormat parser = getDateFormat(format, locale, timezone);
            return parser.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static Calendar toCalendar(final Date date, final Locale locale) {
        if (date == null) {
            return null;
        }

        Calendar cal;
        if (locale == null) {
            cal = Calendar.getInstance();
        } else {
            cal = Calendar.getInstance(locale);
        }
        cal.setTime(date);
        // HACK: Force all fields to update. see link for explanation of this.
        //http://java.sun.com/j2se/1.4/docs/api/java/util/Calendar.html
        cal.getTime();
        return cal;
    }


    // ----------------- misc conversion methods ---------------

    /**
     * Converts objects to String.
     * Null returns null, Arrays and Collections return their first value,
     * or null if they have no values.
     *
     * @param value the object to be turned into a String
     * @return the string value of the object or null if the value is null
     *         or it is an array whose first value is null
     */
    public static String toString(final Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                // recurse on the first value
                return toString(Array.get(value, 0));
            }
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * Returns the first value as a String, if any; otherwise returns null.
     *
     * @param values the Collection to be turned into a string
     * @return the string value of the first object in the collection
     *         or null if the collection is empty
     */
    public static String toString(Collection values) {
        if (values != null && !values.isEmpty()) {
            // recurse on the first value
            return toString(values.iterator().next());
        }
        return null;
    }


    /**
     * Converts a string to a {@link URL}.  It will first try to
     * treat the string as a File name, then a classpath resource,
     * then finally as a literal URL.  If none of these work, then
     * this will return {@code null}.
     *
     * @param value - the string to parse
     * @return the {@link URL} form of the string or {@code null}
     * @see File
     * @see ClassLoaderUtils#getResource(String, Object)
     * @see URL
     */
    public static URL toURL(String value) {
        return toURL(value, ConversionUtils.class);
    }

    /**
     * Converts a string to a {@link URL}.  It will first try to
     * treat the string as a File name, then a classpath resource,
     * then finally as a literal URL.  If none of these work, then
     * this will return {@code null}.
     *
     * @param value  - the string to parse
     * @param caller - the object or Class seeking the url
     * @return the {@link URL} form of the string or {@code null}
     * @see File
     * @see ClassLoaderUtils#getResource(String, Object)
     * @see URL
     */
    public static URL toURL(String value, Object caller) {
        try {
            File file = new File(value);
            if (file.exists()) {
                return file.toURI().toURL();
            }
        } catch (Exception ignored) {
        }
        try {
            URL url = ClassLoaderUtils.getResource(value, caller);
            if (url != null) {
                return url;
            }
        } catch (Exception ignored) {
        }
        try {
            return new URL(value);
        } catch (Exception ignored) {
        }
        return null;
    }


    public static double bytesToMbyte(long bytes) {
        return (double)bytes / MBYTE;
    }

    public static double bytesToKbyte(long bytes) {
        return (double)bytes / KBYTE;
    }

    public static double bytesToMbyte(long bytes, final int decimals) {
        return MathUtils.round((double)bytes / MBYTE, decimals);
    }

    public static double bytesToKbyte(long bytes, final int decimals) {
        return MathUtils.round((double)bytes / KBYTE, decimals);
    }

    public static Charset toCharset(final Object val) {
        return toCharset(val, "UTF-8");
    }

    public static Charset toCharset(final Object val,
                                    final String defaultCharset) {
        // try to convert val
        try {
            if (null != val) {
                if (val instanceof Charset) {
                    return (Charset) val;
                } else {
                    return Charset.forName(val.toString());
                }
            }
        } catch (Throwable t) {
        }
        // try to convert default
        try {
            if (StringUtils.hasText(defaultCharset)) {
                return Charset.forName(defaultCharset);
            }
        } catch (Throwable t) {
        }
        // return system default charset
        return Charset.defaultCharset();
    }

    public static Object[] toTypes(final String[] valuesArray,
                                   final Class[] paramTypes) {
        final List<Object> result = new LinkedList<Object>();
        if (paramTypes.length == valuesArray.length) {
            for (int i = 0; i < paramTypes.length; i++) {
                final String value = valuesArray[i];
                final Class type = paramTypes[i];
                result.add(simpleConversion(value, type));
            }
        }
        return result.toArray(new Object[result.size()]);
    }

    public static Map<String, Object> toTypes(final Map<String, String> stringValues,
                                              final Map<String, Class> paramTypes) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (paramTypes.size() == stringValues.size()) {
            final Set<String> names = paramTypes.keySet();
            for (final String name : names) {
                final String value = stringValues.get(name);
                final Class type = paramTypes.get(name);
                result.put(name, simpleConversion(value, type));
            }
        }
        return result;
    }

    /**
     * Convert an instance to a specific type
     *
     * @param object   Instance to convert
     * @param typeName return type
     * @return Converted instance
     * @throws ClassNotFoundException Error
     */
    @SuppressWarnings("unchecked")
    public static <T> T toType(final Object object,
                               final String typeName) throws Exception {
        Class type = Class.forName(typeName);
        Object result = toType(object, type);
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T toType(final Object object,
                               final String typeName, final String dateFormat) throws Exception {
        Class type = Class.forName(typeName);
        Object result = toType(object, type, dateFormat);
        return (T) result;
    }

    public static <T> T toType(final Object object, final Class<T> type) throws Exception {
        return toType(object, type, "yyyyMMdd");
    }

    /**
     * Convert an instance to a specific type (kind of intelligent casting).
     * Note: you can set primitive types as input <i>type</i> but the return
     * type will be the corresponding wrapper type (e.g. Integer.TYPE will
     * result in Integer.class) with the difference that instead of a result
     * 'null' a numeric 0 (or boolean false) will be returned because primitive
     * types can't be null. <p> Supported simple destination types are: <ul>
     * <li>java.lang.Boolean, Boolean.TYPE (= boolean.class) <li>java.lang.Byte,
     * Byte.TYPE (= byte.class) <li>java.lang.Character, Character.TYPE (=
     * char.class) <li>java.lang.Double, Double.TYPE (= double.class)
     * <li>java.lang.Float, Float.TYPE (= float.class) <li>java.lang.Integer,
     * Integer.TYPE (= int.class) <li>java.lang.Long, Long.TYPE (= long.class)
     * <li>java.lang.Short, Short.TYPE (= short.class) <li>java.lang.String
     * <li>java.math.BigDecimal <li>java.math.BigInteger </ul>
     *
     * @param object Instance to convert.
     * @param type   Destination type (e.g. Boolean.class).
     * @return Converted instance/datatype/collection or null if input object is
     *         null.
     * @since 2.11.0
     */
    @SuppressWarnings("unchecked")
    public static <T> T toType(final Object object, final Class<T> type,
                               final String dateFormat) throws Exception {
        // allow direct cast?
        if (BeanUtils.isAssignable(object, type)) {
            return (T) object;
        }

        T result = null;
        if (object == null) {
            //initalize null values:
            if (type == Boolean.TYPE || type == Boolean.class) {
                result = ((Class<T>) Boolean.class).cast(false);
            } else if (type == Byte.TYPE || type == Byte.class) {
                result = ((Class<T>) Byte.class).cast(0);
            } else if (type == Character.TYPE || type == Character.class) {
                result = ((Class<T>) Character.class).cast(0);
            } else if (type == Double.TYPE || type == Double.class || type == BigDecimal.class) {
                result = ((Class<T>) Double.class).cast(0.0);
            } else if (type == Float.TYPE || type == Float.class) {
                result = ((Class<T>) Float.class).cast(0.0);
            } else if (type == Integer.TYPE || type == Integer.class || type == BigInteger.class) {
                result = ((Class<T>) Integer.class).cast(0);
            } else if (type == Long.TYPE || type == Long.class) {
                result = ((Class<T>) Long.class).cast(0);
            } else if (type == Short.TYPE || type == Short.class) {
                result = ((Class<T>) Short.class).cast(0);
            }
        } else {
            final String so = "" + object;

            //custom type conversions:
            if (type == BigDecimal.class) {
                result = type.cast(new BigDecimal(so));
            } else if (type == BigInteger.class) {
                result = type.cast(new BigInteger(so));
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                Boolean r = null;
                if ("1".equals(so) || "true".equalsIgnoreCase(so) || "yes".equalsIgnoreCase(so) || "on".equalsIgnoreCase(so)) {
                    r = Boolean.TRUE;
                } else if ("0".equals(object) || "false".equalsIgnoreCase(so) || "no".equalsIgnoreCase(so) || "off".equalsIgnoreCase(so)) {
                    r = Boolean.FALSE;
                } else {
                    r = Boolean.valueOf(so);
                }

                if (type == Boolean.TYPE) {
                    result = ((Class<T>) Boolean.class).cast(r); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(r);
                }
            } else if (type == Byte.class || type == Byte.TYPE) {
                Byte i = 0;
                if (so.equalsIgnoreCase("true") || so.equalsIgnoreCase("false")) {
                    if (so.equalsIgnoreCase("true")) {
                        i = -1;
                    }
                } else {
                    i = Byte.valueOf(so);
                }
                if (type == Byte.TYPE) {
                    result = ((Class<T>) Byte.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Character.class || type == Character.TYPE) {
                Character i = new Character(so.charAt(0));
                if (type == Character.TYPE) {
                    result = ((Class<T>) Character.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Double.class || type == Double.TYPE) {
                Double i = Double.valueOf(so);
                if (type == Double.TYPE) {
                    result = ((Class<T>) Double.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Float.class || type == Float.TYPE) {
                Float i = Float.valueOf(so);
                if (type == Float.TYPE) {
                    result = ((Class<T>) Float.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Integer.class || type == Integer.TYPE) {
                Integer i = 0;
                if (so.equalsIgnoreCase("true") || so.equalsIgnoreCase("false")) {
                    if (so.equalsIgnoreCase("true")) {
                        i = -1;
                    }
                } else {
                    i = toInteger(so);//Integer.valueOf(so);
                }
                if (type == Integer.TYPE) {
                    result = ((Class<T>) Integer.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Long.class || type == Long.TYPE) {
                Long i = Long.valueOf(so);
                if (type == Long.TYPE) {
                    result = ((Class<T>) Long.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type == Short.class || type == Short.TYPE) {
                Short i = 0;
                if (so.equalsIgnoreCase("true") || so.equalsIgnoreCase("false")) {
                    if (so.equalsIgnoreCase("true")) {
                        i = -1;
                    }
                } else {
                    i = Short.valueOf(so);
                }
                if (type == Short.TYPE) {
                    result = ((Class<T>) Short.class).cast(i); //avoid ClassCastException through autoboxing
                } else {
                    result = type.cast(i);
                }
            } else if (type.equals(Date.class) || type.equals(Timestamp.class)) {
                Date dt = toDate(so, dateFormat);
                result = ((Class<T>) Date.class).cast(dt);
            } else { //hard cast:
                result = type.cast(object);
            }
        }

        return result;
    }//toType()

    public static long[] toLongArray(final List<String> idList) {
        if (null == idList || idList.isEmpty()) {
            return new long[0];
        }
        long[] result = new long[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            result[i] = Long.parseLong(idList.get(i));
        }
        return result;
    }

    public static long[] toLongArray(String[] idList) {
        if (null == idList || idList.length == 0) {
            return new long[0];
        }
        long[] result = new long[idList.length];
        for (int i = 0; i < idList.length; i++) {
            result[i] = Long.parseLong(idList[i]);
        }
        return result;
    }

    public static Class sqlTypeToClassType(final Integer sqlType) throws SQLException {
        if (sqlType.equals(Types.ARRAY)) {
            return Array.class;
        } else if (sqlType.equals(Types.BIGINT)) {
            return BigInteger.class;
        } else if (sqlType.equals(Types.BINARY)) {
            return Object.class;
        } else if (sqlType.equals(Types.BIT)) {
            return Byte.class;
        } else if (sqlType.equals(Types.BLOB)) {
            return Object.class;
        } else if (sqlType.equals(Types.BOOLEAN)) {
            return Boolean.class;
        } else if (sqlType.equals(Types.CHAR)) {
            return Byte.class;
        } else if (sqlType.equals(Types.CLOB)) {
            return Object.class;
        } else if (sqlType.equals(Types.DATALINK)) {
            return null;
        } else if (sqlType.equals(Types.DATE)) {
            return Date.class;
        } else if (sqlType.equals(Types.DECIMAL)) {
            return BigDecimal.class;
        } else if (sqlType.equals(Types.DISTINCT)) {
            return null;
        } else if (sqlType.equals(Types.DOUBLE)) {
            return Double.class;
        } else if (sqlType.equals(Types.FLOAT)) {
            return Float.class;
        } else if (sqlType.equals(Types.INTEGER)) {
            return Integer.class;
        } else if (sqlType.equals(Types.JAVA_OBJECT)) {
            return Object.class;
        } else if (sqlType.equals(Types.LONGVARBINARY)) {
            return Object.class;
        } else if (sqlType.equals(Types.LONGVARCHAR)) {
            return String.class;
        } else if (sqlType.equals(Types.NULL)) {
            return null;
        } else if (sqlType.equals(Types.OTHER)) {
            return String.class;
        } else if (sqlType.equals(Types.REAL)) {
            return BigDecimal.class;
        } else if (sqlType.equals(Types.REF)) {
            return null;
        } else if (sqlType.equals(Types.SMALLINT)) {
            return Integer.class;
        } else if (sqlType.equals(Types.STRUCT)) {
            return null;
        } else if (sqlType.equals(Types.TIME)) {
            return Date.class;
        } else if (sqlType.equals(Types.TIMESTAMP)) {
            return Timestamp.class;
        } else if (sqlType.equals(Types.TINYINT)) {
            return Integer.class;
        } else if (sqlType.equals(Types.VARBINARY)) {
            return Object.class;
        } else if (sqlType.equals(Types.VARCHAR)) {
            return String.class;
        } else {
            throw new SQLException("Unsupported data type: " + sqlType.toString());
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static String removeFormat(final String s, final String defaultValue) {
        if (StringUtils.hasText(s)) {
            // 3.345,99   3,945.99
            String result = s;
            final int dotIndex = s.indexOf('.');
            final int commaIndex = s.indexOf(',');
            if (dotIndex > -1 && commaIndex > -1) {
                if (dotIndex < commaIndex) {
                    // 3.345,99
                    result = result.replaceAll("\\.", "");
                } else {
                    result = result.replaceAll(",", "");
                }
            }
            result = result.replaceAll(",", "\\.");
            return result.replaceAll(" ", "");
        }
        return defaultValue;
    }

    private static Object simpleConversion(final String value,
                                           final Class type) {
        if (type.equals(String.class)) {
            return null != value ? value.toString() : "";
        } else if (type.equals(Boolean.class)) {
            return null != value ? Boolean.parseBoolean(value) : false;
        } else if (type.equals(Long.class)) {
            return null != value ? Long.parseLong(value) : 0L;
        } else if (type.equals(Double.class)) {
            return null != value ? Double.parseDouble(value) : 0.0;
        } else if (type.equals(Integer.class)) {
            return null != value ? Integer.parseInt(value) : 0;
        } else if (type.equals(Object.class)) {
            return value;
        }
        return null;
    }

    private static Date toDate(final String inputDate, final String inputDateFormat) {
        final SimpleDateFormat format = new SimpleDateFormat(inputDateFormat);
        Date dt;
        try {
            if (StringUtils.hasText(inputDate)) {
                dt = format.parse(inputDate);
            } else {
                dt = DateUtils.zero();
            }
        } catch (ParseException e) {
            dt = DateUtils.zero();
        }
        return dt;
    }
}
