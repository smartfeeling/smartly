/*
 * 
 */

package org.smartly.commons.logging;

/**
 * @author angelo.geminiani
 */
public enum Level {

    ALL(0),
    FINEST(1),
    FINER(2),
    FINE(3),
    CONFIG(4),
    INFO(5),
    WARNING(6),
    SEVERE(7),
    OFF(100);

    private final Integer _numValue;

    Level(final int intValue) {
        _numValue = intValue;
    }

    public int getNumValue() {
        return _numValue;
    }

    public static Level getLevel(String level) {
        if (level != null) {
            level = level.toUpperCase();
            if (level.equals("DEBUG")) {
                return Level.FINE;
            }
            if (level.equals("ERROR")) {
                return Level.SEVERE;
            }
            if (level.equals("FINE")) {
                return Level.FINE;
            }
            if (level.equals("FINER")) {
                return Level.FINER;
            }
            if (level.equals("FINEST")) {
                return Level.FINEST;
            }
            if (level.equals("INFO")) {
                return Level.INFO;
            }
            if (level.equals("CONFIG")) {
                return Level.CONFIG;
            }
            if (level.equals("WARNING")) {
                return Level.WARNING;
            }
            if (level.equals("OFF")) {
                return Level.OFF;
            }
        }
        return Level.ALL;
    }
}
