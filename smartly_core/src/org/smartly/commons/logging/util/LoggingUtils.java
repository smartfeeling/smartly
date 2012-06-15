/*
 * LoggingUtils.java
 *
 */
package org.smartly.commons.logging.util;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.LogItemRepositoryLogger;
import org.smartly.commons.logging.Logger;

/**
 * @author
 */
public abstract class LoggingUtils {

    private static final String LOGGER_NAME = "BASE LOGGER";

    public static Level getLevel() {
        return LoggingRepository.getInstance().getLevel();
    }

    public static void setLevel(final Level level) {
        LoggingRepository.getInstance().setLevel(level);
    }

    public static Logger getLogger(Class cls) {
        return getLogger(cls.getName());
    }

    public static Logger getLogger(Object instance) {
        return getLogger(instance.getClass().getName());
    }

    public static Logger getLogger() {
        final LogItemRepositoryLogger result = new LogItemRepositoryLogger(LOGGER_NAME, "");
        return result;
    }

    public static Logger getLogger(String name) {
        final LogItemRepositoryLogger result = new LogItemRepositoryLogger(name, "");
        return result;
    }
}
