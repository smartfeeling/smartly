/*
 * VLCFormatter.java
 *
 *
 */
package org.smartly.packages.velocity.impl.vtools;

import org.smartly.commons.util.*;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
public final class Formatter
        implements IVLCTool {

    public static final String NAME = "fmt";
    // default locale
    private static final Locale DEFAULT_LOCALE = Locale.US;
    private static final String DEFAULT_PATTERN = "#,##0.0###;(#,##0.0###)";

    public Formatter() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    /*
     * Date Utility
     */
    public String formatDate(final Long date) {
        final Date d = new Date(date);
        return this.formatDate(d);
    }

    public String formatDate(final Date date) {
        String result = null;
        try {
            final DateWrapper dd = new DateWrapper();
            if (null != date) {
                dd.setDateTime(date);
                result = dd.toString(DEFAULT_LOCALE);
            } else {
                throw new Exception("Date cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatDate(final Long date, final String dateFormat) {
        final Date d = new Date(date);
        return this.formatDate(d, dateFormat);
    }

    public String formatDate(final Date date, final String dateFormat) {
        String result = null;
        try {
            final DateWrapper dd = new DateWrapper();
            if (null != date) {
                dd.setDateTime(date);
                result = dd.toString(dateFormat);
            } else {
                throw new Exception("Date cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatDate(final Long date, final String dateFormat, final String lang) {
        final Date d = new Date(date);
        return this.formatDate(d, dateFormat, lang);
    }

    public String formatDate(final Date date, final String dateFormat, final String lang) {
        String result = null;
        try {
            final DateWrapper dd = new DateWrapper();
            final Locale locale = LocaleUtils.getLocaleFromString(lang);
            if (null != date) {
                dd.setDateTime(date);
                if (StringUtils.hasText(dateFormat)) {
                    result = dd.toString(dateFormat, locale);
                } else {
                    result = dd.toString(locale);
                }
            } else {
                throw new Exception("Date cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatDate(final String inputDate,
                             final String inputDateFormat, final String outputDateFormat) {
        String result = null;
        try {
            final DateWrapper dd = new DateWrapper();
            if (null != inputDate && null != inputDateFormat) {
                dd.setDateTime(inputDate, inputDateFormat);
                result = dd.toString(outputDateFormat);
            } else {
                throw new Exception("Date and Format cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatMonth(final Object month) {
        return this.formatMonth(month, DEFAULT_LOCALE);
    }

    public String formatMonth(final Object month, final Object olocale) {
        final int m = ConversionUtils.toInteger(month, 1);
        final Locale locale = LocaleUtils.getLocaleFromObject(olocale, DEFAULT_LOCALE);
        return this.formatMonth(m, locale);
    }

    public String formatNumber(final Object value, final String lang, final String country) {
        final Locale locale = LocaleUtils.getLocale(new Locale(lang, country));
        return formatNumber(value, locale);
    }

    /*
    * Number Utility
    */
    @SuppressWarnings({"unchecked"})
    public String formatNumber(final Object value) {
        String result = null;
        String pattern = DEFAULT_PATTERN;
        try {
            final NumberWrapper dn = new NumberWrapper();
            if (null != value) {
                final Object n = this.getNumber(value, 0);
                dn.setLocale(DEFAULT_LOCALE);
                dn.setPattern(pattern);
                dn.setValue(n);
                result = dn.toString();
            } else {
                throw new Exception("Number cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    public String formatNumber(final Object value, final Locale locale) {
        String result = null;
        try {
            final NumberWrapper dn = new NumberWrapper();
            if (null != value) {
                final Object n = this.getNumber(value, 0);
                dn.setLocale(locale);
                dn.setPattern(this.getNumberPattern(DEFAULT_PATTERN));
                dn.setValue(n);
                result = dn.toString();
            } else {
                throw new Exception("Number and Pattern cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    public String formatNumber(final Object value, final String pattern) {
        return this.formatNumber(value, pattern, DEFAULT_LOCALE);
    }

    @SuppressWarnings({"unchecked"})
    public String formatNumber(final Object value, final String pattern, final Locale locale) {
        String result = null;
        try {
            final NumberWrapper dn = new NumberWrapper();
            if (null != value) {
                final Object n = this.getNumber(value, 0);
                dn.setLocale(locale);
                dn.setPattern(this.getNumberPattern(pattern));
                dn.setValue(n);
                result = dn.toString();
            } else {
                throw new Exception("Number and Pattern cannot be null.");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatCurrency(final Object value, final String lang, final String country) {
        final Locale locale = LocaleUtils.getLocale(lang, country);
        return formatCurrency(value, locale);
    }

    public String formatCurrency(final Object value, final String lang, final String country,
                                 final boolean includesymbol) {
        final Locale locale = LocaleUtils.getLocale(lang, country);
        return formatCurrency(value, locale, includesymbol);
    }

    public String formatCurrency(final Object value, final Locale locale) {
        return this.formatCurrency(value, locale, false);
    }

    public String formatCurrency(final Object value, final Locale locale,
                                 final boolean includesymbol) {
        String result = null;
        try {
            final DecimalFormatSymbols dfs = LocaleUtils.getDecimalFormatSymbols(locale);
            final int dec = dfs.getCurrency().getDefaultFractionDigits();
            final double ivalue = ConversionUtils.toDouble(value, dec);
            final String pattern = this.getCurrencyPattern(dfs, includesymbol);
            result = this.formatNumber(ivalue, pattern, locale);
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatPercent(final Object value) {
        String result = "";
        try {
            double dvalue = ConversionUtils.toDouble(value, 2);
            if (dvalue < 1) {
                dvalue *= 100;
            }
            result = (int) dvalue + "%";
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }

    public String formatHtml(final String value) {
        String result = null;
        try {
            if (StringUtils.hasText(value)) {
                result = value.replaceAll("\n", "<br>");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return null != result ? result : "";
    }

    /**
     * Bring text to Upper Case
     *
     * @param text Some text (for example: "hello")
     * @return Upper Case text, i.e. "HELLO"
     */
    public String upper(final String text) {
        return StringUtils.hasText(text) ? text.toUpperCase() : "";
    }

    /**
     * Bring text to Lower Case
     *
     * @param text Some text (for example: "HELLO")
     * @return Lower Case text, i.e. "hello"
     */
    public String lower(final String text) {
        return StringUtils.hasText(text) ? text.toLowerCase() : "";
    }

    /**
     * Trunc text and return text with ellipsis.<br/>
     * i.e. trunc("Hello world", 5) returns "Hello..."
     *
     * @param text   Text to trunc
     * @param length max length for text.
     * @return Truncated text with ellipsis
     */
    public String trunc(final String text, final int length) {
        return StringUtils.leftStr(text, length, true);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Object getNumber(final Object value,
                             final Object defaultValue) throws Exception {
        if (null == value) {
            return defaultValue;
        }
        String strValue = value.toString();

        try {
            return new BigDecimal(strValue);
        } catch (NumberFormatException ex) {
            throw new Exception("Number Format Exception, '" + strValue + "' is not a number.");
        }
    }

    private String getNumberPattern(final String pattern) {
        // pattern "n,nn0.nn;(n,nn0.nn)" become "#,##0.##;(#,##0.##)"
        return pattern.replace('n', '#');
    }

    private String getCurrencyPattern(final DecimalFormatSymbols dfs, final boolean includesymbol) {
        // "#,##0.##;(#,##0.##)"
        final String symbol = dfs.getCurrency().getSymbol();
        final int dec = dfs.getCurrency().getDefaultFractionDigits();
        final char decsep = '.';
        final char grpsep = ',';//dfs.getGroupingSeparator();
        final char digit = '#'; // dfs.getDigit();

        final StringBuilder sb = new StringBuilder();
        // positive
        if (includesymbol) {
            sb.append(symbol);
        }
        sb.append(digit).append(grpsep).append(digit).append(digit).append("0"); // #,##0
        if (dec > 0) {
            sb.append(decsep);
            for (int i = 0; i < dec; i++) {
                sb.append("0");
            }
        }

        // negative
        sb.append("; ").append("(");
        if (includesymbol) {
            sb.append(symbol);
        }
        sb.append(digit).append(grpsep).append(digit).append(digit).append("0"); // #,##0
        if (dec > 0) {
            sb.append(decsep);
            for (int i = 0; i < dec; i++) {
                sb.append("0");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    private String formatMonth(final int month, final Locale locale) {
        String result = null;
        try {
            final DateWrapper dd = new DateWrapper();
            if (month > 0) {
                dd.setMonth(month);
                result = dd.toString("MMMM", locale);
            } else {
                throw new Exception("Month cannot be zero (allowed 1-12).");
            }
        } catch (Exception ex) {
            result = "Error: [" + ex.getMessage() + "]";
        }
        return result;
    }
}
