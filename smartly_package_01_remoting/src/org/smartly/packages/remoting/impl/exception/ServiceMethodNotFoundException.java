/*
 * 
 */

package org.smartly.packages.remoting.impl.exception;

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
