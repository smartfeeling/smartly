/*
 * 
 */
package org.smartly.packages.http.impl.serialization.json.serializer;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author angelo.geminiani
 */
public class BeanData {

    /**
     * The readable properties of the bean.
     */
    private Map<String, Method> _readableProps;
    /**
     * The writable properties of the bean.
     */
    private Map<String, Method> _writableProps;

    public BeanData() {
        _readableProps = new HashMap<String, Method>();
        _writableProps = new HashMap<String, Method>();
    }

    public BeanData(final Class clazz) {
        this();
        this.analyzeBean(clazz, true, false);
    }

    public BeanData(final Object instance) {
        this();
        this.analyzeBean(instance.getClass(), true, false);
    }

    public Map<String, Method> getReadableProps() {
        return _readableProps;
    }

    public Map<String, Method> getWritableProps() {
        return _writableProps;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getlogger() {
        return LoggingUtils.getLogger(this);
    }

    /**
     * Analyses a bean, returning a BeanData with the data extracted from it.
     *
     * @param clazz The class of the bean to analyse
     */
    private void analyzeBean(final Class clazz,
                             final boolean onlyrwproperties, boolean includeSuperClass) {
        try {
            final Map<String, Method[]> methods = this.getProperties(clazz,
                    onlyrwproperties, includeSuperClass);
            final Set<String> properties = methods.keySet();
            for (final String property : properties) {
                final Method[] pair = methods.get(property);
                if (null != pair[0]) {
                    _readableProps.put(property, pair[0]);
                }
                if (null != pair[1]) {
                    _writableProps.put(property, pair[0]);
                }
            }
        } catch (Throwable t) {
            this.getlogger().log(Level.SEVERE, null, t);
        }
    }

    private Map<String, Method[]> getProperties(final Class klass,
                                                final boolean onlyrwproperties, boolean includeSuperClass) {
        final Map<String, Method[]> result = new HashMap<String, Method[]>();

        if (klass.getClassLoader() == null) {
            includeSuperClass = false;
        }
        final Method[] methods = includeSuperClass
                ? klass.getMethods()
                : klass.getDeclaredMethods();
        // add methods to map
        for (int i = 0; i < methods.length; i++) {
            try {
                final Method method = methods[i];
                if (this.isValidMethod(method)) {
                    final String name = method.getName();
                    final String key = this.getPropertyName(name);
                    if (key.length() > 0) {
                        if (!result.containsKey(key)) {
                            result.put(key, new Method[]{null, null});
                        }
                        final Method[] pair = result.get(key);
                        if (name.startsWith("get") || name.startsWith("is")) {
                            pair[0] = method;
                        } else if (name.startsWith("set")) {
                            pair[1] = method;
                        }
                    }
                }
            } catch (Throwable t) {
            }
        }

        // remove invalid methods
        if (onlyrwproperties) {
            final String[] keys = result.keySet().toArray(new String[result.keySet().size()]);
            for (final String key : keys) {
                final Method[] pair = result.get(key);
                if (null == pair[0] || null == pair[1]) {
                    result.remove(key);
                }
            }
        }

        return result;
    }

    private String getPropertyName(final String methodName) {
        String key = "";
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            key = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            key = methodName.substring(2);
        }
        if (key.length() > 0
                && Character.isUpperCase(key.charAt(0))) {
            if (key.length() == 1) {
                key = key.toLowerCase();
            } else if (!Character.isUpperCase(key.charAt(1))) {
                key = key.substring(0, 1).toLowerCase()
                        + key.substring(1);
            }

        }
        return key;
    }

    private boolean isValidMethod(final Method method) {
        if (Modifier.isPublic(method.getModifiers())) {
            if (method.getName().startsWith("is") || method.getName().startsWith("get")) {
                return method.getParameterTypes().length == 0;
            } else if (method.getName().startsWith("set")) {
                return method.getParameterTypes().length == 1;
            }
        }
        return false;
    }
}
