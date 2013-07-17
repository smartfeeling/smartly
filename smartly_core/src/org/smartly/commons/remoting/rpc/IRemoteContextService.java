/*
 * 
 */
package org.smartly.commons.remoting.rpc;

/**
 * Implements this interface to create a context service.<br/>
 * Context services can interact with httprequest and httpresponse objects.
 *  
 * @author angelo.geminiani
 */
public interface IRemoteContextService {
    
    void setContext(IRemoteContext context);
    
    IRemoteContext getContext();
    
}
