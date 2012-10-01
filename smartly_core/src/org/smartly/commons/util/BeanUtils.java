package org.smartly.commons.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility for bean management using reflection.
 */
public abstract class BeanUtils {

    public static final String PROXYPREFIX = "$";
    public static final String[] PROXYSUFFIX = new String[]{
            "$pcsubclass",
            "$proxy"
    };

    private static final String[] ID_FIELDS = new String[]{"_id", "id", "uid", "index", "name"};

    /**
     * All primitive classes
     */
    public static enum PrimitiveClasses {

        classBoolean(boolean.class, Boolean.class, Boolean.class.getName()),
        classByte(byte.class, Byte.class, Byte.class.getName()),
        classChar(char.class, Character.class, Character.class.getName()),
        classShort(short.class, Short.class, Short.class.getName()),
        classInt(int.class, Integer.class, Integer.class.getName()),
        classLong(long.class, Long.class, Long.class.getName()),
        classFloat(float.class, Float.class, Float.class.getName()),
        classDouble(double.class, Double.class, Double.class.getName());
        private final Class clazz;
        private final Class primitiveClazz;
        private final String className;

        PrimitiveClasses(Class primitive, Class cls, String name) {
            primitiveClazz = primitive;
            clazz = cls;
            className = name;
        }

        public Class getObjectClass() {
            return clazz;
        }

        public Class getPrimitiveClass() {
            return primitiveClazz;
        }

        public static boolean isPrimitive(Class aclass) {
            final PrimitiveClasses[] array = PrimitiveClasses.values();
            for (final PrimitiveClasses item : array) {
                if (item.getObjectClass().equals(aclass) || item.getPrimitiveClass().equals(aclass)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final Class[] PRIMITIVE_CLASSES = {
            boolean.class,
            byte.class,
            char.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class
    };

    /**
     * Return all primitive classes, plus String, Date.
     *
     * @return
     */
    public static Class[] getObjectClasses() {
        PrimitiveClasses[] values = PrimitiveClasses.values();
        List<Class> result = new ArrayList<Class>();
        for (int i = 0; i < values.length; i++) {
            PrimitiveClasses value = values[i];
            result.add(value.getObjectClass());
            result.add(value.getPrimitiveClass());
        }
        // add string, date
        result.add(String.class);
        result.add(Date.class);
        return result.toArray(new Class[result.size()]);
    }

    public static Class getObjectClassFromPrimitive(Class primitiveClass) {
        Class result = primitiveClass;
        if (isPrimitiveClass(primitiveClass)) {
            for (PrimitiveClasses cls : PrimitiveClasses.values()) {
                if (cls.getPrimitiveClass().equals(primitiveClass)) {
                    result = cls.getObjectClass();
                    break;
                }
            }
        }

        return result;
    }

    public static boolean isPrimitiveClass(final Object obj) {
        final Class clazz = obj.getClass();
        return isPrimitiveClass(clazz);
    }

    public static boolean isPrimitiveClass(final Class clazz) {
        for (final Class cls : PRIMITIVE_CLASSES) {
            if (cls.equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static String buildSET(final String fieldName) {
        String result;
        char[] arr = fieldName.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        result = "set" + new String(arr); //String.copyValueOf(arr);
        return result;
    }

    public static String buildGET(String fieldName) {
        String result;
        char[] arr = fieldName.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        result = "get" + new String(arr); //String.copyValueOf(arr);
        return result;
    }

    public static String getPropertyName(final String methodName) {
        final char[] name;
        if (methodName.startsWith("set")) {
            name = methodName.substring(3).toCharArray();
        } else if (methodName.startsWith("get")) {
            name = methodName.substring(3).toCharArray();
        } else if (methodName.startsWith("is")) {
            name = methodName.substring(2).toCharArray();
        } else {
            name = null;
        }
        if (null == name) {
            return null;
        }
        name[0] = toLowerCase(name[0]);
        return new String(name); //String.copyValueOf(name);
    }

    /**
     * Return value of a complex bean navigating its properties.
     *
     * @param instance JavaBean, Map, JSONObject, JSONArray, Array. i.e. "items
     *                 => [{"_id":"H","value":"1500"},{"_id":"W","value":"500"}]"
     * @param path     Propeties path. i.e. "items.H.value"
     * @return
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     *
     */
    public static Object getValue(final Object instance,
                                  final String path)
            throws IllegalAccessException, InvocationTargetException {
        return getPropertyValue(instance, path);
    }

    /**
     * Return value of a complex bean navigating its properties.
     *
     * @param instance JavaBean, Map, JSONObject, JSONArray, Array. i.e. "items
     *                 => [{"_id":"H","value":"1500"},{"_id":"W","value":"500"}]"
     * @param path     Propeties path. i.e. "items.H.value"
     * @return
     */
    public static Object getValueIfAny(final Object instance,
                                       final String path) {
        try {
            return getValue(instance, path);
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean hasValue(final Object instance,
                                   final String path, final Object value) {
        try {
            return getValue(instance, path).equals(value);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean setValue(final Object instance,
                                   final String fieldName, final Object value)
            throws IllegalAccessException, InvocationTargetException {
        return setPropertyValue(instance, fieldName, value);
    }

    public static boolean setValueIfAny(final Object instance,
                                        final String fieldName, final Object value) {
        try {
            return setPropertyValue(instance, fieldName, value);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static Field findField(final Object instance,
                                  final String name) {
        try {
            return instance.getClass().getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Return a method signature.<br> Specific to java, The signature of a
     * method should contain more details. It should contain<br> 1. Visibility
     * modifier (public, private, protected)<br> 2. Return type<br> 3. Name of
     * the method<br> 4. Arguments (type, order)<br> 5. Throws clause<br>
     */
    public static String methodSignature(final Method method) {
        final Class declaringClass = method.getDeclaringClass();
        final Class[] params = method.getParameterTypes();
        final String args = argSignature(params);
        Class retType = method.getReturnType();
        int mod = method.getModifiers();
        final String smod = Modifier.toString(mod);

        final StringBuilder result = new StringBuilder();
        result.append(smod).append(" ").append(retType.getName()).
                append(" ").append(declaringClass.getName()).append(".").
                append(method.getName()).append("(").append(args).append(")");
        return result.toString();
    }

    /**
     * Return a method signature.<br> Specific to java, The signature of a
     * method should contain more details. It should contain<br> 1. Visibility
     * modifier (public, private, protected)<br> 2. Return type<br> 3. Name of
     * the method<br> 4. Arguments (type, order)<br> 5. Throws clause<br>
     */
    public static String methodSignature(Class declaringClass, int mod, Class retType, String methodName, Class[] params, Class[] exceptions) {
        String args = argSignature(params);
        String smod = Modifier.toString(mod);

        StringBuilder result = new StringBuilder();
        result.append(smod).append(" ").append(retType.getName()).append(" ").append(declaringClass.getName()).append(".").append(methodName).append("(").append(args).append(")").append(" ").append(classToString(exceptions, ",", ""));
        return result.toString();
    }

    public static String argSignature(Method method) {
        Class[] params = method.getParameterTypes();
        return argSignature(params);
    }

    public static String argSignature(Object[] params) {
        return argSignature(toClassArray(params));
    }

    public static String argSignature(Class[] params) {
        final StringBuilder buf = new StringBuilder();
        if (null != params && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    buf.append(",");
                }
                buf.append(params[i].getName());
            }
        }
        return buf.toString();
    }

    public static Class[] toClassArray(Object[] params) {
        if (null == params) {
            return new Class[0];
        }
        Class[] classParams = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classParams[i] = params[i].getClass();
        }
        return classParams;
    }

    public static Method[] getPublicMethods(final Class clazz) {
        return getMethods(clazz, Modifier.PUBLIC);
    }

    public static Method[] getPrivateMethods(final Class clazz) {
        return getMethods(clazz, Modifier.PRIVATE);
    }

    public static Method[] getMethods(final Class clazz, final int modifier) {
        final List<Method> result = new LinkedList<Method>();
        final Method[] methods = clazz.getDeclaredMethods();
        if (null != methods && methods.length > 0) {
            for (final Method method : methods) {
                if (method.getModifiers() == modifier) {
                    result.add(method);
                }
            }
        }
        return result.toArray(new Method[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public static Method getMethodIfAny(final Class clazz,
                                        final String name) {
        try {
            return clazz.getMethod(name, new Class[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static Method getMethodIfAny(final Object instance,
                                        final String name, final Class[] params) {
        if (null != params && params.length > 0) {
            return getMethodIfAny(instance.getClass(), name, params);
        } else {
            return getMethodIfAny(instance.getClass(), name);
        }
    }

    public static Method getMethodIfAny(Object instance, String name, Object[] params) {
        if (null != params && params.length > 0) {
            Class[] classParams = toClassArray(params);
            return getMethodIfAny(instance.getClass(), name, classParams);
        } else {
            return getMethodIfAny(instance.getClass(), name);
        }
    }

    public static Method getMethodIfAny(Class clazz, String name, Object[] params) {
        if (null != params && params.length > 0) {
            Class[] classParams = toClassArray(params);
            return getMethodIfAny(clazz, name, classParams);
        } else {
            return getMethodIfAny(clazz, name);
        }
    }

    public static Method getMethodIfAny(Class clazz, String name, Class[] params) {
        return getMethodIfAny(clazz, name, params, true, false);
    }

    public static Method getMethodIfAnyAtLeastOne(Class clazz, String name, Class[] params) {
        return getMethodIfAny(clazz, name, params, true, true);
    }

    public static Class<?> getReturnType(Class clazz, String methodName) {
        Method method = getMethodIfAny(clazz, methodName);
        if (null != method) {
            return method.getReturnType();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static String[] getPropertyNames(final Class aclass,
                                            final Class... retTypeFilter) {
        final List<String> result = new ArrayList<String>();
        final Method[] methods = aclass.getMethods();
        for (Method method : methods) {
            final String name = method.getName();
            final Class retType = method.getReturnType();
            if (name.startsWith("get") || name.startsWith("is")) {
                if ((null == retTypeFilter
                        || retTypeFilter.length == 0)
                        || CollectionUtils.contains(retTypeFilter, retType)) {
                    //-- retrieve property name --//
                    final String propertyName = getPropertyName(name);
                    //-- does exist a setter? --//
                    try {
                        final Method setter = aclass.getMethod(
                                "set".concat(upperCaseFirstInitial(propertyName)),
                                new Class[]{method.getReturnType()});
                        if (null != setter && !result.contains(propertyName)) {
                            result.add(propertyName);
                        }
                    } catch (NoSuchMethodException ex) {
                    }
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    @SuppressWarnings("unchecked")
    public static boolean isAssignable(Object from, Class to) {
        if (null == from) {
            return false;
        }
        return to.isAssignableFrom(from.getClass());
    }

    // -----------------------------------------------------------------------
    //                  p r i v a t e
    // -----------------------------------------------------------------------
    private static Class[] getTypes(final Object[] objects) {
        final List<Class> result = new LinkedList<Class>();
        for (final Object object : objects) {
            result.add(object.getClass());
        }
        return result.toArray(new Class[result.size()]);
    }

    @SuppressWarnings("unchecked")
    private static Method getMethodIfAny(final Class clazz,
                                         final String name, final Class[] params,
                                         final boolean nearest, final boolean atleastone) {
        Method candidate = null;
        try {
            // try with standard method
            try {
                candidate = clazz.getMethod(name, params);
            } catch (Throwable ignored) {
            }
            if (null == candidate) {
                final Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    // has same name?
                    if (method.getName().equals(name)) {
                        Class[] methodParams = method.getParameterTypes();
                        // Are parameters compatible?
                        if (methodParams.length == params.length) {
                            boolean isAssignable = parametersMatch(methodParams, params, nearest);
                            if (isAssignable) {
                                candidate = method;
                                break; // found a good candidate. stop searching
                            } else if (atleastone) {
                                candidate = method; // at least has same name and parameters number
                            }
                        } else {
                            if (atleastone && null == candidate) {
                                candidate = method;
                            } // at least has same name
                        }
                    }
                }
            }
        } catch (Throwable t) {
        }

        return candidate;
    }

    private static boolean parametersMatch(Class<?>[] params1, Class<?>[] params2, boolean nearest) {
        if (params1.length != params2.length) {
            return false;
        }
        for (int i = 0; i < params1.length; i++) {
            final Class<?> param1 = params1[i];
            final Class<?> param2 = params2[i];
            if (null == param1 || null == param2) {
                return true;
            } else if (!param1.isAssignableFrom(param2)) {
                // parameter are not assignable
                if (nearest) {
                    if (!allowCast(param1, param2)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } // for - on parameters
        return true;
    }

    private static String upperCaseFirstInitial(String str) {
        if (str != null && str.length() >= 1) {
            while (str.startsWith("_")) {
                str = str.substring(1);
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str;
        }
    }

    private static String numberToString(Object instance) {
        String s = instance.toString();
        int i = s.lastIndexOf(".");
        if (i == -1) {
            return s;
        }
        String result = s.substring(0, i);

        return result;
    }

    private static List<Class> getInterfacesList(Class clazz) {
        List<Class> result = new ArrayList<Class>();
        Class[] interfaces = clazz.getInterfaces();
        if (null != interfaces && interfaces.length > 0) {
            for (Class intrfc : interfaces) {
                result.add(intrfc);
                // recursively checks childs
                result.addAll(getInterfacesList(intrfc));
            }
        }

        return result;
    }

    private static boolean allowCast(final Class<?> methodParamType,
                                     final Class<?> paramType) {
        try {
            if (methodParamType.isAssignableFrom(String.class)) {
                return true;
            } else if (methodParamType.isAssignableFrom(Long.class) || methodParamType.isAssignableFrom(Integer.class)) {
                if (paramType.isAssignableFrom(Long.class)
                        || paramType.isAssignableFrom(Integer.class)
                        || paramType.isAssignableFrom(Double.class)
                        || paramType.isAssignableFrom(Float.class)) {
                    return true;
                }
            } else if (methodParamType.isAssignableFrom(Boolean.class)
                    || methodParamType.isAssignableFrom(boolean.class)) {
                if (paramType.isAssignableFrom(Boolean.class)
                        || paramType.isAssignableFrom(boolean.class)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    private static String classToString(Class[] array, String separator, String defaultValue) {
        if (null == array) {
            return defaultValue;
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(array[i].getName());
            }
            return result.toString();
        }
    }

    private static Method findGetter(final Object instance,
                                     final String fieldName) {
        final String name = upperCaseFirstInitial(fieldName);
        Method result = getMethodIfAny(instance, "get".concat(name), new Class[0]);
        if (result == null) {
            result = getMethodIfAny(instance, "is".concat(name), new Class[0]);
        }
        return result;
    }

    private static Method findSetter(final Object instance,
                                     final String fieldName, final Class paramType) {
        final Class[] params = new Class[]{paramType};
        final String name = upperCaseFirstInitial(fieldName);
        Method result = getMethodIfAnyAtLeastOne(
                instance.getClass(), "set".concat(name), params);
        if (result == null) {
            result = getMethodIfAnyAtLeastOne(
                    instance.getClass(), "is".concat(name), params);
        }
        return result;
    }

    private static Object getPropertyValue(final Object instance,
                                           final String path)
            throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        if (StringUtils.hasText(path)) {
            final String[] tokens = StringUtils.split(
                    path, ".");
            result = instance;
            for (final String token : tokens) {
                if (null != result) {
                    if (result instanceof JSONArray) {
                        result = getItemOfArray((JSONArray) result, null, token);
                    } else if (result.getClass().isArray()) {
                        result = getItemOfArray((Object[]) result, null, token);
                    } else if (result instanceof List) {
                        result = getItemOfList((List) result, null, token);
                    } else {
                        result = getSimplePropertyValue(result, token);
                    }
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private static Object getItemOfArray(final Object[] array,
                                         final String fieldName, final Object fieldValue)
            throws IllegalAccessException, InvocationTargetException {
        for (final Object item : array) {
            if (null != item) {
                if (StringUtils.hasText(fieldName)) {
                    final Object value = getSimplePropertyValue(item, fieldName);
                    if (CompareUtils.equals(value, fieldValue)) {
                        return item;
                    }
                } else {
                    // try with some standard field names
                    for (final String fname : ID_FIELDS) {
                        final Object value = getSimplePropertyValue(item, fname);
                        if (CompareUtils.equals(value, fieldValue)) {
                            return item;
                        }
                    }
                }
            }

        }
        return null;
    }

    private static Object getItemOfList(final List list,
                                        final String fieldName, final Object fieldValue)
            throws IllegalAccessException, InvocationTargetException {
        for (final Object item : list) {
            if (null != item) {
                if (StringUtils.hasText(fieldName)) {
                    final Object value = getSimplePropertyValue(item, fieldName);
                    if (CompareUtils.equals(value, fieldValue)) {
                        return item;
                    }
                } else {
                    // try with some standard field names
                    for (final String fname : ID_FIELDS) {
                        final Object value = getSimplePropertyValue(item, fname);
                        if (CompareUtils.equals(value, fieldValue)) {
                            return item;
                        }
                    }
                }
            }

        }
        return null;
    }

    private static Object getItemOfArray(final JSONArray array,
                                         final String fieldName, final Object fieldValue)
            throws IllegalAccessException, InvocationTargetException {
        final int len = array.length();
        for (int i = 0; i < len; i++) {
            final Object item = array.opt(i);
            if (null != item) {
                if (StringUtils.hasText(fieldName)) {
                    try {
                        final Object value = getSimplePropertyValue(item, fieldName);
                        if (CompareUtils.equals(value, fieldValue)) {
                            return item;
                        }
                    } catch (Throwable t) {
                        continue;
                    }
                } else {
                    // try with some standard field names
                    for (final String fname : ID_FIELDS) {
                        try {
                            final Object value = getSimplePropertyValue(item, fname);
                            if (CompareUtils.equals(value, fieldValue)) {
                                return item;
                            }
                        } catch (Throwable t) {
                            continue;
                        }
                    }
                }
            }

        }
        return null;
    }

    private static Object getSimplePropertyValue(final Object instance,
                                                 final String fieldName)
            throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        if (null != instance) {
            if (instance instanceof Map) {
                result = ((Map) instance).get(fieldName);
            } else if (instance instanceof JSONObject) {
                result = ((JSONObject) instance).opt(fieldName);
            } else {
                final Field field = findField(instance, fieldName);
                if (null != field) {
                    result = field.get(instance);
                } else {
                    final Method getter = findGetter(instance, fieldName);
                    if (null != getter) {
                        result = getter.invoke(instance, new Object[0]);
                    }
                }
            }
        }
        return result;
    }

    private static boolean setPropertyValue(final Object instance,
                                            final String path,
                                            final Object value)
            throws IllegalAccessException, InvocationTargetException {
        Object propertyBean = instance;
        String fieldName = path;
        if (StringUtils.hasText(path)) {
            final String[] tokens = StringUtils.split( path, "." );
            if (tokens.length > 1) {
                final String[] a = CollectionUtils.removeTokenFromArray(tokens,
                        tokens.length - 1);
                final String newpath = CollectionUtils.toDelimitedString(a, ".");
                propertyBean = getPropertyValue(instance, newpath);
                fieldName = CollectionUtils.getLast(tokens);
            }
        }
        return setSimplePropertyValue(propertyBean, fieldName, value);
    }

    @SuppressWarnings("unchecked")
    private static boolean setSimplePropertyValue(final Object instance,
                                                  final String fieldName, final Object value)
            throws IllegalAccessException, InvocationTargetException {
        boolean result = false;
        if (null != instance) {
            if (instance instanceof Map) {
                ((Map) instance).put(fieldName, value);
                result = true;
            } else if (instance instanceof JSONObject) {
                try {
                    ((JSONObject) instance).putOpt(fieldName, value);
                    result = true;
                } catch (Throwable t) {
                }
            } else {
                // try retrieve field
                final Field field = findField(instance, fieldName);
                if (null != field) {
                    field.set(instance, value);
                    result = true;
                } else {
                    final Class paramType = null != value ? value.getClass() : null;
                    final Method setter = findSetter(instance, fieldName, paramType);
                    if (null != setter) {
                        // is parameter of requested type?
                        Object parameterValue = value;
                        final Class[] types = setter.getParameterTypes();
                        if (types.length == 1) {
                            final Class type = types[0];
                            if (!type.equals(paramType)) {
                                try {
                                    parameterValue = toType(value, type);
                                } catch (Throwable ignored) {
                                }
                            }
                        }
                        //-- invoke set method passing parameter value --//
                        setter.invoke(instance, new Object[]{parameterValue});
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private static <T> T toType(final Object object, final Class<T> type) throws Exception {
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
    private static <T> T toType(final Object object, final Class<T> type,
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
                    i = Integer.parseInt(so);//Integer.valueOf(so);
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
            } else if (type.equals(Date.class)) {
                Date dt = toDate(so, dateFormat);
                result = ((Class<T>) Date.class).cast(dt);
            } else { //hard cast:
                result = type.cast(object);
            }
        }

        return result;
    }//toType()

    private static Date toDate(String inputDate, String inputDateFormat) {
        final SimpleDateFormat format = new SimpleDateFormat(inputDateFormat);
        Date dt;
        try {
            if (StringUtils.hasText(inputDate)) {
                dt = format.parse(inputDate);
            } else {
                dt = DateUtils.zero();
            }
        } catch (Exception e) {
            dt = DateUtils.zero();
        }
        return dt;
    }

    private static char toUpperCase(final char c) {
        final String text = new String(new char[]{c});
        return text.toUpperCase().charAt(0);
    }

    private static char toLowerCase(final char c) {
        final String text = new String(new char[]{c});
        return text.toLowerCase().charAt(0);
    }
}
