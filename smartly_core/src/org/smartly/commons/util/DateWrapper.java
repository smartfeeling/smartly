package org.smartly.commons.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class to encode/decode datetime.
 */
public class DateWrapper {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final int STYLE_SHORT = DateFormat.SHORT;
    public static final int STYLE_MEDIUM = DateFormat.MEDIUM;
    public static final int STYLE_LONG = DateFormat.LONG;
    
    public static final String[] PATTERNS = new String[]{
        "yyyyMMdd", // 20130105
        "EEE, d MMM yyyy HH:mm:ss Z", // Tue, 5 Jan 2013 21:47:38 +0200
        "EEE, d MMM yyyy HH:mm:ss", // Tue, 5 Jan 2013 21:47:38
        "EEE, d-MMM-yyyy HH:mm:ss z", // Tue, 5-Jan-2013 21:47:38 GMT
        "EEE, d-MMM-yyyy HH:mm:ss", // Tue, 5-Jan-2013 21:47:38 GMT
        "EEE, d MM yyyy HH:mm:ss Z", // Tue, 5 06 2013 21:47:38 +0200
        "EEE, d MM yyyy HH:mm:ss", // Tue, 5 06 2013 21:47:38
        "EEE, d-MM-yyyy HH:mm:ss z", // Tue, 5-06-2013 21:47:38 GMT
        "EEE, d-MM-yyyy HH:mm:ss", // Tue, 5-06-2013 21:47:38
        "EEE, dd MMM yyyy HH:mm:ss Z", // Tue, 05 Jan 2013 21:47:38 +0200
        "EEE, dd MMM yyyy HH:mm:ss", // Tue, 05 Jan 2013 21:47:38
        "EEE, dd-MMM-yyyy HH:mm:ss z", // Tue, 05-Jan-2013 21:47:38 GMT
        "EEE, dd-MMM-yyyy HH:mm:ss", // Tue, 05-Jan-2013 21:47:38 GMT
        "EEE, dd MM yyyy HH:mm:ss Z", // Tue, 05 06 2013 21:47:38 +0200
        "EEE, dd MM yyyy HH:mm:ss", // Tue, 05 06 2013 21:47:38
        "EEE, dd-MM-yyyy HH:mm:ss z", // Tue, 05-06-2013 21:47:38 GMT
        "EEE, dd-MM-yyyy HH:mm:ss" // Tue, 05-06-2013 21:47:38
    };
    public static final String DATEFORMAT_DEFAULT = PATTERNS[0];
    public static final String DATEFORMAT_GENERAL = PATTERNS[1];
    public static final String TIMEFORMAT_DEFAULT = "hh:mm:ss";
    // ------------------------------------------------------------------------
    //                      Variables
    // ------------------------------------------------------------------------
    private int _year = 0;
    private int _month = 0;
    private int _day = 0;
    private int _hour = 0;
    private int _minutes = 0;
    private int _seconds = 0;
    private Calendar _calendar = null;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    /**
     * Create a DecodedDate Object.
     */
    public DateWrapper() {
        init();
    }

    /**
     * Create a DecodedDate Object.
     * @param date Date 
     */
    public DateWrapper(final Date date) {
        init(date);
    }

    /**
     * Create a DecodedDate Object.
     * @param dateTime 
     */
    public DateWrapper(final long dateTime) {
        Date dt = new Date(dateTime);
        init(dt);
    }

    /**
     * Create a DecodedDate Object.
     * 
     * @param date Date
     * @param pattern es: "dd/MM/yy", "MM/dd/yy", "yyyyMMdd"
     * @throws Exception
     */
    public DateWrapper(final String date,
                       final String pattern) throws Exception {
        this(date, pattern, LocaleUtils.getCurrent());
    }

    public DateWrapper(final String date,
                       final String pattern, final Locale locale) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        Date dt;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            dt = new Date(0L);
            throw new Exception("Date Parse exception: " + e.getMessage());
        }
        init(dt);
    }

    /**
     * Create an instance of Date decoder.
     * @param date Date
     * @param dateStyle Date style in DateFormat codification (FULL = 0, LONG = 1, MEDIUM = 2, SHORT = 3).
     * @throws Exception
     */
    public DateWrapper(String date, int dateStyle) throws Exception {
        DateFormat format = DateFormat.getDateInstance(dateStyle);
        Date dt;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            dt = new Date(0L);
            throw new Exception("Date Parse exception: " + e.getMessage());
        }
        init(dt);
    }

    /**
     *
     * @param date
     * @param dateStyle
     * @param locale
     * @throws Exception
     */
    public DateWrapper(final String date,
                       final int dateStyle, final Locale locale) throws Exception {
        DateFormat format = DateFormat.getDateInstance(dateStyle, locale);
        Date dt;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            dt = new Date(0L);
            throw new Exception("Date Parse exception: " + e.getMessage());
        }
        this.init(dt);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateWrapper other = (DateWrapper) obj;
        if (this._year != other._year) {
            return false;
        }
        if (this._month != other._month) {
            return false;
        }
        if (this._day != other._day) {
            return false;
        }
        if (this._hour != other._hour) {
            return false;
        }
        if (this._minutes != other._minutes) {
            return false;
        }
        if (this._seconds != other._seconds) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this._year;
        hash = 29 * hash + this._month;
        hash = 29 * hash + this._day;
        hash = 29 * hash + this._hour;
        hash = 29 * hash + this._minutes;
        hash = 29 * hash + this._seconds;
        return hash;
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------
    
    public Calendar getCalendar() {
        return _calendar;
    }
    
    public Date getDateTime() {
        return _calendar.getTime();
    }

    public void setDateTime(Date value) {
        init(value);
    }

    public void setDateTime(final String date, final String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        Date dt;
        dt = format.parse(date);

        init(dt);
    }

    public void setDateTime(final String date, final String pattern, Locale locale) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);

        Date dt;
        dt = format.parse(date);

        init(dt);
    }

    public void setDateTime(final String date, int dateFormat, Locale locale) throws ParseException {
        DateFormat format = DateFormat.getDateInstance(dateFormat, locale);

        Date dt;
        dt = format.parse(date);

        init(dt);
    }

    public void setDateTime(final String date, int dateFormat) throws ParseException {
        DateFormat format = DateFormat.getDateInstance(dateFormat);

        Date dt;
        dt = format.parse(date);

        init(dt);
    }

    public int getYear() {
        return _year;
    }

    public void setYear(int value) {
        _calendar.set(Calendar.YEAR, value);
        _year = value;
    }

    public String getMonthAsString() {
        return DateUtils.getMonthAsString(_month);
    }

    public String getMonthShortAsString() {
        return DateUtils.getShortMonthAsString(_month);
    }

    public int getMonth() {
        return _month;
    }

    public void setMonth(int value) {
        int month = value - 1; // Zero based months
        _calendar.set(Calendar.MONTH, month);
        _month = month;
    }

    public int getDay() {
        return _day;
    }

    public void setDay(int value) {
        _calendar.set(DateUtils.DAY, value);
        _day = value;
    }

    public int getHour() {
        return _hour;
    }

    public void setHour(int value) {
        _calendar.set(DateUtils.HOUR, value);
        _hour = value;
    }

    public int getMinute() {
        return _minutes;
    }

    public void setMinute(int value) {
        _calendar.set(DateUtils.MINUTE, value);
        _minutes = value;
    }

    public int getSecond() {
        return _seconds;
    }

    public void setSecond(int value) {
        _calendar.set(DateUtils.SECOND, value);
        _seconds = value;
    }

    /**
     * Return a formatted date using "yyyyMMdd" pattern.
     * @return Date or Time formatted. i.e.: "20070121"
     */
    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        return f.format(_calendar.getTime());
    }

    /**
     * Return a formatted date
     * @param dateFormat es: "dd/MM/yy", "MM/dd/yy", "yyyyMMdd", "HH:mm:ss", "hh.mm.ss"
     * @return Date or Time formatted.
     */
    public String toString(String dateFormat) {
        SimpleDateFormat f = new SimpleDateFormat(dateFormat);
        return f.format(_calendar.getTime());
    }

    /**
     * Return a formatted date
     * @param dateFormat e.g.: "dd/MM/yy", "MM/dd/yy", "yyyyMMdd", "HH:mm:ss", "hh.mm.ss"
     * @param locale e.g.: Locale.UK
     * @return Date or Time formatted.
     */
    public String toString(String dateFormat, Locale locale) {
        SimpleDateFormat f = new SimpleDateFormat(dateFormat, locale);
        return f.format(_calendar.getTime());
    }

    /**
     * Return a formatted date
     * @param locale Locale to format the date to.
     * @return Date or Time formatted.
     */
    public String toString(Locale locale) {
        int style = DateFormat.SHORT;
        return toString(style, locale);
    }

    /**
     * Return a formatted date
     * @param style Ex: the given formatting style.
     * For example, SHORT for "M/d/yy" in the US locale.
     * (DateFormat.SHORT, DateFormat.LONG, DateFormat.MEDIUM, DateFormat.FULL)
     * @param locale Locale to format the date to.
     * @return Formatted date
     */
    public String toString(int style, Locale locale) {
        DateFormat f = DateFormat.getDateInstance(style, locale);
        return f.format(_calendar.getTime());
    }

    public long getTime() {
        return _calendar.getTimeInMillis();
    }

    public void set(int year, int month, int day) {
        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void init() {
        if (null == _calendar) {
            _calendar = Calendar.getInstance();
        }
        init(_calendar);
    }

    private void init(Date date) {
        if (null == _calendar) {
            _calendar = Calendar.getInstance();
        }
        _calendar.setTime(date);
        _calendar.setTimeZone(TimeZone.getDefault());
        this.init(_calendar);
    }

    private void init(Calendar calendar) {
        _year = calendar.get(Calendar.YEAR);
        _month = calendar.get(Calendar.MONTH) + 1;
        _day = calendar.get(Calendar.DAY_OF_MONTH);
        _hour = calendar.get(Calendar.HOUR_OF_DAY);
        _minutes = calendar.get(Calendar.MINUTE);
        _seconds = calendar.get(Calendar.SECOND);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    public static DateWrapper parse(final String date) {
        return parse(date, LocaleUtils.getLocale(Locale.ENGLISH));
    }

    public static DateWrapper parse(final String date, final Locale locale) {
        // try with date
        for (final String pattern : PATTERNS) {
            try {
                final DateWrapper dt = new DateWrapper(date, pattern, locale);
                return dt;
            } catch (Throwable t) {
            }
        }
        // try with clear date
        final String clear = date.trim().
                replace("GMT", "").
                replace(", ", " ").
                replace("-", "");
        for (final String pattern : PATTERNS) {
            try {
                final DateWrapper dt = new DateWrapper(clear, pattern, locale);
                return dt;
            } catch (Throwable ignored) {
            }
        }
        
        return null;
    }

    /**
     * Return String format of passed date. Uses ENGLISH as locale.
     * @param date Date to format
     * @return "EEE, d MMM yyyy HH:mm:ss"  Tue, 5 Jan 2013 21:47:38
     */
    public static String format(final Date date) {
        return format(date, LocaleUtils.getLocale(Locale.ENGLISH));
    }

    /**
     * Return String format of passed date.
     * @param date Date to format
     * @param locale Locale
     * @return "EEE, d MMM yyyy HH:mm:ss"  Tue, 5 Jan 2013 21:47:38
     */
    public static String format(final Date date, final Locale locale) {
        final DateWrapper dt = new DateWrapper(date);
        return dt.toString(PATTERNS[2], locale);
    }
}
