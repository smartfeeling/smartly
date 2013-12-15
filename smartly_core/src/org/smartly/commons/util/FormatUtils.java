package org.smartly.commons.util;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FormatUtils {

    public static interface FormatHandler{
        Object handle(final String placeholder);
    }

    public static final int STYLE_SHORT = DateFormat.SHORT;
    public static final int STYLE_MEDIUM = DateFormat.MEDIUM;
    public static final int STYLE_LONG = DateFormat.LONG;

    public static final String DEFAULT_DATEFORMAT = "yyyyMMdd";
    public static final String DEFAULT_TIMEFORMAT = "HH:mm:ss";

    //---------------------------------------------------------------------
    //  String
    //---------------------------------------------------------------------

    /**
     * Extends String.format behaviour.<br> If "text" contains more format
     * specifiers than passed arguments, excided format specifiers are replaced
     * with an empty string.
     *
     * @param text
     * @param args
     * @return A formatted string.
     */
    public static String format(final String text,
                                Object... args) {
        Object[] array = CollectionUtils.toArray(args);
        final int length = null != array ? array.length : 0;
        if (StringUtils.hasText(text) && length > 0) {
            //-- string type ("hello {0}" or "hello %s") --//
            if (text.indexOf("{0}") > -1) {
                // "hello {0}"
                final Map<String, Object> context = toContext(args);
                return formatTemplate(text, "{", "}", context);
            } else {
                // "hello %s"
                // check if format specifiers are same number of passed arguments.
                final int count = StringUtils.countOccurrencesOf(text, "%");
                if (count > length) {
                    int diff = count - length;
                    List<Object> largs = new ArrayList<Object>(Arrays.asList(array));
                    for (int i = 0; i < diff; i++) {
                        largs.add("");
                    }
                    array = largs.toArray(new Object[]{largs.size()});
                }
                return String.format(text, array);
            }
        }
        return text;
    }

    public static String formatText(final String prefix,
                                    final String suffix,
                                    final String text,
                                    final Object... args) {
        final Map<String, Object> context = toContext(args);
        if (StringUtils.hasText(text) && !context.isEmpty()) {
            return formatTemplate(text, prefix, suffix, context);
        }
        return text;
    }

    public static Map<String, Object> toContext(final Object... args) {
        final Object[] array = CollectionUtils.toArray(args);
        final Map<String, Object> context = new HashMap<String, Object>();
        for (int i = 0; i < array.length; i++) {
            final Object arg = array[i];
            context.put(i + "", arg);
        }
        return context;
    }

    public static String format(final String text,
                                final Map<String, ? extends Object> context) {
        if (StringUtils.hasText(text) && null != context && !context.isEmpty()) {
            // "hello {name}"
            return formatTemplate(text, "{", "}", context);
        }
        return text;
    }

    /**
     * Resolve encoding placeholders (i.e. &#64;) in the given text, replacing
     * them with corresponding characters (i.e. @).
     *
     * @param text the String to resolve
     * @return the resolved String
     */
    public static String resolveEncodingPlaceHolders(final String text) {
        return resolveEncodingPlaceHolders(text, "&#", ";");
    }

    /**
     * Resolve encoding placeholders (i.e. &#64;) in the given text, replacing
     * them with corresponding characters (i.e. @).
     *
     * @param text   the String to resolve
     * @param prefix The placeholder's prefix (i.e. '&#')
     * @param suffix The placeholder's suffix (i.e. ';')
     * @return the resolved String
     */
    public static String resolveEncodingPlaceHolders(final String text,
                                                     final String prefix, final String suffix) {
        if (null == text) {
            return null;
        }

        final StringBuffer buf = new StringBuffer(text);

        int startIndex = text.indexOf(prefix);
        while (startIndex != -1) {
            final int endIndex = buf.toString().indexOf(suffix, startIndex + prefix.length());
            if (endIndex != -1) {
                Character propVal = null;
                try {
                    final String placeholder = buf.substring(startIndex + prefix.length(), endIndex);
                    propVal = (char) Integer.parseInt(placeholder);
                } catch (Throwable t) {
                }
                if (propVal != null) {
                    buf.replace(startIndex, endIndex + suffix.length(), propVal.toString());
                    startIndex = buf.toString().indexOf(prefix, startIndex + propVal.toString().length());
                } else {
                    // Could not resolve placeholder
                    startIndex = buf.toString().indexOf(prefix, endIndex + suffix.length());
                }
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    /**
     * Resolve placeholders in the given text, replacing them with corresponding
     * contextData values.
     *
     * @param text        the String to resolve
     * @param prefix      The placeholder's prefix (i.e. '{')
     * @param suffix      The placeholder's suffix (i.e. '}')
     * @param contextData Data containig values. Context Data map keys to
     *                    values. Keys are String and Values are Objects
     * @return the resolved String
     */
    public static String formatTemplate(final String text,
                                        final String prefix, final String suffix,
                                        final Map<String, ?> contextData) {
        if (null == text) {
            return null;
        }

        final StringBuilder buf = new StringBuilder(text);

        int startIndex = text.indexOf(prefix);
        while (startIndex != -1) {
            final int endIndex = buf.toString().indexOf(suffix, startIndex + prefix.length());
            if (endIndex != -1) {
                final String placeholder = buf.toString().substring(startIndex + prefix.length(), endIndex);
                Object propVal;
                if (contextData.containsKey(placeholder)) {
                    if (placeholder.indexOf(".") == -1) {
                        // simple property
                        propVal = contextData.get(placeholder);
                    } else {
                        // navigate object
                        propVal = BeanUtils.getValueIfAny(contextData, placeholder);
                    }
                    if (null == propVal) {
                        propVal = "";
                    }
                } else {
                    propVal = null;
                }
                if (propVal != null) {
                    buf.replace(startIndex, endIndex + suffix.length(), propVal.toString());
                    startIndex = buf.toString().indexOf(prefix, startIndex + propVal.toString().length());
                } else {
                    // Could not resolve placeholder
                    startIndex = buf.toString().indexOf(prefix, endIndex + suffix.length());
                }
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    public static String formatTemplate(final String text,
                                        final String prefix, final String suffix,
                                        final FormatHandler handler) {
        if (null == text || null==handler) {
            return null;
        }

        final StringBuilder buf = new StringBuilder(text);

        int startIndex = text.indexOf(prefix);
        while (startIndex != -1) {
            final int endIndex = buf.toString().indexOf(suffix, startIndex + prefix.length());
            if (endIndex != -1) {
                final String placeholder = buf.toString().substring(startIndex + prefix.length(), endIndex);
                final Object propVal = handler.handle(placeholder);
                if (propVal != null) {
                    buf.replace(startIndex, endIndex + suffix.length(), propVal.toString());
                    startIndex = buf.toString().indexOf(prefix, startIndex + propVal.toString().length());
                } else {
                    // Could not resolve placeholder
                    startIndex = buf.toString().indexOf(prefix, endIndex + suffix.length());
                }
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }



    /**
     * Resolve placeholders in the given text, replacing them with corresponding
     * contextData values.
     *
     * @param text        the String to resolve
     * @param prefix      The placeholder's prefix (i.e. ':')
     * @param contextData Data containig values. Context Data map keys to
     *                    values. Keys are String and Values are Objects
     * @return the resolved String
     */
    public static String formatTemplate(final String text, final String prefix, Map<String, Object> contextData) {
        if (null == text) {
            return null;
        }

        String result = text;

        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            if (null != entry.getValue()) {
                result = result.replace(prefix.concat(entry.getKey()), entry.getValue().toString());
            }
        }
        return result;
    }


    // -------------------------------------------------------------------------
    //                      D A T E
    // -------------------------------------------------------------------------

    /**
     * Format an input date using passed Locale settings and a standard mask (yyyyMMdd).<br>
     * Ex: for a date "19680121", if locale is 'US'
     * return value: <br>
     * "01-21-1968"
     *
     * @param sDate  String date to format. Ex: '21-01-1968'
     * @param locale Locale
     * @return Formatted date using Locale settings and "yyyyMMdd" mask.
     */
    public static String formatDate(String sDate, Locale locale) {
        String result = null;
        try {
            String inputMask = DEFAULT_DATEFORMAT;
            result = formatDate(sDate, inputMask, locale);
        } catch (Exception ex) {
            result = "";
        }
        return result;
    }

    /**
     * Format an input date using passed Locale settings.<br>
     * Ex: for a date "21-01-1968", with mask "dd-MM-yyyy", if locale is 'US'
     * return value: <br>
     * "01-21-1968"
     *
     * @param sDate     String date to format. Ex: '21-01-1968'
     * @param inputMask Format of input date. Ex: 'dd-MM-yyyy'
     * @param locale    Locale
     * @return Formatted date using Locale settings
     */
    public static String formatDate(String sDate, String inputMask, Locale locale) {
        String result = null;
        DateWrapper date = null;
        try {
            date = new DateWrapper(sDate, inputMask);
            result = date.toString(DateFormat.MEDIUM, locale);
        } catch (Exception ex) {
            result = ex.getMessage();
        }

        return result;
    }

    /**
     * Format an input date using output mask.<br>
     * Ex: for a date "21-01-1968", with input mask "dd-MM-yyyy", if outpurMask
     * is "yyyyMMdd" return value: <br>
     * "19680121"
     *
     * @param sDate      String date to format. Ex: '21-01-1968'
     * @param inputMask  Format of input date. Ex: 'dd-MM-yyyy'
     * @param outputMask Format of output mask (desired output date format).<br>
     *                   Ex: 'yyyyMMdd'
     * @return Formatted date. Ex: "19680121"
     */
    public static String formatDate(String sDate, String inputMask, String outputMask) {
        String result = null;
        DateWrapper date = null;
        try {
            date = new DateWrapper(sDate, inputMask);
            result = date.toString(outputMask);
        } catch (Exception ex) {
            result = ex.getMessage();
        }

        return result;
    }

    /**
     * Format a date using MEDIUM format.
     *
     * @param dateToFormat Date to format
     * @param locale       Locale
     * @return String formatted date.
     */
    public static String formatDate(final Date dateToFormat, final Locale locale) {
        return formatDate(dateToFormat, DateFormat.MEDIUM, locale);
    }

    public static String formatDate(final long dateToFormat, final Locale locale) {
        final Date date = new Date(dateToFormat);
        return formatDate(date, locale);
    }

    public static String formatDate(final long dateToFormat, final String template) {
        final Date date = new Date(dateToFormat);
        return formatDate(date, template);
    }

    public static String formatDate(final long dateToFormat, int style, Locale locale) {
        return formatDate(new Date(dateToFormat), style, locale, false);
    }

    public static String formatDate(final Date dateToFormat, final Locale locale,
                                    final boolean includetime) {
        return formatDate(dateToFormat, DateFormat.MEDIUM, locale, includetime);
    }

    /**
     * Format a date using passed style.<br>
     * Styles:<br>
     * - FULL = 0<br>
     * - LONG = 1<br>
     * - MEDIUM = 2<br>
     * - SHORT = 3
     *
     * @param dateToFormat Date to format
     * @param style        Date format style: FULL = 0, LONG = 1, MEDIUM = 2, SHORT = 3.
     * @param locale       Locale
     * @return String formatted date.
     */
    public static String formatDate(final Date dateToFormat,
                                    final int style,
                                    final Locale locale) {
        return formatDate(dateToFormat, style, locale, false);
    }

    /**
     * Format a date using passed style.<br>
     * Styles:<br>
     * - FULL = 0<br>
     * - LONG = 1<br>
     * - MEDIUM = 2<br>
     * - SHORT = 3
     *
     * @param dateToFormat Date to format
     * @param style        Date format style: FULL = 0, LONG = 1, MEDIUM = 2, SHORT = 3.
     * @param locale       Locale
     * @param includetime  If true also time in format "hh:mm:ss" is included in output
     * @return formatted date.
     */
    public static String formatDate(final Date dateToFormat,
                                    final int style,
                                    final Locale locale,
                                    final boolean includetime) {
        String result = null;
        try {
            final DateWrapper date = new DateWrapper(dateToFormat);
            result = date.toString(style, locale);
            if (includetime) {
                result = result + " " + date.toString(DEFAULT_TIMEFORMAT);
            }
        } catch (Exception ex) {
            result = "";
        }
        return result;
    }

    public static String formatDate(final Date dateToFormat) {
        return formatDate(dateToFormat, DEFAULT_DATEFORMAT);
    }

    /**
     * Format a date using passed outputFormat as mask.<br>
     * i.e. : if outputFormat is "yyyyMMdd" this date "21-jan-1968" will be formatted "19680121"
     *
     * @param dateToFormat Date to format
     * @param outputFormat Date format
     * @return String formatted date.
     */
    public static String formatDate(final Date dateToFormat,
                                    final String outputFormat) {
        String result = null;
        try {
            final DateWrapper date = new DateWrapper(dateToFormat);
            result = date.toString(outputFormat);
        } catch (Exception ex) {
            result = "";
        }
        return result;
    }

    public static String formatDateRange(final Date fromDate,
                                         final Date toDate, final Locale locale) {
        final DateWrapper date1 = new DateWrapper(fromDate);
        final int day1 = date1.getDay();
        final int month1 = date1.getMonth();
        final int year1 = date1.getYear();
        final DateWrapper date2 = new DateWrapper(toDate);
        final int day2 = date2.getDay();
        final int month2 = date2.getMonth();
        final int year2 = date2.getYear();

        if (year1 == year2) {
            if (month1 == month2) {
                //-- same month --//
                if (day1 == day2) {
                    return FormatUtils.formatDate(fromDate, 2, locale);
                } else {
                    return day1 + "-" + day2
                            + " " + DateUtils.getMonthAsString(month1, locale)
                            + " " + year1;
                }
            } else {
                //-- not same month --//
                return day1 + " " + DateUtils.getShortMonthAsString(month1, locale)
                        + " - "
                        + day2 + " " + DateUtils.getShortMonthAsString(month2, locale)
                        + " " + year1;
            }
        }

        return FormatUtils.formatDate(fromDate, 2, locale)
                + " " + FormatUtils.formatDate(toDate, 2, locale);
    }

    public static String getNow() {
        return FormatUtils.formatDate(DateUtils.now());
    }

    /**
     * Return the pattern of a certain date style using current locale.
     *
     * @param style Date format style. i.e. DateFormat.SHORT
     * @return Patter of passed style and locale. i.e "M/d/yy"
     */
    public static String getDateFormatLocalizedPattern(int style) {
        return getDateFormatLocalizedPattern(style, LocaleUtils.getCurrent());
    }

    /**
     * Return the pattern of a certain date style.
     *
     * @param style  Date format style. i.e. DateFormat.SHORT
     * @param locale Locale
     * @return Patter of passed style and locale. i.e "M/d/yy"
     */
    public static String getDateFormatLocalizedPattern(int style, Locale locale) {
        final SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance(style, locale);
        return df.toLocalizedPattern();
    }

    /**
     * Return the pattern of a certain date style using current locale.
     *
     * @param style Date format style. i.e. DateFormat.SHORT
     * @return Patter of passed style and locale. i.e "M/d/yy"
     */
    public static String getDateFormatPattern(int style) {
        return getDateFormatPattern(style, LocaleUtils.getCurrent());
    }

    /**
     * Return the pattern of a certain date style.
     *
     * @param style  Date format style. i.e. DateFormat.SHORT
     * @param locale Locale
     * @return Patter of passed style and locale. i.e "M/d/yy"
     */
    public static String getDateFormatPattern(int style, Locale locale) {
        final SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance(style, locale);
        return df.toPattern();
    }

    // -------------------------------------------------------------------------
    //                      D O U B L E
    // -------------------------------------------------------------------------

    /**
     * Format a Double number using Locale settings
     *
     * @param value  Double number to format
     * @param locale Locale
     * @return Formatted number. Ex: '1.256,78'
     */
    public static String formatDouble(Double value, Locale locale) {
        return formatDouble(new BigDecimal(value), locale);
    }

    /**
     * Format a Double number using Locale settings
     *
     * @param value  BigDecimal number to format
     * @param locale Locale
     * @return Formatted number. Ex: '1.256,78'
     */
    public static String formatDouble(final BigDecimal value,
                                      final Locale locale) {
        String result = null;
        try {
            final NumberWrapper number = new NumberWrapper(value);
            result = number.toString(locale);
        } catch (Exception ex) {
            result = "0.0";
        }
        return result;
    }

    /**
     * Format a Double number using Locale settings or current user
     *
     * @param value  Double number (as String) to format
     * @param locale Locale
     * @return Formatted number. Ex: '1.256,78'
     */
    public static String formatDouble(String value, final Locale locale) {
        value = value.replace(",", "\\.");
        final BigDecimal bd = new BigDecimal(value);
        final NumberWrapper number = new NumberWrapper(bd);
        final String result = number.toString(locale);
        return result;
    }

    public static String formatInteger(final Integer value,
                                       final Locale locale) {
        final String pattern = "#,##0;(#,##0)";
        return formatNumber(value, pattern, locale);
    }

    public static String formatNumber(final Object value,
                                      final Locale locale) {
        final String pattern = "#,##0.0###;(#,##0.0###)";

        return formatNumber(value, pattern, locale);
    }

    public static String formatNumber(final Object value,
                                      final Locale locale,
                                      final int minDecimals) {
        final String pattern = getPattern(minDecimals);

        return formatNumber(value, pattern, locale);
    }

    public static String formatNumber(final Object value,
                                      final String pattern,
                                      final Locale locale) {
        String result = null;
        try {
            if (null != value) {
                final NumberWrapper decodedNumber = new NumberWrapper();
                decodedNumber.setLocale(locale);
                decodedNumber.setPattern(getPattern(pattern));
                decodedNumber.setValue(value);
                result = decodedNumber.toString();
            } else {
                throw new Exception("Number and Pattern cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    // -------------------------------------------------------------------------
    //                  p r i v a t e
    // -------------------------------------------------------------------------
    private static String getPattern(final String pattern) {
        // pattern "n,nn0.nn;(n,nn0.nn)" become "#,##0.##;(#,##0.##)"
        return pattern.replace('n', '#');
    }

    private static String getPattern(final int minDecimals) {
        final int pos = 4 - (minDecimals > 4 ? 4 : minDecimals);
        String d = StringUtils.fillString("", "#", pos);
        d = StringUtils.fillString(d, "0", 4);
        return "#,##0.".concat(d).concat(";(#,##0.").concat(d).concat(")");
    }



}
