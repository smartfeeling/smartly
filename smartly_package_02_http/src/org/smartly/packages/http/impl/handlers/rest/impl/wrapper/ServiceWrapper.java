package org.smartly.packages.http.impl.handlers.rest.impl.wrapper;

import org.smartly.commons.util.FormatUtils;
import org.smartly.packages.http.impl.handlers.rest.impl.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * wrap a rest service into RESTRegistry
 */
public class ServiceWrapper {

    private final Class _class;
    private final Object _instance;
    private final Set<MethodWrapper> _methods;

    private String _path;

    public ServiceWrapper(final Class aclass) throws Exception {
        _methods = new HashSet<MethodWrapper>();
        _class = aclass;
        _instance = _class.newInstance();
        this.init(_class);
    }

    public String getPath() {
        return _path;
    }

    public boolean hasMethods() {
        return !_methods.isEmpty();
    }

    public MethodWrapper[] getMethods() {
        return _methods.toArray(new MethodWrapper[_methods.size()]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final Class aclass) throws Exception {
        final Path path = (Path) aclass.getAnnotation(Path.class);
        _path = path.value();

        //-- methods --//
        final Method[] methods = aclass.getDeclaredMethods();
        for (final Method m : methods) {
            if (isValid(m)) {
                final MethodWrapper mw = new MethodWrapper(_instance, m);
                if (!_methods.contains(mw)) {
                    _methods.add(mw);
                } else {
                    // method already exists
                    final String msg = FormatUtils.format("METHOD '{0}' ALREADY EXISTS! " +
                            "Another method with same path already exists in service '{1}'",
                            mw.getPath(), _class.getName());
                    throw new Exception(msg);
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static boolean isValid(final Method method) {
        if (null != method) {
            final Annotation[] annotations = method.getDeclaredAnnotations();
            if (annotations.length > 0) {
                for (final Annotation a : annotations) {
                    if (isValid(a)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isValid(final Annotation a) {
        return a instanceof Path ||
                a instanceof GET ||
                a instanceof POST ||
                a instanceof DELETE ||
                a instanceof PUT;
    }
}
