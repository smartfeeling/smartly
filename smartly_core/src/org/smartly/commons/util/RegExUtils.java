/*
 * RegExUtils.java
 *
 */
package org.smartly.commons.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author
 */
public abstract class RegExUtils {
    //public static final String EMAIL_PATTERN = ".+@.+.[a-z]+\\.+.[a-z]";

    public static final String ALPHANUMERIC = "([0-9a-zA-Z])";
    public static final String ALPHANUMERIC_EXT = "([0-9a-zA-Z ,'\"()])";
    public static final String NOTNUMERIC = "[^0-9]+";
    public static final String VELOCITY_VARIABLES = "(\\$|\\$\\{)(?:[a-zA-Z\\.\\('\\)\\{\\}]+)?(?=(\\s|))";
    public static final String IP_ADDRESS = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";
    public static final String DOMAIN_NAME = "^[a-zA-Z]([a-zA-Z0-9-][a-zA-Z0-9])?\\.[a-zA-Z]([a-zA-Z0-9-][a-zA-Z0-9])?(\\.[a-zA-Z]([a-zA-Z0-9-][a-zA-Z0-9])?)?$";
    public static final String EMAIL_PATTERN = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@(([0-9a-zA-Z])+([-\\w]*[0-9a-zA-Z])*\\.)+[a-zA-Z]{2,9})$";
    /**
     * Matches text included in curly brakets '{' '}'.
     */
    public static final String CURLY_BRAKETS = "\\{(?:[^\\}]+)?\\}";
    /**
     * Matches text included in curly brakets '{' '}', but not preceded from a dollar symbol '$'
     */
    public static final String NODOLLAR_CURLY_BRAKETS = "(?<!\\$)" + CURLY_BRAKETS;
    /**
     * Matches text included in curly brakets '{' '}', only if preceded from a dollar symbol '$'
     */
    public static final String DOLLAR_CURLY_BRAKETS = "(?<=\\$)" + CURLY_BRAKETS;
    public static final Character[] SPECIALS = new Character[]{
            '{', '}', '[', ']', '(', ')', '-', '+', '?', '$', '!', '.', '*'
    };
    /**
     * A newline (line feed) character ('\n'),
     * A carriage-return character followed immediately by a newline character ("\r\n"),
     * A standalone carriage-return character ('\r'),
     * A next-line character ('\u0085'),
     * A line-separator character ('\u2028'), or
     * A paragraph-separator character ('\u2029).
     */
    public static final String[] LINETERMINATORS = new String[]{
            "\n", "\r\n", "\r", "\u0085", "\u2028", "\u2029"
    };

    public static String replaceLineTerminators(final String string) {
        if (StringUtils.hasText(string)) {
            String result = string;
            for (final String lt : LINETERMINATORS) {
                result = result.replaceAll(lt, "");
            }
            return result;
        }
        return "";
    }

    public static String replaceNoAlphanumericChar(final String string) {
        final String[] matches = getMatches(string, ALPHANUMERIC);
        return null != matches && matches.length > 0
                ? StringUtils.toString(matches, "")
                : "";
    }

    public static String preserve(final String regexp,
                                  final String string) {
        final String[] matches = getMatches(string, regexp);
        return null != matches && matches.length > 0
                ? StringUtils.toString(matches, "")
                : "";
    }

    public static boolean match(String text, String pattern) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        Pattern emailPattern = Pattern.compile(pattern);
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.find();
    }

    public static boolean isValidIntNumber(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        final Pattern emailPattern = Pattern.compile(NOTNUMERIC);
        final Matcher emailMatcher = emailPattern.matcher(value);
        return !emailMatcher.find();
    }

    public static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.find();
    }

    public static boolean isValidSocialSecurityNumber(final String value) {
        int i, s, c;
        String cf2;
        int setdisp[] = {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 2, 4, 18, 20,
                11, 3, 6, 8, 12, 14, 16, 10, 22, 25, 24, 23};
        try {
            if (value.length() == 0) {
                throw new Exception("No data to check");
            }
            if (value.length() != 16) {
                String sMsg = "wrongSize";
                throw new Exception(sMsg);
            }
            cf2 = value.toUpperCase();
            for (i = 0; i < 16; i++) {
                c = cf2.charAt(i);
                if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'Z')) {
                    String sMsg = "wrongChar";
                    throw new Exception(sMsg);
                }
            }
            s = 0;
            for (i = 1; i <= 13; i += 2) {
                c = cf2.charAt(i);
                if (c >= '0' && c <= '9') {
                    s = s + c - '0';
                } else {
                    s = s + c - 'A';
                }
            }
            for (i = 0; i <= 14; i += 2) {
                c = cf2.charAt(i);
                if (c >= '0' && c <= '9') {
                    c = c - '0' + 'A';
                }
                s = s + setdisp[c - 'A'];
            }
            if (s % 26 + 'A' != cf2.charAt(15)) {
                String sMsg = "wrongChecksum";
                throw new Exception(sMsg);
            }
            return true;
        } catch (Throwable t) {
        }
        return false;
    }

    public static boolean isValidVatNumber(final String value) {
        int i, c, s;
        if (StringUtils.hasText(value)) {
            try {

                if (value.length() != 11) {
                    String sMsg = "wrongSize";
                    throw new Exception(sMsg);
                }
                for (i = 0; i < 11; i++) {
                    if (value.charAt(i) < '0' || value.charAt(i) > '9') {
                        String sMsg = "wrongChar";
                        throw new Exception(sMsg);
                    }
                }
                s = 0;
                for (i = 0; i <= 9; i += 2) {
                    s += value.charAt(i) - '0';
                }
                for (i = 1; i <= 9; i += 2) {
                    c = 2 * (value.charAt(i) - '0');
                    if (c > 9) {
                        c = c - 9;
                    }
                    s += c;
                }
                if ((10 - s % 10) % 10 != value.charAt(10) - '0') {
                    String sMsg = "wrongChecksum";
                    throw new Exception(sMsg);
                }
                return true;

            } catch (Throwable t) {
            }
        }
        return false;
    }

    public static String[] split(final String text,
                                 final String splitter) {
        return text.split(escape(splitter));
    }

    public static String[] splitLitteral(final String text, final String regex) {
        return text.split(regex);
    }

    /**
     * Return an array of matched groups.
     *
     * @param text  Text to parse. i.e. "this is {hello} text {world}!"
     * @param regex The regular expression. i.e "\{(?:[^\}]+)?\}"
     * @return Array of matched groups. i.e. ["{hello}", "{world}"]
     */
    public static String[] getMatches(final String text, final String regex) {
        final List<String> list = new LinkedList<String>();
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(text);
        boolean result = m.find();
        // Loop through and add mathes
        while (result) {
            list.add(m.group());
            result = m.find();
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Fill passed lists with 'all tokens', 'unmatched tokens', 'matched tokens'.
     *
     * @param text          Text to splitLitteral in tokens. i.e. "this is {hello} text {world}!"
     * @param regex         The regular expression. i.e "\{(?:[^\}]+)?\}"
     * @param tokensList    List to fill with all tokens. i.e. ["this is ", "{hello}", " text ", "{world}", "!"]
     * @param unmatchedList List to fill with unmatched tokens. i.e. ["this is ", " text ", "!"]
     * @param matchedList   List to fill with matches. i.e. ["{hello}", "{world}"]
     */
    public static void tokenize(final String text, final String regex,
                                final List<String> tokensList, final List<String> unmatchedList,
                                final List<String> matchedList) {
        if (null == text) {
            return;
        }
        if (!StringUtils.hasText(regex)) {
            addToList(text, tokensList);
            return;
        }

        // retrieve matches and splitLitteral text
        final String[] splits = text.split(regex);
        final String[] matches = getMatches(text, regex);
        final int splitLen = splits.length;
        final int matchLen = matches.length;
        final int diff = matchLen - splitLen;

        // fill lists
        fillList(unmatchedList, splits);
        fillList(matchedList, matches);

        if (splitLen > 0) {
            for (int i = 0; i < splitLen; i++) {
                final String split = splits[i];
                if (StringUtils.hasLength(split)) {
                    //tokensList.add(splitLitteral);
                    addToList(split, tokensList);
                }
                if (matchLen > i) {
                    //tokensList.add(matches[i]);
                    addToList(matches[i], tokensList);
                }
            }
            // have more matches
            if (diff > 0) {
                for (int i = splitLen; i < matchLen; i++) {
                    //tokensList.add(matches[i]);
                    addToList(matches[i], tokensList);
                }
            }
        } else {
            for (int i = 0; i < matches.length; i++) {
                //tokensList.add(matches[i]);
                addToList(matches[i], tokensList);
            }
        }
    }

    /**
     * Return an array of strings containig both matched and not matched text.<br>
     * NULL is never returned. If text is null, is returned an empty array.
     *
     * @param text  Text to splitLitteral in tokens. i.e. "this is {hello} text {world}!"
     * @param regex The regular expression. i.e "\{(?:[^\}]+)?\}"
     * @return Array of String. i.e. ["this is ", "{hello}", " text ", "{world}", "!"]
     */
    public static String[] tokenize(final String text, final String regex) {
        final List<String> tokensList = new LinkedList<String>();
        final List<String> unmatchedList = new LinkedList<String>();
        final List<String> matchedList = new LinkedList<String>();
        tokenize(text, regex, tokensList, unmatchedList, matchedList);
        if (tokensList.isEmpty()) {
            return new String[0];
        }
        if (!StringUtils.hasText(regex)) {
            return new String[]{text};
        }

        return tokensList.toArray(new String[tokensList.size()]);
    }

    /**
     * Add escape to each character of string, if character is a special character.
     * i.e. "$" become "\$"
     *
     * @param string A String to check for escape
     * @return character with escape if need.
     */
    public static String escape(final String string) {
        return RegExUtils.escape(string, SPECIALS);
    }

    /**
     * Add escape to each character of string, if character is a special character.
     * i.e. "$" become "\$"
     *
     * @param string A String to check for escape
     * @return character with escape if need.
     */
    public static String escape(final String string,
                                final Character[] special) {
        final StringBuffer result = new StringBuffer();
        final char[] chars = string.toCharArray();
        for (final char c : chars) {
            final Character item = new Character(c);
            if (CollectionUtils.contains(special, item)) {
                result.append("\\").append(c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static void fillList(final List<String> list, final String[] array) {
        if (null != list && null != array && array.length > 0) {
            for (final String item : array) {
                list.add(item);
            }
        }
    }

    private static void addToList(final String item, final List<String> list) {
        if (null != item && null != list) {
            list.add(item);
        }
    }


}
