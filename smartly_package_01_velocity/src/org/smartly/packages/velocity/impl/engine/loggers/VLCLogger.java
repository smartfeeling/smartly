/*
 * 
 */
package org.smartly.packages.velocity.impl.engine.loggers;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

/**
 * @author angelo.geminiani
 */
public class VLCLogger implements LogChute {

    private Logger _logger;

    //    public static final int TRACE_ID = -1;
    //    public static final int DEBUG_ID = 0;
    //    public static final int INFO_ID = 1;
    //    public static final int WARN_ID = 2;
    //    public static final int ERROR_ID = 3;
    @Override
    public void init(RuntimeServices rs) throws Exception {
        _logger = LoggingUtils.getLogger(rs);
    }

    @Override
    public void log(int i, String message) {
        if (null != _logger) {
            final Level level = this.getLevel(i);
            _logger.log(level, message);
        }
    }

    @Override
    public void log(int i, String message, Throwable thrwbl) {
        if (null != _logger) {
            final Level level = this.getLevel(i);
            _logger.log(level, message, thrwbl);
        }
    }

    @Override
    public boolean isLevelEnabled(int i) {
        if (null != _logger) {
            final Level level = this.getLevel(i);
            return _logger.isLoggable(level);
        }
        return true;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Level getLevel(final int i) {
        if (i == TRACE_ID) {
            return Level.FINER;
        } else if (i == DEBUG_ID) {
            return Level.FINE;
        } else if (i == INFO_ID) {
            return Level.INFO;
        } else if (i == WARN_ID) {
            return Level.WARNING;
        } else if (i == ERROR_ID) {
            return Level.SEVERE;
        }
        return Level.CONFIG;
    }
}
