/*
 * 
 */
package org.smartly.commons.remoting.rpc;

import org.smartly.commons.util.BeanUtils;

/**
 * @author angelo.geminiani
 */
public abstract class RemoteServiceContext
        extends RemoteService {

    private IRemoteContext _context;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public RemoteServiceContext(final String name) {
        super(name);
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------
    public IRemoteContext getContext() {
        return _context;
    }

    public void setContext(final IRemoteContext context) {
        this._context = context;
    }

    public boolean hasContext() {
        return null != _context;
    }

    public boolean hasContext(final Class contextClass) {
        return BeanUtils.isAssignable(_context, contextClass);
    }
}
