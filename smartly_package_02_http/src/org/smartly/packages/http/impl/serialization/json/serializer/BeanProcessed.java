/*
 * 
 */
package org.smartly.packages.http.impl.serialization.json.serializer;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 *
 * @author angelo.geminiani
 */
public class BeanProcessed {

    private Object _bean;
    private Object _parent;
    private JSONObject _jsonObject;
    private BeanData _data;
    private Object _beanId;

    public Object getBeanId() {
        return _beanId;
    }

    public BeanData getBeanData() {
        return _data;
    }

    public Object getBean() {
        return _bean;
    }

    public void setBean(Object bean) {
        _bean = bean;
        this.initBeanData(bean);
    }

    public Object getParent() {
        return _parent;
    }

    public void setParent(Object parent) {
        _parent = parent;
    }

    public JSONObject getJsonObject() {
        return _jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        _jsonObject = jsonObject;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void initBeanData(final Object bean) {
        if (null != _bean) {
            _data = new BeanData(bean);
            _beanId = this.getId(_bean, _data, _bean.hashCode());
        }
    }

    private Object getId(final Object bean, final BeanData data,
            final Object defaultValue) {
        if (null != data
                && data.getReadableProps().containsKey("id")) {
            final Method method = data.getReadableProps().get("id");
            try {
                return method.invoke(bean, new Object[0]);
            } catch (Throwable t) {
            }
        }
        return defaultValue;
    }
}
