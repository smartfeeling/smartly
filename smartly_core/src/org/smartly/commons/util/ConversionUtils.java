/*
 * ConversionUtil.java
 *
 */
package org.smartly.commons.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Angelo Geminiani ( angelo.geminiani@gmail.com )
 */
public abstract class ConversionUtils {

    public static final long KBYTE = 1024L;
    public static final long MBYTE = KBYTE * 1024L;

    public static double bytesToMbyte(long bytes) {
        return bytes / MBYTE;
    }

    public static double bytesToKbyte(long bytes) {
        return bytes / KBYTE;
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

    public static Boolean toBoolean(final Object val, final Boolean defValue) {
        if (null == val) {
            return defValue;
        }
        String s = val.toString();
        Boolean result = defValue;
        try {
            result = Boolean.parseBoolean(s);
        } catch (Throwable t) {
            result = defValue;
        }
        return result;
    }

    private static Date toDate(String inputDate, String inputDateFormat) {
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
}
