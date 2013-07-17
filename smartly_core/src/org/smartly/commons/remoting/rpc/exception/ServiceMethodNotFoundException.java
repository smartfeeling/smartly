/*
 * 
 */

package org.smartly.commons.remoting.rpc.exception;

/**
 * Generic response exception.
 *
 * @author angelo.geminiani
 */
public class ServiceMethodNotFoundException
        extends Exception {

    public ServiceMethodNotFoundException() {
    }

    public ServiceMethodNotFoundException(String msg) {
        super(msg);
    }
    
}
