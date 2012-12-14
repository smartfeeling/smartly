package org.smartly.commons.lang.compilers;

import org.smartly.commons.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class CompilerRegistry {

    //-- repos: key=extension, value=compiler --//
    private final Map<String, Class<? extends ICompiler>> _classes;
    private final Map<String, ICompiler> _objects;

    private CompilerRegistry() {
        _classes = Collections.synchronizedMap(new HashMap<String, Class<? extends ICompiler>>());
        _objects = Collections.synchronizedMap(new HashMap<String, ICompiler>());
    }

    public void registerClass(final String ext, final Class<? extends ICompiler> compilerClass) {
        synchronized (_classes) {
            _classes.put(this.removeDot(ext), compilerClass);
        }
    }

    public void registerInstance(final String ext, final ICompiler compilerInstance) {
        synchronized (_objects) {
            _objects.put(this.removeDot(ext), compilerInstance);
        }
    }

    public void removeAll(final String ext) {
        synchronized (_objects) {
            _objects.remove(this.removeDot(ext));
        }
        synchronized (_classes) {
            _classes.remove(this.removeDot(ext));
        }
    }

    public ICompiler getCompiler(final String ext) {
        synchronized (_objects) {
            final String key = removeDot(ext);
            if (_objects.containsKey(key)) {
                return _objects.get(key);
            } else {
                //-- creates compile instance --//
                synchronized (_classes) {
                    final ICompiler instance = this.createCompiler(key);
                    if (null != instance) {
                        _objects.put(key, instance);
                    }
                    return instance;
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String removeDot(final String ext) {
        return StringUtils.replace(ext, ".", "");
    }

    private ICompiler createCompiler(final String key) {
        if (_classes.containsKey(key)) {
            try {
                return _classes.get(key).newInstance();
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static CompilerRegistry __instance;

    private static CompilerRegistry getInstance() {
        if (null == __instance) {
            __instance = new CompilerRegistry();
        }
        return __instance;
    }

    public static void register(final String ext, final Class<? extends ICompiler> compilerClass) {
        getInstance().registerClass(ext, compilerClass);
    }

    public static void register(final String ext, final ICompiler compilerInstance) {
        getInstance().registerInstance(ext, compilerInstance);
    }

    public static void remove(final String ext) {
        getInstance().removeAll(ext);
    }

    public static ICompiler get(final String ext) {
        return getInstance().getCompiler(ext);
    }
}
