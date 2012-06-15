/*
 * ICSVConstants.java
 *
 */

package org.smartly.commons.csv;

import java.text.DateFormat;
import java.util.Locale;

/**
 *
 */
public interface ICSVConstants {
    
    /** The character used for escaping quotes. */
    public static final char ESCAPE_CHARACTER = '"';

    /** The default separator to use if none is supplied to the constructor. */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    
    /** The quote constant to use when you wish to suppress all quoting. */
    public static final char NO_QUOTE_CHARACTER = '\u0000';
    
    /** Default line terminator uses platform encoding. */
    public static final String DEFAULT_LINE_END = "\n";
    
    /**
     * The default line to start reading.
     */
    public static final int DEFAULT_SKIP_LINES = 0;
    
    /** DATA FORMAT: The default date format */
    public static final int DEFAULT_DATE_FORMAT = DateFormat.SHORT;
    /** DATA FORMAT: Default locale */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    /** DATA FORMAT: The default number format */
    public static final String DEFAULT_NUMBER_PATTERN = "#,##0.0###;-#,##0.0###";
    
}
