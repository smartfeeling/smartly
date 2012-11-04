/*
 * RandomUtils.java
 *
 */
package org.smartly.commons.util;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Static utils class for Randomize
 *
 * @author Angelo Geminiani ( angelo.geminiani@gmail.com )
 */
public abstract class RandomUtils {
    
    public static final char[] CHARS_LOW_NUMBERS = "abcdefghilmnopqrstuvzxywjk0123456789".toCharArray();
    public static final char[] CHARS_NUMBERS = "1234567890abcdefghilmnopqrstuvzxywkjABCDEFGHILMNOPQRSTUVZXYWKJ".toCharArray();
    
    private static final Random _random = new Random();

    /**
     * Return a random number with a limit range.
     *
     * @param min
     * @param max
     * @return
     */
    public static double rnd(double min, double max) {
        return (Math.floor(Math.random() * (max - min + 1)) + min);
    }

    /**
     * This returns a random {@link Number} within the
     * specified range.  The returned value will be
     * greater than or equal to the first number
     * and less than the second number.  If both arguments
     * are whole numbers then the returned number will
     * also be, otherwise a {@link Double} will
     * be returned.
     *
     * @param num1 the first number
     * @param num2 the second number
     * @return a pseudo-random {@link Number} greater than
     *         or equal to the first number and less than
     *         the second
     * @see java.lang.Math#random()
     */
    public static Number rnd(final Object num1, final Object num2) {
        Number n1 = toNumber(num1);
        Number n2 = toNumber(num2);
        if (n1 == null || n2 == null) {
            return null;
        }

        double diff = n2.doubleValue() - n1.doubleValue();
        // multiply the difference by a pseudo-random double from
        // 0.0 to 1.0, round to the nearest int, and add the first
        // value to the random int and return as an Integer
        double random = (diff * java.lang.Math.random()) + n1.doubleValue();

        // check if either of the args were floating points
        String in = n1.toString() + n2.toString();
        if (in.indexOf('.') < 0) {
            // args were whole numbers, so return the same
            return MathUtils.matchType(n1, n2, java.lang.Math.floor(random));
        }
        // one of the args was a floating point,
        // so don't floor the result
        return new Double(random);
    }

    /**
     * Return a random integer (based on current time) of desired lenght.
     * Maximum digits allowed is 9. If digits parameter exceed number of nine,
     * will be setted to nine by default.<br> e.g. if you pass 10 as number of
     * desired digits, result will be of 9 digits.
     *
     * @param digits Maximum 9 digits
     * @return Random number
     */
    public static Integer getTimeBasedRandomInteger(int digits) {
        if (digits > 9) {
            digits = 9;
        }
        Random generator = new Random(System.currentTimeMillis());
        double rnd = Math.pow(10d, digits);
        Integer value = generator.nextInt((int) rnd);
        while (value.toString().length() != digits) {
            if (value.toString().length() < digits) {
                value *= 10;
            } else {
                value /= 10;
            }
        }
        return value;
    }

    /**
     * Return a random integer (based on current time).
     *
     * @return Random integer.
     */
    public static Integer getTimeBasedRandomInteger() {
        Random generator = new Random(System.currentTimeMillis());
        Integer value = generator.nextInt();
        return value;
    }

    /**
     * Return a random long (based on current time). Number can have values from
     * a maximum of 2<sup>63</sup>-1, to a minimum of -2<sup>63</sup>.
     *
     * @return Random long.
     */
    public static Long getTimeBasedRandomLong() {
        Random generator = new Random(System.currentTimeMillis());
        Long value = generator.nextLong();
        return value;
    }

    /**
     * Return a random integer (based on current time). If flag ordinal is True,
     * only numbers grater than zero will be returned. Number can have values
     * from a maximum of 2<sup>63</sup>-1, to a minimum of 0.
     *
     * @param ordinal if true, will be returned a number greated than zero.
     * @return Random integer.
     */
    public static Long getTimeBasedRandomLong(boolean ordinal) {
        Random generator = new Random(System.currentTimeMillis());
        Long value = generator.nextLong();
        if (value < 0 && ordinal) {
            value *= -1;
        }
        return value;
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of all characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String random(int count) {
        return random(count, false, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters whose ASCII value
     * is between
     * <code>32</code> and
     * <code>126</code> (inclusive).</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alphabetic characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of numeric characters.</p>
     *
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomNumeric(int count) {
        return random(count, false, true);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alpha-numeric characters as
     * indicated by the arguments.</p>
     *
     * @param count the length of random string to create
     * @param letters if
     * <code>true</code>, generated string will include alphabetic characters
     * @param numbers if
     * <code>true</code>, generated string will include numeric characters
     * @return the random string
     */
    public static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of alpha-numeric characters as
     * indicated by the arguments.</p>
     *
     * @param count the length of random string to create
     * @param start the position in set of chars to start at
     * @param end the position in set of chars to end before
     * @param letters if
     * <code>true</code>, generated string will include alphabetic characters
     * @param numbers if
     * <code>true</code>, generated string will include numeric characters
     * @return the random string
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, _random);
    }

    /**
     * <p>Creates a random string based on a variety of options, using default
     * source of randomness.</p>
     *
     * <p>This method has exactly the same semantics as
     * {@link #random(int,int,int,boolean,boolean,char[], java.util.Random)}, but instead
     * of using an externally supplied source of randomness, it uses the
     * internal static {@link java.util.Random} instance.</p>
     *
     * @param count the length of random string to create
     * @param start the position in set of chars to start at
     * @param end the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars the set of chars to choose randoms from. If
     * <code>null</code>, then it will use the set of all chars.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     * <code>(end - start) + 1</code> characters in the set array.
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars) {
        return random(count, start, end, letters, numbers, chars, _random);
    }

    /**
     * <p>Creates a random string based on a variety of options, using supplied
     * source of randomness.</p>
     *
     * <p>If start and end are both
     * <code>0</code>, start and end are set to
     * <code>' '</code> and
     * <code>'z'</code>, the ASCII printable characters, will be used, unless
     * letters and numbers are both
     * <code>false</code>, in which case, start and end are set to
     * <code>0</code> and
     * <code>Integer.MAX_VALUE</code>.
     *
     * <p>If set is not
     * <code>null</code>, characters between start and end are chosen.</p>
     *
     * <p>This method accepts a user-supplied {@link java.util.Random} instance to use as
     * a source of randomness. By seeding a single
     * {@link java.util.Random} instance with a fixed seed and using it for each call, the
     * same random sequence of strings can be generated repeatedly and
     * predictably.</p>
     *
     * @param count the length of random string to create
     * @param start the position in set of chars to start at
     * @param end the position in set of chars to end before
     * @param letters only allow letters?
     * @param numbers only allow numbers?
     * @param chars the set of chars to choose randoms from. If
     * <code>null</code>, then it will use the set of all chars.
     * @param random a source of randomness.
     * @return the random string
     * @throws ArrayIndexOutOfBoundsException if there are not
     * <code>(end - start) + 1</code> characters in the set array.
     * @throws IllegalArgumentException if
     * <code>count</code> &lt; 0.
     * @since 2.0
     */
    public static String random(int count, int start, int end, boolean letters, boolean numbers,
            char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length "
                    + count + " is less than 0.");
        }
        if ((start == 0) && (end == 0)) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        final StringBuilder buffer = new StringBuilder();
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (isValid(ch, letters, numbers)) {
                buffer.append(ch);
            } else {
                count++;
            }
        }
        return buffer.toString();
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters specified.</p>
     *
     * @param count the length of random string to create
     * @param chars the String containing the set of characters to use, may be
     * null
     * @return the random string
     * @throws IllegalArgumentException if
     * <code>count</code> &lt; 0.
     */
    public static String random(int count, final String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, _random);
        }
        return random(count, chars.toCharArray());
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     *
     * <p>Characters will be chosen from the set of characters specified.</p>
     *
     * @param count the length of random string to create
     * @param chars the character array containing the set of characters to use,
     * may be null
     * @return the random string
     * @throws IllegalArgumentException if
     * <code>count</code> &lt; 0.
     */
    public static String random(int count, char[] chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, _random);
        }
        return random(count, 0, chars.length, false, false, chars, _random);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static boolean isValid(char ch, boolean letters, boolean numbers) {
        if ((letters && !Character.isDigit(ch))
                || (numbers && Character.isDigit(ch))
                || (!letters && !numbers)) {
            return StringUtils.hasText(ch + "");
        }
        return false;
    }

    private static BigDecimal toNumber(final Object value) {
        try {
            return new BigDecimal(value.toString());
        } catch (Throwable ignored) {
        }
        return BigDecimal.ZERO;
    }


}
