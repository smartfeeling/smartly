package org.smartly.commons.util;


import org.smartly.commons.lang.CharEncoding;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unchecked")
public class ClassLoaderUtils {

    private static final String CHARSET = CharEncoding.getDefault();

    private ClassLoaderUtils() {
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Load a given resource.
     * <p/>
     * This method will try to load the resource using the following methods (in order):
     * <ul>
     * <li>Thread.currentThread().getContextClassLoader().getResource(name)</li>
     * <li>{@link ClassLoaderUtils}.class.getClassLoader().getResource(name)</li>
     * <li>{@link ClassLoaderUtils}.class.getResource(name)</li>
     * <li>caller.getClass().getResource(name) or, if caller is a Class,
     * caller.getResource(name)</li>
     * </ul>
     *
     * @param name   The name of the resource to load
     * @param caller The instance or {@link Class} calling this method
     */
    public static URL getResource(final String name, final Object caller) {
        URL url = getThreadContextLoader().getResource(name);
        if (url == null) {
            url = getClassLoader().getResource(name);
            if (url == null) {
                url = ClassLoaderUtils.class.getResource(name);
                if (url == null && caller != null) {
                    Class callingClass = caller.getClass();
                    if (callingClass == Class.class) {
                        callingClass = (Class) caller;
                    }
                    url = callingClass.getResource(name);
                }
            }
        }
        return url;
    }

    public static Class forName(final String className) throws ClassNotFoundException {
        return lookupForName(className, getThreadContextLoader());
    }

    //-- resource Methods --//

    public static InputStream getResourceAsStream(final String resourcePath) {
        final ClassLoader cl = getThreadContextLoader();
        return cl.getResourceAsStream(resourcePath);
    }

    public static InputStream getResourceAsStream(final ClassLoader classLoader, final String resourcePath) {
        final ClassLoader cl = null != classLoader ? classLoader : getThreadContextLoader();
        return cl.getResourceAsStream(resourcePath);
    }

    public static InputStream getResourceAsStream(final ClassLoader classLoader, final Class packageClass, final String resourceName) {
        final ClassLoader cl = null != classLoader ? classLoader : getThreadContextLoader();
        final String packagePath = PathUtils.getPackagePath(packageClass);
        final String resourcePath = PathUtils.join(packagePath, resourceName);
        return cl.getResourceAsStream(resourcePath);
    }

    public static String getResourceAsString(final String resourcePath) {
        return getString(getResourceAsStream(resourcePath), CHARSET);
    }

    public static String getResourceAsString(final ClassLoader classLoader, final String resourcePath) {
        return getString(getResourceAsStream(classLoader, resourcePath), CHARSET);
    }

    public static String getResourceAsString(final ClassLoader classLoader, final Class packageClass, final String resourceName) {
        return getString(getResourceAsStream(classLoader, packageClass, resourceName), CHARSET);
    }

    public static URL getResource(final ClassLoader classLoader, final Class packageClass, final String resourceName) {
        final ClassLoader cl = null != classLoader ? classLoader : getThreadContextLoader();
        final String packagePath = PathUtils.getPackagePath(packageClass);
        final String resourcePath = PathUtils.join(packagePath, resourceName);
        return cl.getResource(resourcePath);
    }

    public static URL getResource(final String resourcePath) {
        final ClassLoader cl = getThreadContextLoader();
        return cl.getResource(resourcePath);
    }

    public static String getResourceAsString(final String resourcePath,
                                             final String charset) {
        return getString(getResourceAsStream(resourcePath), charset);
    }

    public static String getResourceAsString(final ClassLoader classLoader,
                                             final String resourcePath,
                                             final String charset) {
        return getString(getResourceAsStream(classLoader, resourcePath), charset);
    }

    public static String getResourceAsString(final ClassLoader classLoader,
                                             final Class packageClass,
                                             final String resourceName,
                                             final String charset) {
        return getString(getResourceAsStream(classLoader, packageClass, resourceName), charset);
    }

    public static String getString(final InputStream is, final String charset) {
        if (null != is) {
            try {
                return new String(ByteUtils.getBytes(is), charset);
            } catch (Throwable ignored) {
            } finally {
                try {
                    is.close();
                } catch (Throwable ignored) {
                }
            }
        }
        return null;
    }

    //-- newInstance Methods --//

    @SuppressWarnings("unchecked")
    public static Object newInstance(final String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class cls = forName(className);
        if (null != cls) {
            return cls.newInstance();
        } else {
            throw new ClassNotFoundException("Unable to retrieve class [" + className
                    + "]. Please, check class full name is correct.");
        }
    }

    /**
     * Creates and intializes a new instance of the specified class with the
     * specified arguments.
     * <p/>
     * <p>Note only public constructors are searched.
     *
     * @param cls      the class of the instance to create
     * @param argTypes the argument types of the constructor to inovke
     * @param args     the arguments to initialize the instance
     * @return the new instance
     * @throws NoSuchMethodException  if a matching method is not found
     * @throws InstantiationException if the class that declares the
     *                                underlying constructor represents an abstract class
     * @throws java.lang.reflect.InvocationTargetException
     *                                if the underlying constructor throws
     *                                an exception
     * @see #newInstance(String, Class[], Object[])
     */
    @SuppressWarnings("unchecked")
    public static Object newInstance(final Class cls,
                                     final Class[] argTypes,
                                     final Object[] args) throws NoSuchMethodException, InstantiationException,
            InvocationTargetException, IllegalAccessException {
        final Constructor ctor;
        if (null == argTypes || argTypes.length == 0) {
            ctor = cls.getConstructor();
            return ctor.newInstance();
        } else {
            ctor = cls.getConstructor(argTypes);
            return ctor.newInstance(args);
        }
    }

    public static Object newInstance(final Class cls) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        return newInstance(cls, null);
    }

    public static Object newInstance(final Class cls,
                                     final Object[] args) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        if (null != args && args.length > 0) {
            final Class[] types = getTypes(args);
            final Constructor ctor = cls.getConstructor(types);
            return ctor.newInstance(args);
        }
        return cls.newInstance();
    }

    /**
     * Creates and initializes a new instance of the specified class name with
     * the specified arguments.
     * <p/>
     * <p>It uses Class.forName to get the class.
     *
     * @param clsName  the class name of the instance to create
     * @param argTypes the argument types of the constructor to inovke
     * @param args     the arguments to initialize the instance
     * @return the new instance
     * @throws NoSuchMethodException     if a matching method is not found
     * @throws InstantiationException    if the class that declares the
     *                                   underlying constructor represents an abstract class
     * @throws InvocationTargetException if the underlying constructor throws
     *                                   an exception
     * @throws ClassNotFoundException    if the specified class name is not a
     *                                   class
     * @see #newInstance(Class, Class[], Object[])
     */
    public static Object newInstance(final String clsName,
                                     final Class[] argTypes,
                                     final Object[] args)
            throws NoSuchMethodException, InstantiationException,
            InvocationTargetException, ClassNotFoundException, IllegalAccessException {
        final Class aclass = forName(clsName);
        return null != aclass
                ? newInstance(aclass, argTypes, args)
                : null;
    }


    public static <T> T optInstance(final String className) {
        try {
            return (T) newInstance(className);
        } catch (Throwable ignored) {
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private final static Map<String, Class> _cache = Collections.synchronizedMap(new HashMap<String, Class>());

    private static final ClassLoader getThreadContextLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static final ClassLoader getClassLoader() {
        return ClassLoaderUtils.class.getClassLoader();
    }

    private static final ClassLoader getCallerLoader(Object caller) {
        if (caller instanceof Class) {
            return ((Class) caller).getClassLoader();
        } else {
            return caller.getClass().getClassLoader();
        }
    }

    private static Class lookupForName(final String name,
                                       final ClassLoader classLoader) throws ClassNotFoundException {
        final Class result;
        if (_cache.containsKey(name)) {
            result = _cache.get(name);
        } else {
            // try with primitive
            final Class primitive = getPrimitiveClass(name);
            if (primitive != null) {
                result = primitive;
            } else {
                Class tmp = null;
                if (name.endsWith("[]")) {
                    // special handling for array class names
                    final String elementClassName = name.substring(0, name.length() - "[]".length());
                    final Class elementClass = lookupForName(elementClassName, classLoader);
                    tmp = Array.newInstance(elementClass, 0).getClass();
                }
                try {
                    if (null == tmp) {
                        tmp = Class.forName(name, true, classLoader);
                    }
                } catch (Throwable ignored) {
                }

                try {
                    if (null == tmp) {
                        tmp = Class.forName(name);
                    }
                } catch (Throwable ignored) {
                }

                try {
                    if (null == tmp) {
                        tmp = classLoader.loadClass(name);
                    }
                } catch (Throwable ignored) {
                }
                result = tmp;
            }
            //-- add to cache --//
            if (null != result) {
                synchronized (_cache) {
                    _cache.put(name, result);
                }
            }
        }
        return result;
    }

    private static Class getPrimitiveClass(final String name) {
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name.length() <= 8) {
            // could be a primitive - likely
            final BeanUtils.PrimitiveClasses[] primitives = BeanUtils.PrimitiveClasses.values();
            for (final BeanUtils.PrimitiveClasses primitive : primitives) {
                final Class clazz = primitive.getClass();
                if (clazz.getName().equals(name)) {
                    return clazz;
                }
            }
        }
        return null;
    }

    private static Class[] getTypes(final Object[] objects) {
        final List<Class> result = new LinkedList<Class>();
        for (final Object object : objects) {
            result.add(object.getClass());
        }
        return result.toArray(new Class[result.size()]);
    }

}
