/*
 * VLCMath.java
 *
 *
 */
package org.smartly.packages.velocity.impl.vtools.impl;

import org.smartly.commons.util.MathUtils;
import org.smartly.commons.util.RandomUtils;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tool utility for basic arithmetic operations.
 */
public class Math
        implements IVLCTool {

    public static final String NAME = "math";

    /**
     * Creates a new instance of VLCMath
     */
    public Math() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Sum a list of numbers in different formats (String, integer, BigDecimal, Decimal, etc..)
     *
     * @param numbers List of numbers to sum
     * @return BigDecimal
     */
    public BigDecimal sum(List<Object> numbers) {
        BigDecimal result = BigDecimal.ZERO;
        for (Object number : numbers) {
            BigDecimal value;
            if (null != number) {
                value = this.toNumber(number);
            } else {
                value = BigDecimal.ZERO;
            }

            result = result.add(value);
        }

        return result;
    }

    public BigDecimal sum(final Object... numbers) {
        BigDecimal result = BigDecimal.ZERO;
        for (Object number : numbers) {
            BigDecimal value;
            if (null != number) {
                value = this.toNumber(number);
            } else {
                value = BigDecimal.ZERO;
            }

            result = result.add(value);
        }
        return result;
    }

    public double divide(final Object dividend, final Object divisor) {
        final BigDecimal num1 = this.toNumber(dividend);
        final BigDecimal num2 = this.toNumber(divisor);
        final double result = num1.doubleValue() / num2.doubleValue();

        return result;
    }

    public int divideIntValue(final Object dividend, final Object divisor) {
        final double result = this.divide(dividend, divisor);
        return this.intValue(result);
    }

    public int divideRoundCeil(final Object dividend, final Object divisor) {
        final double result = this.divide(dividend, divisor);
        return this.intValue(this.roundCeil(result));
    }

    public BigDecimal multiply(final Object dividend, final Object divisor) {
        final BigDecimal num1 = this.toNumber(dividend);
        final BigDecimal num2 = this.toNumber(divisor);
        return num1.multiply(num2);
    }

    public Integer randomInt(final int digits) {
        return RandomUtils.getTimeBasedRandomInteger(digits);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p/>
     * <p>Characters will be chosen from the set of alpha-numeric
     * characters as indicated by the arguments.</p>
     *
     * @param digits the length of random string to create
     * @return the random string
     */
    public String randomString(final int digits) {
        return RandomUtils.random(digits, true, true);
    }

    /**
     * <p>Creates a random string whose length is the number of characters
     * specified.</p>
     * <p/>
     * <p>Characters will be chosen from the set of characters whose
     * ASCII value is between <code>32</code> and <code>126</code> (inclusive).</p>
     *
     * @param digits the length of random string to create
     * @return the random string
     */
    public String randomAscii(final int digits) {
        return RandomUtils.randomAscii(digits);
    }

    /**
     * Return a random number with a limit range.
     *
     * @param min
     * @param max
     * @return
     */
    public double random(final double min, final double max) {
        return RandomUtils.rnd(min, max);
    }

    /**
     * Round a number mantainig desired number of decimal digits.<br>
     * i.e. round(1.234, 2) = 1.23<br>
     * i.e. round(1.236, 2) = 1.24
     *
     * @param value
     * @param decimals
     * @return
     */
    public double round(final double value, final int decimals) {
        return MathUtils.round(value, decimals);
    }

    /**
     * Round ceil a number, mantaining desired decimals.
     * i.e. roundCeil(1.121, 2) = 1.13
     *
     * @param value    the value
     * @param decimals number of decimals digits
     * @return rounded value
     */
    public double roundCeil(final double value, final int decimals) {
        return MathUtils.roundCeil(value, decimals);
    }

    /**
     * Round ceil a number, mantaining desired decimals.
     * i.e. roundCeil(1.121, 2) = 1.13
     *
     * @param value    the value
     * @param decimals number of decimals digits
     * @return rounded value
     */
    public double roundCeil(final Object value, final int decimals) {
        final BigDecimal number = this.toNumber(value);
        return this.roundCeil(number.doubleValue(), decimals);
    }

    public double roundCeil(final Object value) {
        final BigDecimal number = this.toNumber(value);
        return this.roundCeil(number.doubleValue(), 0);
    }

    /**
     * Round floor a number, mantaining desired decimals.
     * i.e. roundFloor(1.129, 2) = 1.12
     *
     * @param value    the value
     * @param decimals number of decimals digits
     * @return rounded value
     */
    public double roundFloor(final double value, final int decimals) {
        return MathUtils.roundFloor(value, decimals);
    }

    /**
     * Return integer part of a number
     *
     * @param value
     * @return
     */
    public int intValue(final Object value) {
        final BigDecimal number = this.toNumber(value);
        return number.intValue();
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private BigDecimal toNumber(final Object value) {
        try {
            return new BigDecimal(value.toString());
        } catch (Throwable t) {
        }
        return BigDecimal.ZERO;
    }
}
