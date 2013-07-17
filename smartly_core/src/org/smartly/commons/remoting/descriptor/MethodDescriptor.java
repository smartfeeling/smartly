/*
 * 
 */
package org.smartly.commons.remoting.descriptor;

import java.util.*;

/**
 *
 * @author angelo.geminiani
 */
public class MethodDescriptor {

    private String _name;
    private final Map<String, Class> _paremeters;

    public MethodDescriptor() {
        _paremeters = Collections.synchronizedMap(new LinkedHashMap<String, Class>());
    }

    @Override
    public String toString(){
        final StringBuilder result = new StringBuilder();
        result.append(_name);
        result.append("(");
        if(!_paremeters.isEmpty()){
            final Set<String> names = _paremeters.keySet();
            int i = 0;
            for(final String name:names){
                final Class aclass = _paremeters.get(name);
                if(i>0){
                    result.append(", ");
                }
                result.append(aclass.getName());
                result.append(" ").append(name);
                i++;
            }
        }
        result.append(")");

        return result.toString();
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void addParameter(final String name, final Class paramType) {
        synchronized (_paremeters) {
            _paremeters.put(name, paramType);
        }
    }

    public void addParameter(final Class paramType) {
        synchronized (_paremeters) {
            final String name = "param" + (_paremeters.size()+1);
            _paremeters.put(name, paramType);
        }
    }

    public String[] getParameterNames() {
        synchronized (_paremeters) {
            final Set<String> keys = _paremeters.keySet();
            return keys.toArray(new String[keys.size()]);
        }
    }

    public Class[] getParameterTypes() {
        synchronized (_paremeters) {
            final Collection<Class> values = _paremeters.values();
            return values.toArray(new Class[values.size()]);
        }
    }

    public Map<String, Class>  getParameters() {
        synchronized (_paremeters) {
            return new LinkedHashMap<String, Class>(_paremeters);
        }
    }

    public boolean hasParameters(){
        synchronized (_paremeters) {
            return !_paremeters.isEmpty();
        }
    }
}
