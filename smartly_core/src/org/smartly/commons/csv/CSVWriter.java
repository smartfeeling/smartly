/*
 * CSVWriter.java
 *
 */
package org.smartly.commons.csv;

import org.smartly.commons.csv.formatter.CSVDataFormatter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;

/**
 *
 */
public class CSVWriter
        implements ICSVConstants {

    private Writer _writer;
    private char _separator = DEFAULT_SEPARATOR;
    private char _quotechar = DEFAULT_QUOTE_CHARACTER;
    private String _lineEnd = DEFAULT_LINE_END;
    private Locale _locale = DEFAULT_LOCALE;
    //-- late initialized --//
    private PrintWriter __pwriter;
    private CSVDataFormatter __formatter;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------
    public CSVWriter() {
    }

    /**
     * Constructs CSVWriter using a comma for the _separator.
     *
     * @param writer the writer to an underlying CSV source.
     */
    public CSVWriter(final Writer writer) {
        this(writer, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs CSVWriter with supplied _separator.
     *
     * @param writer    the writer to an underlying CSV source.
     * @param separator the delimiter to use for separating entries.
     */
    public CSVWriter(final Writer writer, char separator) {
        this(writer, separator, DEFAULT_QUOTE_CHARACTER);
    }

    /**
     * Constructs CSVWriter with supplied _separator and quote char.
     *
     * @param writer    the writer to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     */
    public CSVWriter(final Writer writer, char separator, char quotechar) {
        this(writer, separator, quotechar, "\n", DEFAULT_LOCALE);
    }

    /**
     * Constructs CSVWriter with supplied _separator and quote char.
     *
     * @param writer    the writer to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     * @param lineEnd   the line feed terminator to use
     */
    public CSVWriter(final Writer writer, char separator,
                     char quotechar, String lineEnd, Locale locale) {
        _writer = writer;
        _separator = separator;
        _quotechar = quotechar;
        _lineEnd = lineEnd;
        _locale = locale;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      properties
    // ------------------------------------------------------------------------
    public void setWriter(final Writer writer) {
        _writer = writer;
    }

    public Writer getWriter() {
        return _writer;
    }

    public char getSeparator() {
        return _separator;
    }

    public void setSeparator(char separator) {
        this._separator = separator;
    }

    public char getQuotechar() {
        return _quotechar;
    }

    public void setQuotechar(char quotechar) {
        this._quotechar = quotechar;
    }

    public String getLineEnd() {
        return _lineEnd;
    }

    public void setLineEnd(String lineEnd) {
        this._lineEnd = lineEnd;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        this._locale = locale;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    /**
     * Writes the entire list to a CSV file. The list is assumed to be a
     * String[]
     *
     * @param allLines a List of String[], with each String[] representing a line of
     *                 the file.
     */
    public int writeAll(List<String[]> allLines) {
        int result = 0;
        for (Iterator iter = allLines.iterator(); iter.hasNext(); ) {
            String[] nextLine = (String[]) iter.next();
            this.writeNext(nextLine);
            result++;
        }
        return result;
    }

    /**
     * Writes the entire list to a CSV file. The list is assumed to be a Map
     *
     * @param allLines a List of Map, with each row representing a line of the file.
     */
    public int writeAll(final List<Map> allLines,
                        boolean includeColumnNames) {
        int result = 0;
        int i = 0;
        String[] header = null;
        for (final Map item : allLines) {
            if (i == 0 && includeColumnNames) {
                header = this.getNames(item);
                this.writeNext(header);
            }

            String[] nextLine = this.getValues(header, item);
            this.writeNext(nextLine);

            result++;
            i++;
        }
        return result;
    }

    /**
     * Writes the entire ResultSet to a CSV file.
     * <p/>
     * The caller is responsible for closing the ResultSet.
     *
     * @param rs                 the recordset to write
     * @param includeColumnNames true if you want column names in the output, false otherwise
     */
    public int writeAll(ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        try {
            rs.beforeFirst();
        } catch (Throwable t) {
        }
        int result = 0;
        ResultSetMetaData metadata = rs.getMetaData();


        if (includeColumnNames) {
            this.writeColumnNames(metadata);
        }

        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            String[] nextLine = new String[columnCount];

            for (int i = 0; i < columnCount; i++) {
                nextLine[i] = this.getColumnValue(rs, metadata.getColumnType(i + 1), i + 1);
            }

            this.writeNext(nextLine);
            result++;
        }
        return result;
    }

    /**
     * Writes the next line to the file.
     *
     * @param nextLine a string array with each comma-separated element as a separate
     *                 entry.
     */
    public void writeNext(final String[] nextLine) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(_separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null) {
                continue;
            }
            if (_quotechar != NO_QUOTE_CHARACTER) {
                sb.append(_quotechar);
            }
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (nextChar == _quotechar) {
                    sb.append(ESCAPE_CHARACTER).append(nextChar);
                } else if (nextChar == ESCAPE_CHARACTER) {
                    sb.append(ESCAPE_CHARACTER).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }
            if (_quotechar != NO_QUOTE_CHARACTER) {
                sb.append(_quotechar);
            }
        }

        sb.append(_lineEnd);
        this.getPWriter().write(sb.toString());

    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     *
     * @throws java.io.IOException if bad things happen
     */
    public void close() throws IOException {
        if (null != __pwriter) {
            __pwriter.flush();
            __pwriter.close();
        }
        if (null != _writer) {
            _writer.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected void writeColumnNames(ResultSetMetaData metadata)
            throws SQLException {

        int columnCount = metadata.getColumnCount();

        String[] nextLine = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String name = metadata.getColumnName(i + 1);
            if (null == name || name.length() == 0) {
                name = metadata.getColumnLabel(i + 1);
            }
            nextLine[i] = name;
        }
        writeNext(nextLine);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private CSVDataFormatter getFormatter() {
        if (null == __formatter) {
            __formatter = new CSVDataFormatter(DateFormat.SHORT, "", _locale);
        }
        return __formatter;
    }

    private PrintWriter getPWriter() {
        if (null == __pwriter) {
            if (null != _writer) {
                __pwriter = new PrintWriter(_writer);
            }
        }
        return __pwriter;
    }

    private String[] getNames(final Map map) {
        final String[] result = new String[map.size()];
        int i = 0;
        final Set keys = map.keySet();
        for (final Object key : keys) {
            result[i] = key.toString();
            i++;
        }
        return result;
    }

    private String[] getValues(final String[] keys,
                               final Map map) {
        final String[] result = new String[map.size()];
        int i = 0;
        if (null != keys && keys.length > 0) {
            // respect keys order
            for (final String key : keys) {
                result[i] = this.getFormatter().serialize(map.get(key));
                i++;
            }
        } else {
            final Collection values = map.values();
            for (final Object value : values) {
                result[i] = this.getFormatter().serialize(value);
                i++;
            }
        }

        return result;
    }

    private String getColumnValue(ResultSet rs, int colType, int colIndex)
            throws SQLException, IOException {

        return this.getFormatter().serialize(rs, colType, colIndex);
    }
}
