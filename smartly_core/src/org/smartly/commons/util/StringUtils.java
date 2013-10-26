package org.smartly.commons.util;

import org.smartly.IConstants;
import org.smartly.Smartly;
import org.smartly.commons.lang.CharEncoding;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;


public final class StringUtils {

    /**
     * <code>\u000a</code> linefeed LF ('\n').
     *
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#101089">JLF: Escape Sequences
     *      for Character and String Literals</a>
     */
    public static final char LF = '\n';

    /**
     * <code>\u000d</code> carriage return CR ('\r').
     *
     * @see <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#101089">JLF: Escape Sequences
     *      for Character and String Literals</a>
     */
    public static final char CR = '\r';


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
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will
     * still be considered as single delimiter string, rather than as bunch of
     * potential delimiter characters - in contrast to
     * <code>tokenizeToStringArray</code>.
     *
     * @param str   the input String
     * @param delim the delimiter between elements (this is a single
     *              delimiter, rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     */
    public static String[] split(final String str, final String delim) {
        return split(str, delim, true);
    }

    /**
     * Split a string into an array of strings.
     *
     * @param str   the string to split
     * @param delim the delimiter to split the string at
     * @return the string split into a string array
     */
    public static String[] split(final String str, final String delim, final boolean trim) {
        if (str == null) {
            return new String[0];
        }
        final StringTokenizer st = new StringTokenizer(str, delim);
        final String[] s = new String[st.countTokens()];
        for (int i = 0; i < s.length; i++) {
            s[i] = trim ? st.nextToken().trim() : st.nextToken();
        }
        return s;
    }

    /**
     * @param str
     * @param delimiters Array of valid delimiters. i.e. {". ", "; "} (both
     *                   delimiters are valid)
     * @return
     */
    public static String[] split(final String str,
                                 final String[] delimiters) {
        return split(str, delimiters, true, true, true);
    }

    public static String[] split(final String str,
                                 final String[] delimiters,
                                 final boolean trimTokens,
                                 final boolean ignoreEmptyTokens,
                                 final boolean unique) {
        final Collection<String> result = unique ? new LinkedHashSet<String>() : new LinkedList<String>();
        if (null != delimiters && delimiters.length > 0) {
            for (final String delimiter : delimiters) {
                String[] tokens = StringUtils.split(str, delimiter);
                if (null != tokens && tokens.length > 0) {
                    for (final String token : tokens) {
                        final String value = trimTokens ? trim(token) : token;
                        if (ignoreEmptyTokens) {
                            if (StringUtils.hasText(value)) {
                                result.add(value);
                            }
                            result.add(value);
                        } else {
                            result.add(value);
                        }
                    }
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Tokenize the given String into a String array via a StringTokenizer.
     * <p>The given delimiters string is supposed to consist of any number of
     * delimiter characters. Each of those characters can be used to separate
     * tokens. A delimiter is always a single character; for multi-character
     * delimiters, consider using
     * <code>delimitedListToStringArray</code>
     *
     * @param str               the String to tokenize
     * @param delimiters        the delimiter characters, assembled as String (each of
     *                          those characters is individually considered as delimiter)
     * @param trimTokens        trim the tokens via String's
     *                          <code>trim</code>
     * @param ignoreEmptyTokens omit empty tokens from the result array (only
     *                          applies to tokens that are empty after trimming; StringTokenizer will not
     *                          consider subsequent delimiters as token in the first place).
     * @return an array of the tokens
     * @see java.util.StringTokenizer
     * @see String#trim
     */
    public static String[] split(final String str,
                                 final String delimiters,
                                 final boolean trimTokens,
                                 final boolean ignoreEmptyTokens) {

        final StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    public static String[] split(final String text,
                                 final String[] delims, final boolean trim,
                                 final boolean removeDuplicates,
                                 final int minLenght) {
        return split(text, delims, trim, removeDuplicates, minLenght, null);
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will
     * still be considered as single delimiter string, rather than as bunch of
     * potential delimiter characters - in contrast to
     * <code>tokenizeToStringArray</code>.
     *
     * @param str   the input String
     * @param delim the delimiters between elements (this is a single delimiter,
     *              rather than a bunch individual delimiter characters)
     * @param trim  If true, values are trimmed. @minLenght minimum lenght of
     *              token (useful if you need tokens of min lenght)
     * @return an array of the tokens in the list
     */
    public static String[] split(final String str,
                                 final String delim, final boolean trim,
                                 final boolean removeDuplicates,
                                 final int minLenght) {
        return split(str, new String[]{delim}, trim, removeDuplicates, minLenght);
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will
     * still be considered as single delimiter string, rather than as bunch of
     * potential delimiter characters - in contrast to
     * <code>tokenizeToStringArray</code>.
     *
     * @param text
     * @param delims           Array of delimiters between elements.
     * @param trim             If true, values are trimmed.
     * @param removeDuplicates Boolean. True if you want only unique values.
     * @param excludes         Optional (default = null). Array of keywords to exclude
     * @return an array of the tokens in the list
     * @minLenght minimum lenght of token (useful if you need tokens of min
     * lenght)
     */
    public static String[] split(final String text,
                                 final String[] delims,
                                 final boolean trim,
                                 final boolean removeDuplicates,
                                 final int minLenght,
                                 final String[] excludes) {
        if (text == null) {
            return new String[0];
        }
        if (delims == null || delims.length == 0) {
            return new String[]{trim ? text.trim() : text};
        }

        final List<String> result = new ArrayList<String>();
        // loop for each delimiter in array.
        final int count = delims.length;
        for (int j = 0; j < count; j++) {
            final String delim = delims[j];
            final String str = count > 1 ? StringUtils.replace(text, delims, delim) : text;
            if ("".equals(delim)) {
                for (int i = 0; i < str.length(); i++) {
                    final String value = str.substring(i, i + 1);
                    // ADD
                    CollectionUtils.add(result, value, minLenght, trim, !removeDuplicates, excludes);
                }
            } else {
                int pos = 0;
                int delPos = 0;
                while ((delPos = str.indexOf(delim, pos)) != -1) {
                    final String value = str.substring(pos, delPos);
                    // ADD
                    CollectionUtils.add(result, value, minLenght, trim, !removeDuplicates, excludes);
                    pos = delPos + delim.length();
                }
                if (str.length() > 0 && pos <= str.length()) {
                    // Add rest of String, but not in case of empty input.
                    final String value = str.substring(pos);
                    // ADD
                    CollectionUtils.add(result, value, minLenght, trim, !removeDuplicates, excludes);
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * @param str       String to split
     * @param delimiter
     * @param trim
     * @param minLenght
     * @param maxSize   Maximum length for return Array.
     * @return
     */
    public static String[] split(final String str,
                                 final String delimiter, final boolean trim,
                                 final boolean removeDuplicates,
                                 final int minLenght, final int maxSize) {
        final String[] tokens = split(str, delimiter, trim, removeDuplicates, minLenght);
        if (maxSize > 0 && tokens.length > maxSize) {
            return CollectionUtils.resizeArray(tokens, maxSize);
        }
        return tokens;
    }

    /**
     * Split a String at the first occurrence of the delimiter. Does not include
     * the delimiter in the result.<br> i.e. : "hello.world.wide" ('.' is
     * delimiter) -> {"hello", "world.wide"}
     *
     * @param toSplit   the string to split
     * @param delimiter to split the string up with
     * @return a two element array with index 0 being before the delimiter, and
     *         index 1 being after the delimiter (neither element includes the
     *         delimiter); or
     *         <code>null</code> if the delimiter wasn't found in the given input String
     */
    public static String[] splitFirst(final String toSplit,
                                      final String delimiter) {
        final String[] result = _splitLastOrFirst(toSplit, delimiter, false);
        return null != result ? result : new String[]{toSplit};
    }

    /**
     * Split string into an array of 2 elements.
     *
     * @param toSplit   String to split. i.e. "this.is.a.string"
     * @param delimiter Delimiter. i.e. "."
     * @return Array of 2 elements. i.e. ["this.is.a", "string"]
     */
    public static String[] splitLast(final String toSplit,
                                     final String delimiter) {
        final String[] result = _splitLastOrFirst(toSplit, delimiter, true);
        return null != result ? result : new String[]{toSplit};
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

    /**
     * Split a String at the "count" occurrence of the delimiter. Does not
     * include the delimiter in the result.<br> i.e. : "hello.world.wide" ('.'
     * is delimiter) -> {"hello", "world.wide"}
     *
     * @param toSplit   the string to split
     * @param delimiter to split the string up with
     * @return a two element array with index 0 being before the delimiter, and
     *         index 1 being after the delimiter (neither element includes the
     *         delimiter); or
     *         <code>null</code> if the delimiter wasn't found in the given input String
     */
    public static String[] splitAt(final int count, final String toSplit,
                                   final String delimiter) {
        if (!StringUtils.hasLength(toSplit) || !StringUtils.hasLength(delimiter)) {
            return null;
        }
        int offset = -1;
        for (int i = 0; i < count; i++) {
            offset = toSplit.indexOf(delimiter, offset + 1);
        }
        if (offset < 0) {
            return null;
        }

        final String beforeDelimiter = toSplit.substring(0, offset);
        final String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }

    public static String concatArgs(final Object... args) {
        final StringBuffer result = new StringBuffer();
        if (null != args && args.length > 0) {
            for (final Object arg : args) {
                if (!isNULL(arg)) {
                    result.append(arg);
                }
            }
        }
        return result.toString();
    }

    public static String concatArgsEx(final String separator,
                                      final Object... args) {
        final StringBuffer result = new StringBuffer();
        if (null != args && args.length > 0) {
            for (final Object arg : args) {
                if (!isNULL(arg)) {
                    if (hasText(separator) && result.length() > 0) {
                        result.append(separator);
                    }
                    result.append(arg);
                }
            }
        }
        return result.toString();
    }

    public static String concatDot(final Object... args) {
        return concatArgsEx(".", args);
    }

    public static String concatUnderscore(final Object... args) {
        return concatArgsEx("_", args);
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
    public static boolean contains(final String text,
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
     * <p/>
     * <p>A <code>null</code> String will return <code>false</code>.
     * A <code>null</code> or zero length search array will return <code>false</code>.</p>
     * <p/>
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
     * @param str         the String to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the <code>true</code> if any of the chars are found,
     *         <code>false</code> if no match or null input
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
     * <p/>
     * <p>A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.</p>
     * <p/>
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
     * @param str          the String to check, may be null
     * @param invalidChars an array of invalid chars, may be null
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

    public static boolean hasTextAll(final String... args) {
        if (null != args) {
            for (final String str : args) {
                if (!StringUtils.hasText(str)) {
                    return false;
                }
            }
            return true;
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
        final String string = null != value ? RegExUtils.replaceLineTerminators(value.toString()) : "";
        return string.startsWith("{") && string.endsWith("}");
    }

    public static boolean isJSONArray(final Object value) {
        final String string = null != value ? RegExUtils.replaceLineTerminators(value.toString()) : "";
        return string.startsWith("[") && string.endsWith("]");
    }


    /**
     * Return true if passed value is empty string or null string or a string
     * containig "NULL" text.
     *
     * @param value Value to check
     * @return Boolean value.
     */
    public static boolean isNULL(final Object value) {
        if (null == value) {
            return true;
        }
        final String svalue = value.toString().trim();
        return !StringUtils.hasText(svalue)
                || svalue.equalsIgnoreCase(IConstants.NULL);
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

    /**
     * Trim and remove duplicates \n inside text
     *
     * @param text
     * @return
     */
    public static String trimTokens(final String text) {
        final StringBuilder result = new StringBuilder();
        final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken().trim();
            if (StringUtils.hasText(token)) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(token);
            }
        }
        return result.toString();
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

    /**
     * Returns a left filled string with passed chars.
     *
     * @param s        A String
     * @param fillChar Characters to append
     * @param size     Size of result
     * @return A String of defined size, filled with passed chars. i.e. "0000000123" // 10 characters string filled with "0"
     */
    public static String fillString(final String s, final String fillChar, final int size) {
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
     * Returns a right filled string with passed chars.
     *
     * @param s        A String
     * @param fillChar Characters to append
     * @param size     Size of result
     * @return A String of defined size, filled with passed chars. i.e. "123-------" // 10 characters string filled with "-"
     */
    public static String fillRightString(final String s, final String fillChar, final int size) {
        final StringBuilder result = new StringBuilder(substring(s, 0, size));
        int len = result.length();
        if (len < size) {
            int diff = size - len;
            for (int i = 0; i < diff; i++) {
                result.append(fillChar);
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

    public static String replaceDuplicates(final String text,
                                           final String[] symbols) {
        String result = text;
        for (final String symbol : symbols) {
            while (result.indexOf(symbol + symbol) > -1) {
                result = result.replace(symbol + symbol, symbol);
            }
        }
        return result;
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
                                           final String symbol,
                                           final String[] exclusions) {
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

    public static boolean isURLEncoded(final String value) {
        try {
            return !URLDecoder.decode(value, Smartly.getCharset()).equalsIgnoreCase(value);
        } catch (Exception ignored) {
        }
        return false;
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

    private static String[] _splitLastOrFirst(final String toSplit,
                                              final String delimiter, final boolean last) {
        if (!StringUtils.hasLength(toSplit) || !StringUtils.hasLength(delimiter)) {
            return null;
        }
        int offset = last
                ? toSplit.lastIndexOf(delimiter)
                : toSplit.indexOf(delimiter);
        if (offset < 0) {
            return null;
        }
        final String beforeDelimiter = toSplit.substring(0, offset);
        final String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }


}
