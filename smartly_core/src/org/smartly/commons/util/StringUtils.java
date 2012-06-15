package org.smartly.commons.util;

import org.smartly.IConstants;
import org.smartly.commons.lang.CharEncoding;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;


public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Split a string into an array of strings. Use comma and space
     * as delimiters.
     *
     * @param str the string to split
     * @return the string split into a string array
     */
    public static String[] split(final String str) {
        return split(str, ", \t\n\r\f");
    }

    /**
     * Split a string into an array of strings.
     *
     * @param str   the string to split
     * @param delim the delimiter to split the string at
     * @return the string split into a string array
     */
    public static String[] split(final String str, final String delim) {
        if (str == null) {
            return new String[0];
        }
        final StringTokenizer st = new StringTokenizer(str, delim);
        final String[] s = new String[st.countTokens()];
        for (int i = 0; i < s.length; i++) {
            s[i] = st.nextToken();
        }
        return s;
    }

    public static String concatPaths(final String path1, final String path2) {
        if (StringUtils.hasText(path1)) {
            if (!path1.endsWith(IConstants.FOLDER_SEPARATOR)
                    && !path2.startsWith(IConstants.FOLDER_SEPARATOR)) {
                return path1.concat(IConstants.FOLDER_SEPARATOR).concat(path2);
            } else {
                return path1.concat(path2);
            }
        } else {
            return path2;
        }
    }

    public static String concatArgs(final Object... args) {
        final StringBuffer result = new StringBuffer();
        if (null != args && args.length > 0) {
            for (final Object arg : args) {
                result.append(arg);
            }
        }
        return result.toString();
    }

    public static String concatArgsEx(final String separator,
                                      final Object... args) {
        final StringBuffer result = new StringBuffer();
        if (null != args && args.length > 0) {
            for (final Object arg : args) {
                if (hasText(separator) && result.length() > 0) {
                    result.append(separator);
                }
                result.append(arg);
            }
        }
        return result.toString();
    }

    /**
     * Append a value separated from comma (",") from other values to a
     * StringBuilder.
     *
     * @param value
     * @param sb
     */
    public static void append(final Object value,
                              final StringBuilder sb) {
        append(value, sb, ",");
    }

    /**
     * Append a value and a separator to a StringBuilder.
     *
     * @param value
     * @param sb
     * @param delim
     */
    public static void append(final Object value,
                              final StringBuilder sb, final String delim) {
        if (null != sb && null != value) {
            if (sb.length() > 0) {
                sb.append(delim);
            }
            sb.append(value);
        }
    }

    /**
     * Compare two strings also if null.
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsIgnoreCase(final Object str1, final Object str2) {
        if (null != str1 && null != str2) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        }

        return null == str1 && null == str2;
    }

    public static boolean equals(final Object str1, final Object str2) {
        if (null != str1 && null != str2) {
            return str1.toString().equals(str2.toString());
        }

        return null == str1 && null == str2;
    }

    //---------------------------------------------------------------------
    // General convenience methods for working with Strings
    //---------------------------------------------------------------------

    /**
     * Case insensitive method.
     *
     * @param text
     * @param charSequence
     * @return
     */
    public static boolean contains(final String text,
                                   final String charSequence) {
        if (hasText(text) && hasText(charSequence)) {
            return text.toLowerCase().contains(charSequence.toLowerCase());
        }
        return false;
    }

    /**
     * Case insentive method.
     *
     * @param text
     * @param tokens
     * @return
     */
    public static boolean contains (final String text,
                                   final String[] tokens) {
        for (final String charSequence : tokens) {
            if (StringUtils.contains(text, charSequence)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Checks if the String contains any character in the given
     * set of characters.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.
     * A <code>null</code> or zero length search array will return <code>false</code>.</p>
     *
     * <pre>
     * StringUtils.containsAny(null, *)                = false
     * StringUtils.containsAny("", *)                  = false
     * StringUtils.containsAny(*, null)                = false
     * StringUtils.containsAny(*, [])                  = false
     * StringUtils.containsAny("zzabyycdxx",['z','a']) = true
     * StringUtils.containsAny("zzabyycdxx",['b','y']) = true
     * StringUtils.containsAny("aba", ['z'])           = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the <code>true</code> if any of the chars are found,
     * <code>false</code> if no match or null input
     * @since 2.4
     */
    public static boolean containsAny(final String str, final char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>Checks that the String does not contain certain characters.</p>
     *
     * <p>A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.</p>
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", '')      = true
     * StringUtils.containsNone("abab", 'xyz') = true
     * StringUtils.containsNone("ab1", 'xyz')  = true
     * StringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param invalidChars  an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     */
    public static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String toString(final InputStream is) {
        if (null != is) {
            try {
                final Reader reader = new InputStreamReader(is);
                return FileUtils.copyToString(reader);
            } catch (IOException ex) {
                //-- Error reading stream --//
            }
        }
        return null;
    }

    public static String toString(final char c) {
        return new String(new char[]{c});
    }

    public static String toString(final Character c) {
        return null != c ? c.toString() : null;
    }

    /*
     * Return allways a string, even if passed value is null. <p><pre>
     * StringUtils.toString(null) = "" StringUtils.toString("") = ""
     * StringUtils.toString(" ") = " " StringUtils.toString("Hello") = "Hello"
     * </pre> @param obj Object @return a string.
     */
    public static String toString(final Object obj) {
        if (null == obj) {
            return "";
        } else if (obj instanceof Object[]) {
            return toString((Object[]) obj, ",");
        } else {
            return obj.toString();
        }
    }

    /*
     * Return allways a string, even if passed value is null. If passed value is
     * null, the default value is returned. <p><pre> StringUtils.toString(null)
     * = "" StringUtils.toString("") = "" StringUtils.toString(" ") = " "
     * StringUtils.toString("Hello") = "Hello" </pre> @param obj Object @param
     * defaultValue the default value to return if passed value is null. @return
     * a string.
     */
    public static String toString(final Object obj, final String defaultValue) {
        if (null == obj) {
            return defaultValue;
        } else {
            return toString(obj);
        }
    }

    /*
     * Return allways a string, even if passed value is null.<br> If passed
     * value is null, the default value is returned.<br> If "zerolength" is
     * false and passed value is empty string, the default value is returned.
     * <p><pre> StringUtils.toString(null) = "" StringUtils.toString("") = ""
     * StringUtils.toString(" ") = " " StringUtils.toString("Hello") = "Hello"
     * </pre> @param obj Object @param defaultValue the default value to return
     * if passed value is null. @param True/False parameter to indicate if
     * "zero-lenght" values are allowed. If False, the default value is
     * returned. @return a string.
     */
    public static String toString(final Object obj,
                                  final String defaultValue, boolean zerolength) {
        if (null == obj) {

            return defaultValue;
        } else if (obj.toString().length() == 0) {
            return zerolength ? "" : defaultValue;
        } else {
            return obj.toString();
        }
    }

    public static String toString(final byte[] arr, final String charset) {
        try {
            return new String(arr, charset);
        } catch (Throwable ignore) {
        }
        return new String(arr);
    }

    public static String toString(final byte[] arr) {
        return toString(arr, CharEncoding.getDefault());
    }

    public static String toString(final Object[] array,
                                  final String separator) {
        return toString(array, separator, null);
    }

    public static String toString(final Object[] array,
                                  final String separator, final String defaultValue) {
        if (null == array) {
            return defaultValue;
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(toString(array[i]));
            }
            return result.toString();
        }
    }

    /**
     * @param array        Array of objects to convert into String
     * @param separator    Separator for String
     * @param defaultValue Default value if array is null or empty
     * @param maxOutputLen Max length of output string. Default is -1.
     * @return Concatenating string of array items.
     */
    public static String toString(final Object[] array,
                                  final String separator, final String defaultValue,
                                  final int maxOutputLen) {
        if (null == array) {
            return defaultValue;
        } else {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (result.length() > 0) {
                    result.append(separator);
                }
                final String value = toString(array[i]);
                if (maxOutputLen > 0) {
                    if (value.length() + separator.length() + result.length() > maxOutputLen) {
                        break;
                    }
                }
                result.append(value);
            }
            return result.toString();
        }
    }

    public static String toQueryString(final Map<String, ?> params) {
        return toQueryString(params, "&");
    }

    public static String toQueryString(final Map<String, ?> params,
                                final String separator) {
        final String sep = StringUtils.hasText(separator) ? separator : "&";
        if (!CollectionUtils.isEmpty(params)) {
            final StringBuilder result = new StringBuilder();
            final Set<String> keys = params.keySet();
            for (final String key : keys) {
                final String value = StringUtils.notNull(params.get(key), "");
                if (StringUtils.hasText(value)) {
                    StringUtils.append(key.concat("=").concat(encode(value)),
                            result, sep);
                }
            }
            return result.toString();
        }
        return "";
    }


    public static InputStream toInputStream(final String text) {
        return toInputStream(CharEncoding.getDefault(), text);
    }

    public static InputStream toInputStream(final byte[] bytes) {
        final String text = StringUtils.toString(bytes);
        return toInputStream(CharEncoding.getDefault(), text);
    }

    public static InputStream toInputStream(final String encoding,
                                            final String text) {
        try {
            final ByteArrayInputStream result = new ByteArrayInputStream(
                    text.getBytes(encoding));
            return result;
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * Check if a String has length. <p><pre>
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the String to check, may be
     *            <code>null</code>
     * @return <code>true</code> if the String is not null and has length
     */
    public static boolean hasLength(final String str) {
        return hasLength(str, 1);
    }

    public static boolean hasLength(final String str, final int minLenght) {
        return (str != null && str.length() > minLenght - 1);
    }

    /**
     * Check if a String has text. More specifically, returns
     * <code>true</code> if the string not
     * <code>null<code>, it's
     * <code>length is > 0</code>, and it has at least one non-whitespace
     * character. <p><pre>
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
     * @param str the String to check, may be
     *            <code>null</code>
     * @return <code>true</code> if the String is not null, length > 0, and not
     *         whitespace only
     * @see java.lang.Character#isWhitespace
     */
    public static boolean hasText(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate a JSON string
     *
     * @param value String
     * @return true if value is JSON string
     */
    public static boolean isJSON(final Object value) {
        return isJSONObject(value) || isJSONArray(value);
    }

    public static boolean isJSONObject(final Object value) {
        return null != value && (value.toString().startsWith("{") && value.toString().endsWith("}"));
    }

    public static boolean isJSONArray(final Object value) {
        return null != value && (value.toString().startsWith("[") && value.toString().endsWith("]"));
    }



    /**
     * Return true if passed value is empty string or null string or a string
     * containig "NULL" text.
     *
     * @param value Value to check
     * @return Boolean value.
     */
    public static boolean isNULL(final Object value) {
        if (value instanceof String) {
            final String svalue = (String) value;
            return !StringUtils.hasText(svalue)
                    || svalue.equalsIgnoreCase(IConstants.NULL);
        }
        return null == value;
    }

    /**
     * Return true iv passed value is not null and equals "NULL" text.<br> It's
     * a shortcut to 'IBeeConstants.NULL.equalsIgnoreCase(value)'.
     *
     * @param value Value to check
     * @return Boolean value
     */
    public static boolean equalsNULL(final String value) {
        return null != value
                ? value.equalsIgnoreCase(IConstants.NULL)
                : false;
    }

    public static String trim(final String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (!hasText(str)) {
            return null;
        }
        return str;
    }

    /**
     * Return a not null value. If passed parameter is null, an empty string is
     * returned.
     *
     * @param str String
     * @return Never null value.
     */
    public static String notNull(final Object str) {
        if (str == null) {
            return "";
        }
        return str.toString();
    }

    /**
     * Return a not null value. If passed parameter is null, default value is
     * returned.<br> You cann use also ObjectUtils.notNull method.
     *
     * @param str String
     * @param def String - the default value to return if passed string is null
     * @return Never null value.
     */
    public static String notNull(final Object str, final String def) {
        if (str == null) {
            return def;
        }
        return str.toString();
    }

    /**
     * Return a not empty string. If parameter is null or empty string, "NULL"
     * is returned.
     *
     * @param str String
     * @return
     */
    public static String notEmpty(final Object str) {
        return notEmpty(str, IConstants.NULL);
    }

    /**
     * Return a not enpty value. If passed parameter is null or empty string,
     * default value is returned.
     *
     * @param str
     * @param def
     * @return
     */
    public static String notEmpty(final Object str, final String def) {
        if (str == null) {
            return def;
        }
        return hasText(str.toString())
                ? str.toString()
                : def;
    }

    /**
     * Test if the given String starts with the specified prefix, ignoring
     * upper/lower case.
     *
     * @param str    the String to check
     * @param prefix the prefix to look for
     * @see java.lang.String#startsWith
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }

    /**
     * Test if the given String ends with the specified suffix, ignoring
     * upper/lower case.
     *
     * @param str    the String to check
     * @param suffix the suffix to look for
     * @see java.lang.String#endsWith
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String lcStr = str.toLowerCase();
        String lcSuffix = suffix.toLowerCase();
        return lcStr.endsWith(lcSuffix);
    }

    /**
     * Count the occurrences of the substring in string s.
     *
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     */
    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0, pos = 0, idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }

    public static String fillString(String s, String fillChar, int size) {
        final StringBuilder result = new StringBuilder(substring(s, 0, size));
        int len = result.length();
        if (len < size) {
            int diff = size - len;
            for (int i = 0; i < diff; i++) {
                result.insert(0, fillChar);
            }
        }

        return result.toString();
    }

    /**
     * Extract a substring from a source string with max 'charCount' char.<br>
     * Ex: String s = substring("Hello World.", 0, 2);<br> s equals 'He'.<br>
     * Ex: String s = substring("Hello World.", 2, 2);<br> s equals 'll'.<br>
     * Ex: String s = substring("Hello World.", 2, 100);<br> s equals 'llo
     * World.'.<br> This method doesn't throw exception if char count is major
     * then string lenght.
     *
     * @param s         Source string
     * @param start     Start index (base 0)
     * @param charCount Number of chars to include in substring (ex: if need a
     *                  substring of max 10 chars, charCount value will be 10)
     * @return Substring
     */
    public static String substring(String s, int start, int charCount) {
        if (null == s) {
            return "";
        }
        int len = s.length();
        int toIndex = start + charCount;
        if (toIndex > len) {
            toIndex = len;
        }
        return s.substring(start, toIndex);
    }

    /**
     * Extract a substring from a source text starting from position of
     * 'matcher' string.<br> i.e. substring("prefixHello world!", "prefix")
     * returns "Hello world!".
     *
     * @param text    Original text. i.e. "prefixHello world!"
     * @param matcher String to serach inside original text. i.e. "prefix"
     * @return The subtring or the original text if no matcher was found in
     *         original text. i.e. "Hello world!"
     */
    public static String substring(final String text,
                                   final String matcher) {
        if (StringUtils.hasText(text) && StringUtils.hasText(matcher)) {
            final int i = text.indexOf(matcher);
            if (i >= 0) {
                return text.substring(i + matcher.length());
            }
        }
        return text;
    }

    /**
     * @param text   Text to parse. i.e. ".class{font=arial; size=12px;}"
     * @param prefix Prefix. i.e. ".class{"
     * @param suffix Suffix. i.e. "}"
     * @return Content between prefix and suffix. i.e. "font=arial; size=12px;"
     */
    public static String substring(final String text,
                                   final String prefix, final String suffix) {
        if (StringUtils.hasText(text) && StringUtils.hasText(prefix)) {
            final int startIndex = text.indexOf(prefix);
            final int endIndex = text.indexOf(suffix, startIndex);
            if (startIndex >= 0) {
                return text.substring(startIndex + prefix.length(), endIndex);
            }
        }
        return text;
    }

    /**
     * Return a substring starting from left side.
     *
     * @param text     Original text
     * @param numChars Number of characters to return
     * @return A substring starting from left side.
     */
    public static String leftStr(final String text,
                                 final int numChars) {
        return leftStr(text, numChars, false);
    }

    /**
     * Return a substring starting from left side.
     *
     * @param text     Original text
     * @param numChars Number of characters to return
     * @param addDots  add dots at end of result. i.e. "result..."
     * @return A substring starting from left side.
     */
    public static String leftStr(final Object text,
                                 final int numChars, final boolean addDots) {
        if (null != text) {
            final String stext = text.toString();
            final int len = StringUtils.hasText(stext)
                    ? stext.length()
                    : 0;
            if (len > numChars) {
                final String result = stext.substring(0, numChars);
                return addDots
                        ? result.concat("...")
                        : result;
            }
            return stext;
        }
        return "";
    }

    /**
     * Return a substring starting from right side.
     *
     * @param text     Original text
     * @param numChars Number of characters to return
     * @return A substring starting from right side.
     */
    public static String rightStr(final String text, final int numChars) {
        return rightStr(text, numChars, false);
    }

    /**
     * Return a substring starting from right side.
     *
     * @param text     Original text
     * @param numChars Number of characters to return
     *                 * @param addDots add dots at begin of result. i.e. "...result"
     * @return A substring starting from right side.
     */
    public static String rightStr(final String text,
                                  final int numChars, final boolean addDots) {
        final int len = StringUtils.hasText(text) ? text.length() : 0;
        if (len > numChars) {
            final int startIndex = len - numChars;
            final String result = text.substring(startIndex, startIndex + numChars);
            return addDots
                    ? "...".concat(result)
                    : result;
        }
        return null != text ? text : "";
    }

    /**
     * Replace all occurences of a substring within a string with another
     * string.
     *
     * @param inString    Original String. i.e. "hello item1 item2"
     * @param oldPatterns Array of substrings to replace. i.e. {"item1",
     *                    "item2"}
     * @param newPattern  String to insert. i.e. "angelo"
     * @return a String with the replacements. i.e. "hello angelo angelo"
     */
    public static String replace(final String inString,
                                 final String[] oldPatterns, final String newPattern) {
        String result = inString;
        for (final String oldPattern : oldPatterns) {
            result = StringUtils.replace(result, oldPattern, newPattern);
        }
        return result;
    }

    /**
     * Replace all occurences of a substring within a string with another
     * string.
     *
     * @param inString   String to examine
     * @param oldPattern String to replace
     * @param newPattern String to insert
     * @return a String with the replacements
     */
    public static String replace(final String inString,
                                 final String oldPattern, final String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        final StringBuilder sbuf = new StringBuilder();
        // output StringBuffer we'll build up
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));

        // remember to append any characters to the right of a match
        return sbuf.toString();
    }

    /**
     * Replace all duplicates characters with a single character.<br> i.e. :
     * <code>"c:\\folder\\file" -> "c:\folder\file"</code><br> i.e. :
     * <code>"it------IT" -> "it-IT"</code><br>
     *
     * @param text   input string
     * @param symbol character to remove duplicates
     * @return cleaned from duplicates string
     */
    public static String replaceDuplicates(final String text,
                                           final String symbol) {
        String result = text;
        while (result.indexOf(symbol + symbol) > -1) {
            result = result.replace(symbol + symbol, symbol);
        }
        return result;
    }

    /**
     * Replace duplicates with some exclusions.
     *
     * @param text       Text to check for duplicates
     * @param symbol     character to remove duplicates
     * @param exclusions Array of strings. i.e. ["file://", "http://"]
     * @return cleaned from duplicates string
     */
    public static String replaceDuplicates(final String text,
                                           final String symbol, final String[] exclusions) {
        final StringBuilder result = new StringBuilder();
        final String exclusionRegEx = getExclusionRegEx(text, exclusions);
        if (StringUtils.hasText(exclusionRegEx)) {
            final String regex = exclusionRegEx + "[" + symbol + "]";
            final String[] tokens = text.split(regex);
            for (final String token : tokens) {
                if (StringUtils.hasText(token)) {
                    if (token.startsWith(symbol)) {
                        result.append(replaceDuplicates(token, symbol));
                    } else {
                        if (result.length() > 0) {
                            result.append(symbol).append(token);
                        } else {
                            result.append(token);
                        }
                    }
                }
            }
        } else {
            result.append(StringUtils.replaceDuplicates(text, symbol));
        }
        return result.toString();
    }

    /**
     * Delete all occurrences of the given substring.
     *
     * @param pattern the pattern to delete all occurrences of
     * @return
     */
    public static String delete(final String inString,
                                final String pattern) {
        return replace(inString, pattern, "");
    }

    /**
     * Delete all occurrences of the given substrings.
     *
     * @param patterns the patterns to delete all occurrences of
     * @return
     */
    public static String delete(final String inString,
                                final String[] patterns) {
        return replace(inString, patterns, "");
    }

    /**
     * Delete any character in a given string.
     *
     * @param charsToDelete a set of characters to delete. E.g. "az\n" will
     *                      delete 'a's, 'z's and new lines.
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if (inString == null || charsToDelete == null) {
            return inString;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String insert(final String text, final int position, final String target) {
        if (null != text && hasText(target)) {
            final StringBuilder sb = new StringBuilder(target);
            sb.insert(position, text);
            return sb.toString();
        }
        return target;
    }


    //---------------------------------------------------------------------
    // Convenience methods for working with formatted Strings
    //---------------------------------------------------------------------

    /**
     * Quote the given String with single quotes.
     *
     * @param str the input String (e.g. "myString")
     * @return the quoted String (e.g. "'myString'"), or
     *         <code>null<code> if the input was
     *         <code>null</code>
     */
    public static String quote(String str) {
        return (str != null ? "'" + str + "'" : null);
    }

    /**
     * Quote the given String with passed quoteChar.
     *
     * @param str       the input String (e.g. "myString")
     * @param quoteChar Character to use for quote.
     * @return the quoted String (e.g. "'myString'"), or
     *         <code>null<code> if the input was
     *         <code>null</code>
     */
    public static String quote(String str, String quoteChar) {
        return (str != null ? quoteChar + str + quoteChar : null);
    }

    /**
     * Turn the given Object into a String with single quotes if it is a String;
     * keeping the Object as-is else.
     *
     * @param obj the input Object (e.g. "myString")
     * @return the quoted String (e.g. "'myString'"), or the input object as-is
     *         if not a String
     */
    public static Object quoteIfString(Object obj) {
        return (obj instanceof String ? quote((String) obj) : obj);
    }

    /**
     * Unqualify a string qualified by a '.' dot character. For example,
     * "this.name.is.qualified", returns "qualified".
     *
     * @param qualifiedName the qualified name
     */
    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    /**
     * Unqualify a string qualified by a separator character. For example,
     * "this:name:is:qualified" returns "qualified" if using a ':' separator.
     *
     * @param qualifiedName the qualified name
     * @param separator     the separator
     */
    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     * Capitalize a
     * <code>String</code>, changing the first letter to upper case as per {@link Character#toUpperCase(char)}.
     * No other letters are changed.
     *
     * @param str the String to capitalize, may be
     *            <code>null</code>
     * @return the capitalized String,
     *         <code>null</code> if null
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * Uncapitalize a
     * <code>String</code>, changing the first letter to lower case as per {@link Character#toLowerCase(char)}.
     * No other letters are changed.
     *
     * @param str the String to uncapitalize, may be
     *            <code>null</code>
     * @return the uncapitalized String,
     *         <code>null</code> if null
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    public static String toLowerCase(final char c) {
        final String text = new String(new char[]{c});
        return text.toLowerCase();
    }

    public static String toLowerCase(final String text) {
        if (StringUtils.hasText(text)) {
            return text.toLowerCase();
        }
        return "";
    }

    public static String toUpperCase(final String text) {
        if (StringUtils.hasText(text)) {
            return text.toUpperCase();
        }
        return "";
    }

    public static String toUpperCase(final char c) {
        final String text = new String(new char[]{c});
        return text.toUpperCase();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static String changeFirstCharacterCase(final String str,
                                                   final boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        final StringBuilder buf = new StringBuilder(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(str.charAt(0));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }

    private static String getExclusionRegEx(final String checkText,
                                            final String[] controlArray) {
        final List<String> matches = new ArrayList<String>();
        if (null != controlArray && controlArray.length > 0) {
            for (final String item : controlArray) {
                if (checkText.indexOf(item) > -1) {
                    matches.add(item);
                }
            }
        }

        //-- creates regex --//
        final StringBuilder regex = new StringBuilder();
        if (matches.size() > 0) {
            for (final String match : matches) {
                if (regex.length() > 0) {
                    regex.append("|");
                }
                regex.append(match);
            }
            regex.insert(0, "(?<![");
            regex.append("])");
        }

        return regex.toString();
    }

    private static String encode(final String s) {
        try {
            return URLEncoder.encode(s, CharEncoding.getDefault());
        } catch (Exception ignored) {
        }
        return s;
    }


}
