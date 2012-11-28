package org.smartly;


import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

/**
 * Logger wrapper that check for "silent" option.
 * <p/>
 * SILENT mode does not log info or warnings, but only errors.
 * Errors are always logged.
 */
public class SmartlyLogger {

    private final boolean _silent;
    private final Object _sender;

    public SmartlyLogger(final boolean silent) {
        _silent = silent;
        _sender = null;
    }

    public SmartlyLogger(final Object sender, final boolean silent) {
        _silent = silent;
        _sender = sender;
    }

    public void debug(final String message) {
        this.debug(null != _sender ? _sender : this, message);
    }

    public void info(final String message) {
        this.info(null != _sender ? _sender : this, message);
    }

    public void warning(final String message) {
        this.warning(null != _sender ? _sender : this, message);
    }

    public void warning(final Throwable error) {
        this.warning(null != _sender ? _sender : this, error);
    }

    public void severe(final String message) {
        this.severe(null != _sender ? _sender : this, message);
    }

    public void severe(final Throwable error) {
        this.severe(null != _sender ? _sender : this, error);
    }

    public void severe(final String message, final Throwable error) {
        this.severe(null != _sender ? _sender : this, message, error);
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
