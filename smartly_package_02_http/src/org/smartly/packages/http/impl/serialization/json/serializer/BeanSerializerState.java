/*
 *
 */

package org.smartly.packages.http.impl.serialization.json.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angelo.geminiani
 */
public class BeanSerializerState {

    private Map<Object, BeanProcessed> _processedObjects;

    public BeanSerializerState() {
        _processedObjects = new HashMap<Object, BeanProcessed>();
    }

    public boolean isProcessed(final Object bean){
        return _processedObjects.containsKey(bean);
    }

    public BeanProcessed getProcessedBean(final Object bean){
        return _processedObjects.get(bean);
    }

    public BeanProcessed addProcessedBean(final Object parent, final Object bean){
        final BeanProcessed item = new  BeanProcessed();
        item.setBean(bean);
        item.setParent(parent);
        _processedObjects.put(bean, item);
        return item;
    }
}
