/*
 * 
 */
package org.smartly.commons.logging;

import org.smartly.commons.util.FormatUtils;

/**
 * @author angelo.geminiani
 */
public class Logger {


    private String _name;
    private Level _level = Level.INFO;

    public Logger(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public Level getLevel() {
        return _level;
    }

    public void setLevel(Level level) {
        this._level = level;
    }

    public boolean isLoggable(final Level level) {
        return _level.getNumValue() <= level.getNumValue();
    }

    public void log(final Level level, final String msg) {
        this.notify(level, msg, null);
    }

    public void log(final Level level, final String msg,
                    final Object param1) {
        this.notify(level, FormatUtils.format(msg, param1), null);
    }

    public void log(final Level level, final String msg,
                    final Object[] params) {
        this.notify(level, FormatUtils.format(msg, params), null);
    }

    public void log(final Level level, final String msg,
                    final Throwable thrown) {
        this.notify(level, msg, thrown);
    }

    public void info(String msg) {
        this.notify(Level.INFO, msg, null);
    }

    public void info(final String msg, final Object... args) {
        this.notify(Level.INFO, FormatUtils.format(msg, args), null);
    }

    public void severe(String msg) {
        this.notify(Level.SEVERE, msg, null);
    }

    public void severe(final String msg, final Object... args) {
        this.notify(Level.SEVERE, FormatUtils.format(msg, args), null);
    }

    public void warning(String msg) {
        this.notify(Level.WARNING, msg, null);
    }

    public void warning(final String msg, final Object... args) {
        this.notify(Level.WARNING, FormatUtils.format(msg, args), null);
    }

    public void fine(String msg) {
        this.notify(Level.FINE, msg, null);
    }

    public void fine(final String msg, final Object... args) {
        this.notify(Level.FINE, FormatUtils.format(msg, args), null);
    }

    public void finer(String msg) {
        this.notify(Level.FINER, msg, null);
    }

    public void finer(final String msg, final Object... args) {
        this.notify(Level.FINER, FormatUtils.format(msg, args), null);
    }

    public void finest(String msg) {
        this.notify(Level.FINEST, msg, null);
    }

    public void finest(final String msg, final Object... args) {
        this.notify(Level.FINEST, FormatUtils.format(msg, args), null);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Object notify(final Level level,
                            final String subject,
                            final Throwable error) {
        LoggingRepository.getInstance().log(this, level, error, subject);
        return this;
    }

}
