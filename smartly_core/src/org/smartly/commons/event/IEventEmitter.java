/*
 * 
 */
package org.smartly.commons.event;

/**
 *
 * @author
 */
public interface IEventEmitter {

    public abstract void addEventListener(IEventListener listener);
    
    public abstract void removeEventListener(IEventListener listener);
    
    public abstract int emit(Event event);
}
