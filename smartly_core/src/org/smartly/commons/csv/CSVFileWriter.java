/*
 * CSVFileWriter.java
 *
 */
package org.smartly.commons.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public final class CSVFileWriter extends CSVWriter {

    private File _file;

    public CSVFileWriter() {
    }

    public CSVFileWriter(final String fileName) throws IOException {
        this(new File(fileName),
                DEFAULT_SEPARATOR,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                DEFAULT_LOCALE);
    }

    public CSVFileWriter(final String fileName, char separator,
            final Locale locale) throws IOException {
        this(new File(fileName),
                separator,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                locale);
    }

    public CSVFileWriter(final File file, char separator,
            final Locale locale) throws IOException {
        this(file,
                separator,
                DEFAULT_QUOTE_CHARACTER,
                DEFAULT_LINE_END,
                locale);
    }

    public CSVFileWriter(final File file, char separator,
            char quoteChar, final String lineEnd,
            final Locale locale) throws IOException {
        super.setLineEnd(lineEnd);
        super.setLocale(locale);
        super.setQuotechar(quoteChar);
        super.setSeparator(separator);
        this.setFile(file);
    }

    public File getFile() {
        return _file;
    }

    public void setFile(final File file) throws IOException {
        _file = file;
        super.setWriter(new FileWriter(_file));
    }

    @Override
    public int writeAll(List<String[]> allLines) {
        return super.writeAll(allLines);
    }

    @Override
    public int writeAll(List<Map> allLines, boolean includeColumnNames) {
        return super.writeAll(allLines, includeColumnNames);
    }

    @Override
    public int writeAll(ResultSet rs, boolean includeColumnNames) throws SQLException, IOException {
        return super.writeAll(rs, includeColumnNames);
    }

    @Override
    public void writeNext(String[] nextLine) {
        super.writeNext(nextLine);
    }
}
