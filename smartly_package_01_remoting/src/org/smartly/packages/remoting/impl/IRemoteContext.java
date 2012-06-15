/*
 * 
 */
package org.smartly.packages.remoting.impl;

/**
 *
 * @author angelo.geminiani
 */
public interface IRemoteContext {

    Object getRequest();

    Object getResponse();

    Object getAttribute(String id);
    
    Object removeAttribute(String id);

    void setAttribute(String id, Object obj);
    
}
