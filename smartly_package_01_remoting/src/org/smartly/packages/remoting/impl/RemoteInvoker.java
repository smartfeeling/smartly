/*
 * 
 */
package org.smartly.packages.remoting.impl;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.packages.remoting.impl.descriptor.MethodDescriptor;
import org.smartly.packages.remoting.impl.descriptor.ServiceDescriptor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author angelo.geminiani
 */
public class RemoteInvoker {

    public static int POOL_SIZE = 3; // 3 services per instance
    private final Map<Class, List<Object>> _servicePools;

    public RemoteInvoker() {
        _servicePools = Collections.synchronizedMap(new HashMap<Class, List<Object>>());
    }

    public Object call(final String endpoint,
                       final String serviceName, final String methodName,
                       final Map<String, String> parameters) throws Exception  {
        return  this.call(null, endpoint, serviceName, methodName, parameters);
    }

    public Object call(final IRemoteContext context, final String endpoint,
                       final String serviceName, final String methodName,
                       final Map<String, String> parameters) throws Exception {
        final ServiceDescriptor service = RemoteServiceRepository.getInstance().getService(
                endpoint, serviceName);
        if (null != service) {
            return this.call(context, service, methodName, parameters);
        } else {
            throw new Exception(FormatUtils.format("Service not found: {0}", serviceName));
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private Object popServiceFromPool(final Class aclass) {
        synchronized (_servicePools) {
            if (_servicePools.containsKey(aclass)) {
                // already have pool for service
                final List<Object> pool = _servicePools.get(
                        aclass);
                if (pool.size() > 0) {
                    // pop service from pool
                    return pool.remove(0);
                }
            } else {
                // creates new service pool
                final List<Object> pool = new ArrayList<Object>();
                _servicePools.put(aclass, pool);
            }
            // creates new service
            return this.createService(aclass);
        }
    }

    private void putServiceInPool(final Object serviceInstance) {
        synchronized (_servicePools) {
            final List<Object> pool = _servicePools.get(
                    serviceInstance.getClass());
            if (null != pool
                    && pool.size() < POOL_SIZE) {
                pool.add(serviceInstance);
            } else {
                this.getLogger().log(Level.INFO,
                        FormatUtils.format("SERVICE POOL SIZE ({0}) NOT ENOUGHT."
                                + " New service instance was created out of pool to "
                                + " serve requests.",
                                POOL_SIZE));
            }
        }
    }

    private Object createService(final Class aclass) {
        try {
            final Object result = aclass.newInstance();
            return result;
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    private Object call(final IRemoteContext context,
                        final ServiceDescriptor service, final String methodName,
                        final Map<String, String> stringParamValues) throws Exception {
        // retrieve service from pool ( Thread SAFE )
        final Object serviceInstance = this.popServiceFromPool(
                service.getServiceClass());
        try {
            if (null != serviceInstance) {
                final MethodDescriptor method = service.getMethod(methodName);
                if (null != method) {
                    final Map<String, Object> paramValues;
                    final Map<String, Class> paramTypes = method.getParameters();
                    if (paramTypes.size() > 0) {
                        paramValues = ConversionUtils.toTypes(stringParamValues,
                                paramTypes);
                    } else {
                        paramValues = null;
                    }
                    return this.call(context,
                            serviceInstance, methodName,
                            paramValues, paramTypes);
                } else {
                    // method not found
                    this.getLogger().warning(FormatUtils.format(
                            "Method '{0}' not found!", methodName));
                }
            }
        } finally {
            // reintroduce service in pool
            this.putServiceInPool(serviceInstance);
        }
        return null;
    }

    private Object call(final IRemoteContext context,
                        final Object serviceInstance, final String methodName,
                        final Map<String, Object> paramValues,
                        final Map<String, Class> paramTypes) throws Exception {
        final Object[] values = null != paramValues
                ? paramValues.values().toArray(new Object[paramValues.size()])
                : new Object[0];
        final Class[] types = null != paramTypes
                ? paramTypes.values().toArray(new Class[paramTypes.size()])
                : new Class[0];
        return this.call(context, serviceInstance, methodName, values, types);
    }

    private Object call(final IRemoteContext context,
                        final Object serviceInstance,
                        final String methodName, final Object[] parameters,
                        final Class[] paramTypes) throws Exception {
        //-- add context if enabled --//
        if (null!=context && serviceInstance instanceof RemoteServiceContext) {
            ((RemoteServiceContext) serviceInstance).setContext(context);
        }
        //-- execute method --//
        final Method method = BeanUtils.getMethodIfAny(serviceInstance.getClass(),
                methodName, paramTypes);
        if (null != method) {
            return method.invoke(serviceInstance, parameters);
        } else {
            //-- method not found in service --//
            final String msg = FormatUtils.format(
                    "Method '{0}' not found in service '{1}'.",
                    methodName,
                    serviceInstance.getClass().getName());
            throw new Exception(FormatUtils.format("Service not found: {0}", msg));
        }
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private final static RemoteInvoker __INSTANCE = new RemoteInvoker();

    public static RemoteInvoker getInstance() {
        return __INSTANCE;
    }
}
