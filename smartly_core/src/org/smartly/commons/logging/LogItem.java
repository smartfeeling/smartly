/*
 * 
 */
package org.smartly.commons.logging;

import org.smartly.IConstants;
import org.smartly.commons.util.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author angelo.geminiani
 */
public final class LogItem
        implements Serializable {

    private static int COUNT = 0;
    private final String _loggerName;
    private int _id;
    private Date _date;
    private Level _level;
    private Throwable _exception;
    private String _message;

    public LogItem() {
        this(null, Level.INFO, null, null);
    }

    public LogItem(final Level level, final String message) {
        this(null, level, null, message);
    }

    public LogItem(final String message) {
        this(null, Level.INFO, null, message);
    }

    public LogItem(final String loggername, final Level level,
                   final Throwable exception, final String message) {
        _loggerName = loggername;
        _id = ++COUNT;
        _level = level;
        _exception = exception;
        _message = message;
        _date = DateUtils.now();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[").append(_id).append("] ");
        result.append(_level).append(": ");
        result.append(" (").append(FormatUtils.formatDate(_date, Locale.getDefault(), true)).append(") ");
        if (StringUtils.hasText(_message)) {
            result.append(_message).append(" ");
        }
        if (null != _exception) {
            result.append(IConstants.LINE_SEPARATOR);
            if (StringUtils.hasText(_loggerName)) {
                result.append("\t LOGGER: ");
                result.append(_loggerName);
                result.append(IConstants.LINE_SEPARATOR);
            }
            result.append("\t ERROR: {");
            result.append(_exception.toString());
            result.append("}");
            result.append(IConstants.LINE_SEPARATOR);
            result.append("\t CAUSE: {");
            result.append(ExceptionUtils.getRealMessage(_exception));
            result.append("}");
        }
        return result.toString();
    }

    public int getId() {
        return _id;
    }

    public Level getLevel() {
        return _level;
    }

    public void setLevel(Level level) {
        this._level = level;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        this._date = date;
    }

    public Throwable getException() {
        return _exception;
    }

    public void setException(Throwable exception) {
        this._exception = exception;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        this._message = message;
    }

    public boolean enabled() {
        return Level.OFF != _level;
    }

    public Throwable getCause() {
        return ExceptionUtils.getRealCause(_exception);
    }

    public String getCauseMessage() {
        return ExceptionUtils.getRealMessage(_exception);
    }


}
