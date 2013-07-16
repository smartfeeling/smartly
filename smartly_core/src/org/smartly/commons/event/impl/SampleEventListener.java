/*
 * 
 */

package org.smartly.commons.event.impl;

import org.smartly.commons.event.Event;
import org.smartly.commons.event.IEventListener;

/**
 * Sample IEventListener implementation.
 *
 * @author
 */
public class SampleEventListener
        implements IEventListener {

    public void on(Event event) {
        System.out.println(String.format("Fired event: %s", event));
    }

}
