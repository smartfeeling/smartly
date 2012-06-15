/*
 * 
 */
package org.smartly.packages.velocity.impl.engine;

import org.apache.velocity.VelocityContext;
import org.smartly.packages.velocity.impl.tools.VLCToolbox;

import java.util.Map;

/**
 * @author
 */
public final class VLCContextFactory {

    private VLCContextFactory() {
    }

    public static VelocityContext getContext() {
        return new VelocityContext(VLCToolbox.getInstance().getToolsContext());
    }

    public static VelocityContext getContext(final Map<String, Object> contextData) {
        return null != contextData && !contextData.isEmpty()
                ? new VelocityContext(contextData, VLCToolbox.getInstance().getToolsContext())
                : new VelocityContext(VLCToolbox.getInstance().getToolsContext());
    }

    public static VelocityContext getContext(final VelocityContext contextData) {
        return null != contextData
                ? new VelocityContext(VLCToolbox.getInstance().getToolsContextAsMap(), contextData)
                : new VelocityContext(VLCToolbox.getInstance().getToolsContext());
    }
}
