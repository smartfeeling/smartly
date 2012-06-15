/*
 * 
 */
package org.smartly.commons.logging;


/**
 * This logger user LogItemRepository logger.<br/>
 * Logger instance is created calling "LoggingUtils.getLogger()".
 *
 * @author angelo.geminiani
 */
public class LogItemRepositoryLogger
        extends Logger {

    public LogItemRepositoryLogger(String name, String resourceBundleName) {
        super(name);
        super.setLevel(Level.WARNING);
    }
}
