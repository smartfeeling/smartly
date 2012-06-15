/*
 * 
 */
package org.smartly.packages.velocity.impl.tools;

import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author angelo.geminiani
 */
public final class VLCToolboxItem {

    // ------------------------------------------------------------------------
    //                      fields
    // ------------------------------------------------------------------------
    private final String _id;
    private final Class _toolClass;
    private final List<Object> _args;
    private IVLCTool _toolInstance;
    private boolean _singleton;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public VLCToolboxItem(final String id, final Class toolClass,
                          final Object[] args, final boolean isSingleton) {
        _id = id;
        _toolClass = toolClass;
        _args = new LinkedList<Object>();
        _singleton = isSingleton;
        if (!CollectionUtils.isEmpty(args)) {
            for (final Object arg : args) {
                _args.add(arg);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this._id != null ? this._id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VLCToolboxItem other = (VLCToolboxItem) obj;
        if ((this._id == null) ? (other._id != null) : !this._id.equals(other._id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(_id);
        result.append("{ ");
        result.append(_toolClass.getName());
        result.append("}");
        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      properties
    // ------------------------------------------------------------------------
    public String getId() {
        return _id;
    }

    public Class<IVLCTool> getToolClass() {
        return _toolClass;
    }

    public List<Object> getArgs() {
        return _args;
    }

    public boolean isSingleton() {
        return _singleton;
    }

    public void setSingleton(boolean singleton) {
        this._singleton = singleton;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public IVLCTool getInstance() throws Exception {
        if (null != _toolClass) {
            if (_singleton) {
                if (null == _toolInstance) {
                    _toolInstance = this.createInstance();
                }
            } else {
                _toolInstance = this.createInstance();
            }
        }
        return _toolInstance;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private IVLCTool createInstance() throws Exception {
        if (CollectionUtils.isEmpty(_args)) {
            return (IVLCTool) _toolClass.newInstance();
        } else {
            return (IVLCTool) ClassLoaderUtils.newInstance(_toolClass, _args.toArray(new Object[_args.size()]));
        }
    }
}
