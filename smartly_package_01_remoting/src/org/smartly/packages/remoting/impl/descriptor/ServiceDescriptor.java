/*
 * 
 */
package org.smartly.packages.remoting.impl.descriptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angelo.geminiani
 */
public class ServiceDescriptor {

    private String _name;
    private Class _serviceClass;
    private final Map<String, MethodDescriptor> _methods;

    public ServiceDescriptor() {
        _methods = Collections.synchronizedMap(new HashMap<String, MethodDescriptor>());
    }

    public ServiceDescriptor(final String name) {
        this();
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public ServiceDescriptor setName(String name) {
        _name = name;
        return this;
    }

    public Class getServiceClass() {
        return _serviceClass;
    }

    public ServiceDescriptor setServiceClass(Class serviceClass) {
        _serviceClass = serviceClass;
        return this;
    }

    public boolean hasMethod(final String name) {
        synchronized (_methods) {
            return _methods.containsKey(name);
        }
    }

    public MethodDescriptor getMethod(final String name) {
        synchronized (_methods) {
            if (_methods.containsKey(name)) {
                return _methods.get(name);
            } else {
                return null;
            }
        }
    }

    public MethodDescriptor getOrCreateMethod(final String name) {
        synchronized (_methods) {
            if (_methods.containsKey(name)) {
                return _methods.get(name);
            } else {
                final MethodDescriptor method = new MethodDescriptor();
                method.setName(name);
                _methods.put(name, method);
                return method;
            }
        }
    }
}
