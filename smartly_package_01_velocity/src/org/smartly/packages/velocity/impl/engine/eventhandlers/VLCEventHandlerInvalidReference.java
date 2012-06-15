/*
 * 
 */

package org.smartly.packages.velocity.impl.engine.eventhandlers;

import org.apache.velocity.app.event.implement.ReportInvalidReferences;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author angelo.geminiani
 */
public class VLCEventHandlerInvalidReference
        extends ReportInvalidReferences {

    private static final String LOGFILE = "vtl.log";
    private final Level _level;
    private final Set<String> _exclude;

    public VLCEventHandlerInvalidReference() {
        _level = Level.WARNING;
        _exclude = new TreeSet<String>();
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOGFILE);
    }

    public VLCEventHandlerInvalidReference(final Level level) {
        _level = level;
        _exclude = new TreeSet<String>();
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOGFILE);
    }

    @Override
    public Object invalidGetMethod(Context context, String reference, Object object, String property, Info info) {
        return super.invalidGetMethod(context, reference, object, property, info);
    }

    @Override
    public Object invalidMethod(final Context context, final String reference,
                                final Object object, final String method, final Info info) {
        this.log(reference, info);
        return super.invalidMethod(context, reference, object, method, info);
    }

    @Override
    public boolean invalidSetMethod(Context context, String leftreference, String rightreference, Info info) {
        return super.invalidSetMethod(context, leftreference, rightreference, info);
    }

    public void addExclusion(final String reference) {
        _exclude.add(reference);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected boolean isExcluded(final String reference) {
        return _exclude.contains(reference);
    }

    protected void log(final String reference,
                       final Info info) {
        if (!this.isExcluded(reference)) {
            this.log(_level, reference, info);
        }
    }

    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void log(final Level level, final String reference,
                     final Info info) {
        final String message = FormatUtils.format(
                "Invalid reference for '{0}': {1}",
                reference, info.toString());
        this.getLogger().log(level, message);
    }

}
