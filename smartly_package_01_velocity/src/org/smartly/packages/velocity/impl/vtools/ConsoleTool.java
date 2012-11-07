package org.smartly.packages.velocity.impl.vtools;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * console logger helper for VTL expressions.
 *
 * usage:
 *  $console.log("hello")
 *
 *  $console.log("custom_file", "hello")
 */
public class ConsoleTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "console";

    private final static String PREFIX = "VTL_console_";
    private final static String DEFAULT_NAME = "default";

    private final String _id;


    public ConsoleTool() {
        this(DEFAULT_NAME);
    }

    public ConsoleTool(final String id) {
        _id = id;
    }

    public ConsoleTool create(final String name){
       return new ConsoleTool(name);
    }

    public void log(final Object item) {
        static_log(_id, item);
    }

    public void info(final Object item) {
        static_info(_id, item);
    }

    public void warn(final Object item) {
        static_warn(_id, item);
    }

    public void error(final Object item) {
        static_error(_id, item);
    }

    public void debug(final Object item) {
        static_debug(_id, item);
    }

    //-- CUSTOM LOGGER --//

    public void log(final String logger, final Object item) {
        static_log(logger, item);
    }

    public void info(final String logger, final Object item) {
        static_info(logger, item);
    }

    public void warn(final String logger, final Object item) {
        static_warn(logger, item);
    }

    public void error(final String logger, final Object item) {
        static_error(logger, item);
    }

    public void debug(final String logger, final Object item) {
        static_debug(logger, item);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static final Map<String, ConsoleTool> _loggers;

    static {
        _loggers = new HashMap<String, ConsoleTool>();
    }

    private static String getKey(final String id) {
        final String key = StringUtils.hasText(id) ? PREFIX.concat(id) : PREFIX.concat(DEFAULT_NAME);
        if (!_loggers.containsKey(key)) {
            _loggers.put(id, new ConsoleTool(id));
            LoggingRepository.getInstance().setLogFileName(key, "./smartly_".concat(key).concat(".log"));
        }
        return key;
    }

    private static void static_log(final String id, final Object item) {
        final String key = getKey(id);
        final String msg = FormatUtils.format("{0}", item);
        LoggingUtils.getLogger(key).log(Level.INFO, msg);
    }

    private static void static_info(final String id, final Object item) {
        final String key = getKey(id);
        final String msg = FormatUtils.format("{0}", item);
        LoggingUtils.getLogger(key).log(Level.INFO, msg);
    }

    private static void static_warn(final String id, final Object item) {
        final String key = getKey(id);
        final String msg = FormatUtils.format("{0}", item);
        LoggingUtils.getLogger(key).log(Level.WARNING, msg);
    }

    private static void static_error(final String id, final Object item) {
        final String key = getKey(id);
        final String msg = FormatUtils.format("{0}", item);
        LoggingUtils.getLogger(key).log(Level.SEVERE, msg);
    }

    private static void static_debug(final String id, final Object item) {
        final String key = getKey(id);
        final String msg = FormatUtils.format("{0}", item);
        LoggingUtils.getLogger(key).log(Level.FINE, msg);
    }

}
