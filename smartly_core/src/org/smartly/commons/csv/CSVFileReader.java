/*
 * CSVFileReader.java
 *
 */
package org.smartly.commons.csv;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class CSVFileReader extends CSVReader {

    private File _file;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public CSVFileReader() {
        super();
    }

    public CSVFileReader(final String fileName) {
        this(new File(fileName), DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_SKIP_LINES, DEFAULT_ENCODING);
    }

    public CSVFileReader(final String fileName, char separator) {
        this(new File(fileName), separator, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_SKIP_LINES, DEFAULT_ENCODING);
    }

    public CSVFileReader(final String fileName, char separator,
                         final String encoding) {
        this(new File(fileName), separator, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_SKIP_LINES, encoding);
    }

    public CSVFileReader(final File file) {
        this(file, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_SKIP_LINES, DEFAULT_ENCODING);
    }

    public CSVFileReader(final File file, char separator) {
        this(file, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_SKIP_LINES,
                DEFAULT_ENCODING);
    }

    public CSVFileReader(final File file, char separator, final String encoding) {
        this(file, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_SKIP_LINES,
                encoding);
    }

    public CSVFileReader(final File file, char separator, char quoteChar,
                         int skipLines, final String encoding) {
        super.setQuotechar(quoteChar);
        super.setSeparator(separator);
        super.setSkipLines(skipLines);
        this.setFile(file, encoding);
    }

    public File getFile() {
        return _file;
    }

    public void setFile(final File file) {
        this._file = file;
        try {
            super.setReader(new FileReader(file));
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    public void setFile(final File file, final String encoding) {
        this._file = file;
        try {
            super.setReader(new InputStreamReader(new FileInputStream(file), encoding));
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static List<String[]> readFile(final String filename)
            throws Exception {
        // read data from file
        return readFile(filename, DEFAULT_SEPARATOR);
    }

    public static List<String[]> readFile(final String filename, char separator)
            throws Exception {
        // Read data from file
        return readFile(filename, separator, DEFAULT_ENCODING);
    }

    public static List<String[]> readFile(final String filename,
                                          char separator, final String encoding) throws Exception {
        // read data from file
        final CSVFileReader reader = new CSVFileReader(filename, separator,
                encoding);
        final List<String[]> rows = reader.readAll();
        reader.close();
        return rows;
    }

    public static List<String[]> readFile(final File file) throws Exception {
        // read data from file
        return readFile(file, DEFAULT_SEPARATOR);
    }

    public static List<String[]> readFile(final File file, char separator)
            throws Exception {
        // read data from file
        return readFile(file, separator, DEFAULT_ENCODING);
    }

    public static List<String[]> readFile(final File file, char separator,
                                          final String encoding) throws Exception {
        // read data from file
        final CSVFileReader reader = new CSVFileReader(file, separator,
                encoding);
        final List<String[]> rows = reader.readAll();
        reader.close();
        return rows;
    }

    public static List<String[]> readText(final String text, char separator)
            throws Exception {
        // read data from file
        final CSVReader reader = new CSVReader();
        reader.setSeparator(separator);
        reader.setReader(new StringReader(text));

        final List<String[]> result = reader.readAll();
        reader.close();
        return result;
    }

    public static List<Map<String, String>> readFileAsMap(
            final String filename, boolean headerOnFirstRow) throws Exception {
        return readFileAsMap(filename, DEFAULT_SEPARATOR, headerOnFirstRow, DEFAULT_ENCODING);
    }

    public static List<Map<String, String>> readFileAsMap(
            final String filename, boolean headerOnFirstRow, final String encoding) throws Exception {
        return readFileAsMap(filename, DEFAULT_SEPARATOR, headerOnFirstRow, encoding);
    }

    public static List<Map<String, String>> readFileAsMap(
            final String filename, char separator, boolean headerOnFirstRow, final String encoding)
            throws Exception {
        final CSVFileReader reader = new CSVFileReader(filename, separator, encoding);
        final List<Map<String, String>> result = reader
                .readAllAsMap(headerOnFirstRow);
        reader.close();
        return result;
    }

    public static List<Map<String, String>> readFileAsMap(final File file,
                                                          boolean headerOnFirstRow) throws Exception {
        return readFileAsMap(file, DEFAULT_SEPARATOR, headerOnFirstRow, DEFAULT_ENCODING);
    }

    public static List<Map<String, String>> readFileAsMap(final File file,
                                                          boolean headerOnFirstRow, final String encoding) throws Exception {
        return readFileAsMap(file, DEFAULT_SEPARATOR, headerOnFirstRow, encoding);
    }

    public static List<Map<String, String>> readFileAsMap(final File file,
                                                          char separator, boolean headerOnFirstRow, final String encoding) throws Exception {
        // read data from file
        final CSVFileReader reader = new CSVFileReader(file, separator, encoding);
        final List<Map<String, String>> result = reader
                .readAllAsMap(headerOnFirstRow);
        reader.close();
        return result;
    }

    public static List<Map<String, String>> readTextAsMap(final String text,
                                                          boolean headerOnFirstRow) throws Exception {
        return readTextAsMap(text, DEFAULT_SEPARATOR, headerOnFirstRow);
    }

    public static List<Map<String, String>> readTextAsMap(final String text,
                                                          char separator, boolean headerOnFirstRow) throws Exception {
        final CSVReader reader = new CSVReader();
        reader.setSeparator(separator);
        reader.setReader(new StringReader(text));
        final List<Map<String, String>> result = reader
                .readAllAsMap(headerOnFirstRow);
        reader.close();
        return result;
    }


}
