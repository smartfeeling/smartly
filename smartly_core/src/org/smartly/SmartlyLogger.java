package org.smartly;


import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

/**
 * Logger wrapper that check for "silent" option.
 *
 * SILENT mode does not log info or warnings, but only errors.
 * Errors are always logged.
 */
public class SmartlyLogger {

    private final boolean _silent;

    public SmartlyLogger(final boolean silent) {
        _silent = silent;
    }

    public void debug(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).debug(message);
        }
    }

    public void info(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).info(message);
        }
    }

    public void warning(final Object sender, final String message) {
        if (!_silent) {
            this.getLogger(sender).warning(message);
        }
    }

    public void warning(final Object sender, final Throwable error) {
        if (!_silent) {
            this.getLogger(sender).log(Level.WARNING, null, error);
        }
    }

    public void severe(final Object sender, final String message) {
        this.getLogger(sender).log(Level.SEVERE, message);
    }

    public void severe(final Object sender, final Throwable error) {
        this.getLogger(sender).log(Level.SEVERE, null, error);
    }

    public void severe(final Object sender, final String message, final Throwable error) {
        this.getLogger(sender).log(Level.SEVERE, message, error);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger(final Object instance) {
        return LoggingUtils.getLogger(instance);
    }


}
