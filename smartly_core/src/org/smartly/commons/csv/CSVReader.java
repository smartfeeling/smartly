/*
 * CSVReader.java
 *
 */
package org.smartly.commons.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CSVReader implements ICSVConstants {

    private Reader _reader;
    private char _separator = DEFAULT_SEPARATOR;
    private char _quotechar = DEFAULT_QUOTE_CHARACTER;
    private int _skipLines = DEFAULT_SKIP_LINES;
    //-- late initialized --//
    private BufferedReader __reader;
    // internal fields
    private boolean _hasNext = true;
    private boolean _linesSkiped = false;

    //<editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    public CSVReader() {
        
    }
    
    /**
     * Constructs CSVReader using a null reader and a comma for the separator.<br>
     * @param reader
     *            the reader to an underlying CSV source.
     */
    public CSVReader(final Reader reader) {
        this(reader, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs CSVReader with supplied separator.
     *
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries.
     */
    public CSVReader(final Reader reader, char separator) {
        this(reader, separator, DEFAULT_QUOTE_CHARACTER);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     */
    public CSVReader(final Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     * @param line
     *            the line number to skip for start reading
     */
    public CSVReader(final Reader reader, char separator, char quotechar, int line) {
        _reader = reader;
        _separator = separator;
        _quotechar = quotechar;
        _skipLines = line;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
    
    //</editor-fold>

    public Reader getReader() {
        return _reader;
    }

    public void setReader(Reader reader) {
        this._reader = reader;
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

    public int getSkipLines() {
        return _skipLines;
    }

    public void setSkipLines(int skipLines) {
        this._skipLines = skipLines;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    /**
     * Reads the entire file into a List with each element being a String[] of
     * tokens.
     *
     * @return a List of String[], with each String[] representing a line of the
     *         file.
     *
     * @throws java.io.IOException
     *             if bad things happen during the read
     */
    public List<String[]> readAll() throws IOException {
        final List<String[]> result = new LinkedList<String[]>();
        while (_hasNext) {
            final String[] nextLineAsTokens = this.readNext();
            if (nextLineAsTokens != null) {
                result.add(nextLineAsTokens);
            }
        }
        return result;

    }

    public List<Map<String, String>> readAllAsMap(boolean headerOnFirstRow) throws Exception {
        final List<String[]> data = this.readAll();
        return this.getMap(data, headerOnFirstRow);
    }

    /**
     * Closes the underlying reader.
     *
     * @throws java.io.IOException if the close fails
     */
    public void close() throws IOException {
        if(null!=__reader){
            __reader.close();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private BufferedReader getBReader(){
        if(null==__reader){
            __reader = new BufferedReader(_reader);
        }
        return __reader;
    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     *         entry.
     *
     * @throws java.io.IOException
     *             if bad things happen during the read
     */
    private String[] readNext() throws IOException {

        String nextLine = this.getNextLine();
        return _hasNext ? this.parseLine(nextLine) : null;
    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws java.io.IOException
     *             if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        if (!_linesSkiped) {
            for (int i = 0; i < _skipLines; i++) {
                this.getBReader().readLine();
            }
            _linesSkiped = true;
        }
        String nextLine = this.getBReader().readLine();
        if (nextLine == null) {
            _hasNext = false;
        }
        return _hasNext ? nextLine : null;
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine
     *            the string to parse
     * @return the comma-tokenized list of elements, or null if nextLine is null
     * @throws java.io.IOException if bad things happen during the read
     */
    private String[] parseLine(String nextLine) throws IOException {

        if (nextLine == null) {
            return null;
        }

        final List<String> tokensOnThisLine = new LinkedList<String>();
        StringBuffer sb = new StringBuffer();
        boolean inQuotes = false;
        do {
            if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n");
                nextLine = getNextLine();
                if (nextLine == null) {
                    break;
                }
            }
            for (int i = 0; i < nextLine.length(); i++) {

                char c = nextLine.charAt(i);
                if (c == _quotechar) {
                    // this gets complex... the quote may end a quoted block, or escape another quote.
                    // do a 1-char lookahead:
                    if (inQuotes // we are in quotes, therefore there can be escaped quotes in here.
                            && nextLine.length() > (i + 1) // there is indeed another character to check.
                            && nextLine.charAt(i + 1) == _quotechar) { // ..and that char. is a quote also.
                        // we have two quote chars in a row == one quote char, so consume them both and
                        // put one on the token. we do *not* exit the quoted text.
                        sb.append(nextLine.charAt(i + 1));
                        i++;
                    } else {
                        inQuotes = !inQuotes;
                        // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                        if (i > 2 //not on the begining of the line
                                && nextLine.charAt(i - 1) != _separator //not at the begining of an escape sequence
                                && nextLine.length() > (i + 1)
                                && nextLine.charAt(i + 1) != _separator //not at the	end of an escape sequence
                                ) {
                            sb.append(c);
                        }
                    }
                } else if (c == _separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb = new StringBuffer(); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);
        tokensOnThisLine.add(sb.toString());
        return tokensOnThisLine.toArray(new String[0]);

    }
    
    private List<Map<String, String>> getMap(final List<String[]> rows,
            boolean headerOnFirstRow) {
        final List<Map<String, String>> result = new LinkedList<Map<String, String>>();
        String[] names;
        if (headerOnFirstRow) {
            names = rows.remove(0);
        } else {
            names = new String[rows.get(0).length];
            for (int i = 0; i < names.length; i++) {
                names[i] = "" + (i + 1);
            }
        }

        for (String[] cols : rows) {
            Map<String, String> row = new LinkedHashMap<String, String>();
            for (int i = 0; i < cols.length; i++) {
                row.put(names[i], cols[i]);
            }
            result.add(row);
        }

        return result;
    }
}
