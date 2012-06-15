/*
 * 
 */

package org.smartly.packages.velocity.impl.events;

import org.apache.velocity.VelocityContext;
import org.smartly.commons.event.Event;

/**
 *
 * @author angelo.geminiani
 */
public class OnBeforeEvaluate extends Event {

    public static final String NAME = "onBeforeEvaluate";

    public OnBeforeEvaluate(final Object sender) {
        super(sender, NAME);
    }

    public OnBeforeEvaluate(final Object sender, final VelocityContext context) {
        super(sender, NAME, context);
    }

    public void setContext(final VelocityContext context){
        super.setData(context);
    }

    public VelocityContext getContext(){
        return (VelocityContext) super.getData();
    }
}
