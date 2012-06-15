/*
 * 
 */

package org.smartly.commons.event;

/**
 * Implement this interface if you want add a listener to runtime notifier.
 * 
 * @author
 */
public interface IEventListener {

    public abstract void on(Event event);
    
}
