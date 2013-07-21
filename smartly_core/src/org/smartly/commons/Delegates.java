package org.smartly.commons;

import org.smartly.commons.async.Async;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.FormatUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Common listeners repository.
 */
public class Delegates {

    // --------------------------------------------------------------------
    //               F u n c t i o n   D e l e g a t e s
    // --------------------------------------------------------------------

    public static interface CreateRunnableCallback {
        Runnable handle(final int index, final int length);
    }

    // --------------------------------------------------------------------
    //               E v e n t s
    // --------------------------------------------------------------------

    public static interface ExceptionCallback {
        void handle(final String message, final Throwable exception);
    }

    /**
     * Simple handler for Async Action
     */
    public static interface AsyncActionHandler {
        void handle(Object... args);
    }

    /**
     * Callback for progress indicators.
     */
    public static interface ProgressCallback {
        void handle(final int index, final int length, final double progress);
    }

    // --------------------------------------------------------------------
    //               E v e n t   H a n d l e r s    P o o l
    // --------------------------------------------------------------------

    public static final class Handlers {

        private final Map<Class, List<Object>> _handlers;

        public Handlers() {
            _handlers = Collections.synchronizedMap(new HashMap<Class, List<Object>>());
        }

        public void clear() {
            synchronized (_handlers) {
                if (!_handlers.isEmpty()) {
                    final Collection<List<Object>> values = _handlers.values();
                    for (final List<Object> list : values) {
                        list.clear();
                    }
                }
                _handlers.clear();
            }
        }

        public int size() {
            return _handlers.size();
        }

        public int size(final Class hclass) {
            synchronized (_handlers) {
                return _handlers.containsKey(hclass) ? _handlers.get(hclass).size() : 0;
            }
        }

        public boolean contains(final Class hclass) {
            return _handlers.containsKey(hclass);
        }

        public void add(final Object handler) {
            if (null != handler) {
                final Class hclass = this.getInterfaceClass(handler.getClass());
                this.add(hclass, handler);
            }
        }


        public void triggerAsync(final Class hclass, final Object... args) {
            synchronized (_handlers) {
                if (_handlers.containsKey(hclass)) {
                    this.trigger(true, _handlers.get(hclass), args);
                } else {
                    this.getLogger().fine(FormatUtils.format("No handlers of type '{0}'!", hclass.getName()));
                }
            }
        }

        public void trigger(final Class hclass, final Object... args) {
            synchronized (_handlers) {
                if (_handlers.containsKey(hclass)) {
                    this.trigger(false, _handlers.get(hclass), args);
                } else {
                    this.getLogger().fine(FormatUtils.format("No handlers of type '{0}'!", hclass.getName()));
                }
            }
        }

        //--   p r i v a t e   --//

        private Logger getLogger() {
            return LoggingUtils.getLogger(this);
        }

        private void add(final Class hclass, final Object handler) {
            synchronized (_handlers) {
                if (!_handlers.containsKey(hclass)) {
                    final List<Object> list = new LinkedList<Object>();
                    _handlers.put(hclass, list);
                }
                _handlers.get(hclass).add(handler);
            }
        }

        private void trigger(final boolean async, final List<Object> handlers,
                             final Object... args) {
            for (final Object handler : handlers) {
                final Class hclass = this.getInterfaceClass(handler);
                final Method method = null != args && args.length > 0
                        ? BeanUtils.getMethodIfAny(hclass, "handle", args)
                        : BeanUtils.getMethodIfAny(hclass, "handle");
                if (null != method) {
                    if (async) {
                        Async.Action(new AsyncActionHandler() {
                            @Override
                            public void handle(final Object... args2) {
                                try {
                                    method.invoke(handler, args2);
                                } catch (Throwable t) {
                                    // manage execution error
                                }
                            }
                        }, args);
                    } else {
                        try {
                            method.invoke(handler, args);
                        } catch (Throwable t) {
                            // manage execution error
                        }
                    }
                }
            }
        }

        private Class getInterfaceClass(final Object instance) {
            return null != instance
                    ? this.getInterfaceClass(instance.getClass())
                    : null;
        }

        private Class getInterfaceClass(final Class hclass) {
            try {
                return hclass.getInterfaces()[0];
            } catch (Throwable ignored) {
            }
            return null;
        }
    }

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    private Delegates() {
    }

}
