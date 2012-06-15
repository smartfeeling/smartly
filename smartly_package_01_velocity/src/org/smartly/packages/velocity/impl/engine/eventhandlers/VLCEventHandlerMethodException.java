/*
 * 
 */
package org.smartly.packages.velocity.impl.engine.eventhandlers;

import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;


/**
 * @author angelo.geminiani
 */
public class VLCEventHandlerMethodException implements MethodExceptionEventHandler {

    private final Level _level;

    public VLCEventHandlerMethodException() {
        _level = Level.WARNING;
    }

    public VLCEventHandlerMethodException(final Level level) {
        _level = level;
    }

    @Override
    public Object methodException(final Class type, final String string,
                                  final Exception excptn) throws Exception {
        this.log(type, string, excptn);
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    protected void log(final Class type, final String string,
                       final Exception excptn) {
        final String message = FormatUtils.format("Method Exception calling "
                + "'{0}' of class '{1}': {2}", string, type,
                excptn.toString());
        this.getLogger().log(_level, message);
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
}
