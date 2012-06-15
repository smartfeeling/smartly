/*
 * CSVDataFormatter.java
 *
 */

package org.smartly.commons.csv.formatter;

import org.smartly.commons.csv.ICSVConstants;
import org.smartly.commons.util.FormatUtils;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class CSVDataFormatter implements ICSVConstants {

    private String _datePattern = null;
    private String _numberPattern = DEFAULT_NUMBER_PATTERN;
    private int _dateFormat = DEFAULT_DATE_FORMAT;
    private Locale _locale = DEFAULT_LOCALE;


    private static final SimpleDateFormat
            TIMESTAMP_FORMATTER =
            new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    private static final SimpleDateFormat
            DATE_FORMATTER =
            new SimpleDateFormat("dd-MMM-yyyy");


    /**
     * Creates a new instance of CSVDataFormatter
     */
    public CSVDataFormatter() {
        this(DEFAULT_DATE_FORMAT, null, DEFAULT_LOCALE);
    }

    public CSVDataFormatter(int dateFormat, String datePattern, Locale locale) {
        _dateFormat = dateFormat;
        _datePattern = datePattern;
        _locale = locale;
    }


    public String serialize(final ResultSet rs, int colType, int colIndex)
            throws SQLException, IOException {

        String value = "";

        switch (colType) {
            case Types.BIT:
                Object bit = rs.getObject(colIndex);
                if (bit != null) {
                    value = String.valueOf(bit);
                }
                break;
            case Types.BOOLEAN:
                boolean b = rs.getBoolean(colIndex);
                if (!rs.wasNull()) {
                    value = Boolean.valueOf(b).toString();
                }
                break;
            case Types.CLOB:
                Clob c = rs.getClob(colIndex);
                if (c != null) {
                    value = this.read(c);
                }
                break;
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
            case Types.NUMERIC:
                BigDecimal bd = rs.getBigDecimal(colIndex);
                if (bd != null) {
                    value = this.formatDouble(bd); // value = "" + bd.doubleValue();
                }
                break;
            case Types.INTEGER:
            case Types.TINYINT:
            case Types.SMALLINT:
                int intValue = rs.getInt(colIndex);
                if (!rs.wasNull()) {
                    value = this.formatInteger(intValue); // value = "" + intValue;
                }
                break;
            case Types.JAVA_OBJECT:
                Object obj = rs.getObject(colIndex);
                if (obj != null) {
                    value = String.valueOf(obj);
                }
                break;
            case Types.DATE:
                java.sql.Date date = rs.getDate(colIndex);
                if (date != null) {
                    value = this.formatDate(date);
                }
                break;
            case Types.TIME:
                Time t = rs.getTime(colIndex);
                if (t != null) {
                    value = t.toString();
                }
                break;
            case Types.TIMESTAMP:
                Timestamp tstamp = rs.getTimestamp(colIndex);
                if (tstamp != null) {
                    value = TIMESTAMP_FORMATTER.format(tstamp);
                }
                break;
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.CHAR:
                value = rs.getString(colIndex);
                break;
            default:
                value = "";
        }

        if (value == null) {
            value = "";
        }

        return value;

    }

    public String serialize(Object value) {
        String result = null;
        Class type = value.getClass();
        if (type.equals(Integer.class) ||
                type.equals(Long.class)) {
            result = this.formatInteger(value);
        } else if (type.equals(Date.class)) {
            result = this.formatDate((java.sql.Date) value);
        } else if (type.equals(Float.class) ||
                type.equals(Double.class) ||
                type.equals(BigDecimal.class)) {
            result = this.formatDouble(value);
        }

        if (null == result)
            result = value.toString();
        return result;
    }

    // -------------------------------------------------------------------------
    //                      p r i v a t e
    // -------------------------------------------------------------------------

    private String read(Clob c) throws SQLException, IOException {
        final StringBuilder sb = new StringBuilder((int) c.length());
        Reader r = c.getCharacterStream();
        char[] cbuf = new char[2048];
        int n = 0;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            if (n > 0) {
                sb.append(cbuf, 0, n);
            }
        }
        return sb.toString();
    }

    private String formatDate(java.sql.Date date) {
        String result = DATE_FORMATTER.format(date);
        if (null == _datePattern) {
            result = FormatUtils.formatDate(date, _dateFormat, _locale);
        } else {
            result = FormatUtils.formatDate(date, _datePattern);
        }
        return result;
    }

    private String formatDouble(final Object number) {
        final String result = FormatUtils.formatNumber(number, _numberPattern, _locale);
        return result;
    }

    private String formatInteger(final Object number) {
        final String result = number.toString();
        return result;
    }
}
