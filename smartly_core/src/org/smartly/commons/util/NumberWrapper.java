package org.smartly.commons.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Symbol Meaning<br>
 * 0      a digit<br>
 * #      a digit, zero shows as absent<br>
 * .      placeholder for decimal separator<br>
 * ,      placeholder for grouping separator.<br>
 * E      separates mantissa and exponent for exponential formats.<br>
 * -      default negative prefix.<br>
 * %      multiply by 100 and show as percentage<br>
 * X      any other characters can be used in the prefix or suffix<br>
 * '      used to quote special characters in a prefix or suffix.<br>
 * <p/>
 * <p>A <code>NumberWrapper</code> pattern contains a positive and negative
 * subpattern, for example, <code>"#,##0.00;(#,##0.00)"</code>.  Each
 * subpattern has a prefix, numeric part, and suffix. The negative subpattern
 * is optional; if absent, then the positive subpattern prefixed with the
 * localized minus sign (code>'-'</code> in most locales) is used as the
 * negative subpattern. That is, <code>"0.00"</code> alone is equivalent to
 * <code>"0.00;-0.00"</code>.  If there is an explicit negative subpattern, it
 * serves only to specify the negative prefix and suffix; the number of digits,
 * minimal digits, and other characteristics are all the same as the positive
 * pattern. That means that <code>"#,##0.0#;(#)"</code> produces precisely
 * the same behavior as <code>"#,##0.0#;(#,##0.0#)"</code>.
 * <p/>
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing symbol,
 * location, localized, and meaning.">
 * <tr bgcolor="#ccccff">
 * <th align=left>Symbol
 * <th align=left>Location
 * <th align=left>Localized?
 * <th align=left>Meaning
 * <tr valign=top>
 * <td><code>0</code>
 * <td>Number
 * <td>Yes
 * <td>Digit
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>#</code>
 * <td>Number
 * <td>Yes
 * <td>Digit, zero shows as absent
 * <tr valign=top>
 * <td><code>.</code>
 * <td>Number
 * <td>Yes
 * <td>Decimal separator or monetary decimal separator
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>-</code>
 * <td>Number
 * <td>Yes
 * <td>Minus sign
 * <tr valign=top>
 * <td><code>,</code>
 * <td>Number
 * <td>Yes
 * <td>Grouping separator
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>E</code>
 * <td>Number
 * <td>Yes
 * <td>Separates mantissa and exponent in scientific notation.
 * <em>Need not be quoted in prefix or suffix.</em>
 * <tr valign=top>
 * <td><code>;</code>
 * <td>Subpattern boundary
 * <td>Yes
 * <td>Separates positive and negative subpatterns
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>%</code>
 * <td>Prefix or suffix
 * <td>Yes
 * <td>Multiply by 100 and show as percentage
 * <tr valign=top>
 * <td><code>&#92;u2030</code>
 * <td>Prefix or suffix
 * <td>Yes
 * <td>Multiply by 1000 and show as per mille
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>&#164;</code> (<code>&#92;u00A4</code>)
 * <td>Prefix or suffix
 * <td>No
 * <td>Currency sign, replaced by currency symbol.  If
 * doubled, replaced by international currency symbol.
 * If present in a pattern, the monetary decimal separator
 * is used instead of the decimal separator.
 * <tr valign=top>
 * <td><code>'</code>
 * <td>Prefix or suffix
 * <td>No
 * <td>Used to quote special characters in a prefix or suffix,
 * for example, <code>"'#'#"</code> formats 123 to
 * <code>"#123"</code>.  To create a single quote
 * itself, use two in a row: <code>"# o''clock"</code>.
 * </table>
 * </blockquote>
 */
public class NumberWrapper {

    private Number _value;
    private String _pattern = "#,##0.00;(#,##0.00)";
    private Locale _locale = Locale.ENGLISH;

    public NumberWrapper() {
    }

    public NumberWrapper(final Number value) {
        _value = value;
    }

    public NumberWrapper(final String value,
                         final String pattern, final Locale locale,
                         final Number defValue) {
        _pattern = pattern;
        _locale = locale;
        this.setValue(value, pattern, locale, defValue);
    }

    public void setValue(Number value) {
        _value = value;
    }

    public Number getValue() {
        return _value;
    }

    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    public String getPattern() {
        return _pattern;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setValue(final Object value) {
        this.setValue(value, _pattern, _locale, 0);
    }

    public void setValue(final Object value,
                         final String pattern) {
        this.setValue(value, pattern, _locale, 0);
    }

    public void setValue(final Object value,
                         final String pattern, final Locale locale) {
        this.setValue(value, pattern, locale, 0);
    }

    public void setValue(final Object value,
                         final String pattern, final Locale locale,
                         final Number defValue) {
        if (null != value) {
            try {
                final DecimalFormat f = new DecimalFormat(pattern,
                        new DecimalFormatSymbols(locale));
                if (value instanceof String
                        && StringUtils.hasText((String) value)) {
                    _value = f.parse((String) value);
                } else if (value instanceof Number) {
                    _value = (Number) value;
                } else {
                    _value = defValue;
                }
            } catch (ParseException ex) {
                _value = defValue;
            }
        }
    }

    public double getValueAsDouble() {
        if (null != _value) {
            return _value.doubleValue();
        }
        return 0.0;
    }

    public long getValueAsLong() {
        if (null != _value) {
            return _value.longValue();
        }
        return 0L;
    }

    public long getValueAsInteger() {
        if (null != _value) {
            return _value.intValue();
        }
        return 0;
    }

    @Override
    public String toString() {
        return toString(_pattern, _locale);
    }

    public String toString(Locale locale) {
        return toString(_pattern, locale);
    }

    public String toString(String pattern) {
        return toString(pattern, _locale);
    }

    public String toString(String pattern, Locale locale) {
        final DecimalFormat f = new DecimalFormat(pattern,
                new DecimalFormatSymbols(locale));

        return f.format(_value);
    }
}
