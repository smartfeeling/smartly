/*
 * 
 */
package org.smartly.packages.http.impl.serialization.json.serializer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.http.impl.serialization.json.utils.JsonBeanUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author angelo.geminiani
 */
public class BeanSerializer {

    /**
     * Serialzie object wrapping it in a JsonBean
     *
     * @param object
     * @return
     */
    public JSONObject serialize(final Object object) {
        final JSONObject result = new JSONObject();
        try {
            if (null != object) {
                if (object instanceof Throwable) {
                    JsonBeanUtils.addError(result, (Throwable) object);
                } else {
                    final Object serialized = this.serialize(
                            new BeanSerializerState(), null, object);
                    JsonBeanUtils.addObject(result,
                            object.getClass().getName(), serialized);
                }
            }
        } catch (Throwable t) {
            JsonBeanUtils.addError(result, t);
        }
        return result;
    }

    /**
     * Serialize in a pure JSON raw object
     *
     * @param object
     * @return
     */
    public JSONObject rawSerialize(final Object object) {
        final Object serialized = this.serialize(
                new BeanSerializerState(), null, object);
        if (serialized instanceof JSONObject) {
            return (JSONObject) serialized;
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object serialize(final BeanSerializerState state,
                             final Object parent, final Object object) {
        if (null != object) {
            try {
                if (JsonBeanUtils.isNative(object)) {
                    // native (String, long, Long, int, Integer, ...)
                    return object;
                } else if (JsonBeanUtils.isDate(object)) {
                    // date
                    return ((Date) object).getTime();
                } else if (object instanceof Collection
                        || object.getClass().isArray()) {
                    // array or collection
                    return this.serializeCollection(
                            state, parent, object);
                } else if (object instanceof Map) {
                    return this.serializeMap(state, parent, (Map) object);
                } else if (object instanceof JSONObject) {
                    return this.serializeJSONObject(state, parent, (JSONObject) object);
                } else if (object instanceof JSONArray) {
                    return this.serializeJSONArray(state, parent, (JSONArray) object);
                } else if (StringUtils.isJSONObject(object)) {
                    return this.serializeObject(state, parent, object); //object.toString();
                } else if (StringUtils.isJSONArray(object)) {
                    return this.serializeArray(state, parent, object); //object.toString();
                } else {
                    // bean
                    return this.serializeBean(state, parent, object);
                }
            } catch (Throwable t) {
                return JsonBeanUtils.addError(new JSONObject(), t);
            }
        }
        return IConstants.NULL;    // empty object
    }

    private JSONArray serializeCollection(final BeanSerializerState state,
                                          final Object parent, final Object object) {
        if (object instanceof Collection) {
            final Collection collection = (Collection) object;
            return this.serializeCollection(state, parent,
                    collection.toArray(new Object[collection.size()]));
        } else if (object.getClass().isArray()) {
            return this.serializeCollection(state, parent, (Object[]) object);
        } else {
            return this.serializeCollection(state, parent, new Object[0]);
        }
    }

    private JSONArray serializeCollection(final BeanSerializerState state,
                                          final Object parent, final Object[] objects) {
        final JSONArray result = new JSONArray();
        if (objects.length > 0) {
            for (final Object object : objects) {
                this.putInArray(state, parent, object, result);
            }
        } else {
            return result.put(IConstants.NULL); // avoid empty array
        }

        return result;
    }

    private JSONObject serializeMap(final BeanSerializerState state,
                                    final Object parent, final Map map) {
        final JSONObject result = new JSONObject();
        final Set keys = map.keySet();
        for (final Object key : keys) {
            final Object object = map.get(key);
            this.putInObject(state, parent, key.toString(), object, result);
        }
        return result;
    }

    private JSONObject serializeObject(final BeanSerializerState state,
                                       final Object parent, final Object object) {
        JSONObject result = new JSONObject();
        try {
            result = new JSONObject(object.toString());
        } catch (Throwable t) {
            JsonBeanUtils.addError(result, t);
        }
        return result;
    }

    private Object serializeArray(final BeanSerializerState state,
                                  final Object parent, final Object object) {
        try {
            return new JSONArray(object.toString());
        } catch (Throwable t) {

            return JsonBeanUtils.addError(new JSONObject(), t);
        }
    }

    private JSONObject serializeJSONObject(final BeanSerializerState state,
                                           final Object parent, final JSONObject jsonobject) {
        final JSONObject result = new JSONObject();
        final JSONArray keys = jsonobject.names();
        if (null != keys) {
            for (int i = 0; i < keys.length(); i++) {
                final String key = keys.optString(i);
                final Object object = jsonobject.opt(key);
                this.putInObject(state, parent, key, object, result);
            }
        }
        return result;
    }

    private JSONArray serializeJSONArray(final BeanSerializerState state,
                                         final Object parent, final JSONArray jsonarray) {
        final JSONArray result = new JSONArray();
        for (int i = 0; i < jsonarray.length(); i++) {
            final Object object = jsonarray.opt(i);
            this.putInArray(state, parent, object, result);
        }
        return result;
    }

    private Object serializeBean(final BeanSerializerState state,
                                 final Object parent, final Object object) {
        final JSONObject result = new JSONObject();
        if (!state.isProcessed(object)) {
            final BeanProcessed processed = state.addProcessedBean(parent, object);
            final Map<String, Method> methods = processed.getBeanData().getReadableProps();
            final Set<String> names = methods.keySet();
            for (final String name : names) {
                final Method method = methods.get(name);
                Object resultValue;
                try {
                    resultValue = method.invoke(object, new Object[0]);
                    result.set(name, this.serialize(state, parent, resultValue));
                } catch (Throwable t) {
                    JsonBeanUtils.addError(result, t);
                }
            }
        } else {
            return state.getProcessedBean(object).getBeanId();
        }
        return result;
    }

    private void putInArray(final BeanSerializerState state, final Object parent,
                            final Object object, final JSONArray result) {
        if (null != object) {
            if (JsonBeanUtils.isNative(object)) {
                result.put(object);
                //} else if (object instanceof DBObject) {
                //    result.set(object.toString());
            } else {
                if (!state.isProcessed(object)) {
                    result.put(this.serialize(state, parent, object));
                } else {
                    result.put(state.getProcessedBean(object).getBeanId());
                }
            }
        }
    }

    private void putInObject(final BeanSerializerState state, final Object parent,
                             final String key, final Object object, final JSONObject result) {
        if (null != object) {
            try {
                if (JsonBeanUtils.isNative(object)) {
                    result.set(key.toString(), object);
                    //} else if (object instanceof DBObject) {
                    //    result.set(key.toString(), object.toString());
                } else {
                    if (!state.isProcessed(object)) {
                        result.set(key,
                                this.serialize(state, parent, object));
                    } else {
                        result.set(key,
                                state.getProcessedBean(object).getBeanId());
                    }
                }
            } catch (Throwable t) {
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static BeanSerializer __instance;

    public static BeanSerializer getInstance() {
        if (null == __instance) {
            __instance = new BeanSerializer();
        }
        return __instance;
    }
}
