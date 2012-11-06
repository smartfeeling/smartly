/*
 * 
 */
package org.smartly.commons.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * 
 * @author 
 */
public abstract class MathUtils {

    /**
     * @param nums the numbers to be added
     * @return the sum of the numbers or
     *         <code>null</code> if they're invalid
     */
    public static Number add(Object... nums) {
        double value = 0;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = ConversionUtils.toNumber(num);
            if (n == null) {
                return null;
            }
            value += n.doubleValue();
        }
        return matchType(value, ns);
    }

    /**
     * @param nums the numbers to be subtracted
     * @return the difference of the numbers (subtracted in order) or
     *         <code>null</code> if they're invalid
     */
    public static Number sub(Object... nums) {
        double value = 0;
        Number[] ns = new Number[nums.length];
        for (int i = 0; i < nums.length; i++) {
            Number n = ConversionUtils.toNumber(nums[i]);
            if (n == null) {
                return null;
            }
            if (i == 0) {
                value = n.doubleValue();
            } else {
                value -= n.doubleValue();
            }
        }
        return matchType(value, ns);
    }

    /**
     * @param nums the numbers to be multiplied
     * @return the product of the numbers or
     *         <code>null</code> if they're invalid
     */
    public static Number mul(Object... nums) {
        double value = 1;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = ConversionUtils.toNumber(num);
            if (n == null) {
                return null;
            }
            value *= n.doubleValue();
        }
        return matchType(value, ns);
    }

    /**
     * @param nums the numbers to be divided
     * @return the quotient of the numbers or
     *         <code>null</code> if they're invalid
     *         or if any denominator equals zero
     */
    public static Number div(Object... nums) {
        double value = 0;
        Number[] ns = new Number[nums.length];
        for (int i = 0; i < nums.length; i++) {
            Number n = ConversionUtils.toNumber(nums[i]);
            if (n == null) {
                return null;
            }
            if (i == 0) {
                value = n.doubleValue();
            } else {
                double denominator = n.doubleValue();
                if (denominator == 0.0) {
                    return null;
                }
                value /= denominator;
            }
        }
        return matchType(value, ns);
    }

    /**
     * @param num1 the first number
     * @param num2 the second number
     * @return the first number raised to the power of the
     *         second or <code>null</code> if they're invalid
     */
    public static Number pow(Object num1, Object num2) {
        Number n1 = ConversionUtils.toNumber(num1);
        Number n2 = ConversionUtils.toNumber(num2);
        if (n1 == null || n2 == null) {
            return null;
        }
        double value = java.lang.Math.pow(n1.doubleValue(), n2.doubleValue());
        return matchType(n1, n2, value);
    }

    /**
     * Does integer division on the int values of the specified numbers.
     * <p/>
     * <p>So, $math.idiv('5.1',3) will return '1',
     * and $math.idiv(6,'3.9') will return '2'.</p>
     *
     * @param num1 the first number
     * @param num2 the second number
     * @return the result of performing integer division
     *         on the operands.
     */
    public static Integer idiv(Object num1, Object num2) {
        Number n1 = ConversionUtils.toNumber(num1);
        Number n2 = ConversionUtils.toNumber(num2);
        if (n1 == null || n2 == null || n2.intValue() == 0) {
            return null;
        }
        int value = n1.intValue() / n2.intValue();
        return Integer.valueOf(value);
    }

    /**
     * Does integer modulus on the int values of the specified numbers.
     * <p/>
     * <p>So, $math.mod('5.1',3) will return '2',
     * and $math.mod(6,'3.9') will return '0'.</p>
     *
     * @param num1 the first number
     * @param num2 the second number
     * @return the result of performing integer modulus
     *         on the operands.
     */
    public static Integer mod(Object num1, Object num2) {
        Number n1 = ConversionUtils.toNumber(num1);
        Number n2 = ConversionUtils.toNumber(num2);
        if (n1 == null || n2 == null || n2.intValue() == 0) {
            return null;
        }
        int value = n1.intValue() % n2.intValue();
        return Integer.valueOf(value);
    }

    public static int max (final int...numbers) {
        int result = 0;
        if(null!=numbers && numbers.length>0){
            for(final int number:numbers){
                if(number>result){
                    result = number;
                }
            }
        }
        return result;
    }

    /**
     * @param nums the numbers to be searched
     * @return the largest of the numbers or
     *         <code>null</code> if they're invalid
     */
    public static Number max(final Object... nums) {
        double value = Double.MIN_VALUE;
        final Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            final Number n = ConversionUtils.toNumber(num);
            if (n == null) {
                return null;
            }
            value = java.lang.Math.max(value, n.doubleValue());
        }
        return matchType(value, ns);
    }

    /**
     * @param nums the numbers to be searched
     * @return the smallest of the numbers or
     *         <code>null</code> if they're invalid
     */
    public static Number min(Object... nums) {
        double value = Double.MAX_VALUE;
        Number[] ns = new Number[nums.length];
        for (Object num : nums) {
            Number n = ConversionUtils.toNumber(num);
            if (n == null) {
                return null;
            }
            value = java.lang.Math.min(value, n.doubleValue());
        }
        return matchType(value, ns);
    }

    /**
     * @param num the number
     * @return the absolute value of the number or
     *         <code>null</code> if it's invalid
     */
    public static Number abs(Object num) {
        Number n = ConversionUtils.toNumber(num);
        if (n == null) {
            return null;
        }
        double value = java.lang.Math.abs(n.doubleValue());
        return matchType(n, value);
    }

    // --------------------------------------------------------------------
    //               Aggregation methods
    // --------------------------------------------------------------------

    /**
     * Get the sum of the values from a list
     *
     * @param collection A collection containing Java beans
     * @param field      A Java Bean field for the objects in <i>collection</i> that
     *                   will return a number.
     * @return The sum of the values in <i>collection</i>.
     */
    public static Number getTotal(Collection collection, String field) {
        if (collection == null || field == null) {
            return null;
        }
        double result = 0;
        // hold the first number and use it to match return type
        Number first = null;
        try {
            for (Iterator i = collection.iterator(); i.hasNext(); ) {
                Object property = BeanUtils.getValueIfAny(i.next(), field);
                Number value = ConversionUtils.toNumber(property);
                // skip over nulls (i.e. treat them as 0)
                if (value != null) {
                    if (first == null) {
                        first = value;
                    }
                    result += value.doubleValue();
                }
            }
            return matchType(first, result);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the average of the values from a list
     *
     * @param collection A collection containing Java beans
     * @param field      A Java Bean field for the objects in <i>collection</i> that
     *                   will return a number.
     * @return The average of the values in <i>collection</i>.
     */
    public static Number getAverage(Collection collection, String field) {
        Number result = getTotal(collection, field);
        if (result == null) {
            return null;
        }
        double avg = result.doubleValue() / collection.size();
        return matchType(result, avg);
    }

    /**
     * Get the sum of the values from a list
     *
     * @param array An array containing Java beans
     * @param field A Java Bean field for the objects in <i>array</i> that
     *              will return a number.
     * @return The sum of the values in <i>array</i>.
     */
    public static Number getTotal(Object[] array, String field) {
        return getTotal(Arrays.asList(array), field);
    }

    /**
     * Get the sum of the values from a list
     *
     * @param array A collection containing Java beans
     * @param field A Java Bean field for the objects in <i>array</i> that
     *              will return a number.
     * @return The sum of the values in <i>array</i>.
     */
    public static Number getAverage(Object[] array, String field) {
        return getAverage(Arrays.asList(array), field);
    }

    /**
     * Get the sum of the values
     *
     * @param collection A collection containing numeric values
     * @return The sum of the values in <i>collection</i>.
     */
    public static Number getTotal(Collection collection) {
        if (collection == null) {
            return null;
        }

        double result = 0;
        // grab the first number and use it to match return type
        Number first = null;
        for (Iterator i = collection.iterator(); i.hasNext(); ) {
            Number value = ConversionUtils.toNumber(i.next());
            if (value == null) {
                //FIXME? or should we ignore this and keep adding?
                return null;
            }
            if (first == null) {
                first = value;
            }
            result += value.doubleValue();
        }
        return matchType(first, result);
    }

    /**
     * Get the average of the values
     *
     * @param collection A collection containing number values
     * @return The average of the values in <i>collection</i>.
     */
    public static Number getAverage(Collection collection) {
        Number result = getTotal(collection);
        if (result == null) {
            return null;
        }
        double avg = result.doubleValue() / collection.size();
        return matchType(result, avg);
    }

    /**
     * Get the sum of the values
     *
     * @param array An array containing number values
     * @return The sum of the values in <i>array</i>.
     */
    public static Number getTotal(Object... array) {
        return getTotal(Arrays.asList(array));
    }

    /**
     * Get the average of the values
     *
     * @param array An array containing number values
     * @return The sum of the values in <i>array</i>.
     */
    public static Number getAverage(Object... array) {
        return getAverage(Arrays.asList(array));
    }

    /**
     * Get the sum of the values
     *
     * @param values The list of double values to add up.
     * @return The sum of the arrays
     */
    public static Number getTotal(double... values) {
        if (values == null) {
            return null;
        }

        double result = 0;
        for (int i = 0; i < values.length; i++) {
            result += values[i];
        }
        return new Double(result);
    }

    /**
     * Get the average of the values in an array of double values
     *
     * @param values The list of double values
     * @return The average of the array of values
     */
    public static Number getAverage(double... values) {
        Number total = getTotal(values);
        if (total == null) {
            return null;
        }
        return new Double(total.doubleValue() / values.length);
    }

    /**
     * Get the sum of the values
     *
     * @param values The list of long values to add up.
     * @return The sum of the arrays
     */
    public static Number getTotal(long... values) {
        if (values == null) {
            return null;
        }

        long result = 0;
        for (long value : values) {
            result += value;
        }
        return Long.valueOf(result);
    }

    /**
     * Get the average of the values in an array of long values
     *
     * @param values The list of long values
     * @return The average of the array of values
     */
    public static Number getAverage(long... values) {
        Number total = getTotal(values);
        if (total == null) {
            return null;
        }
        double avg = total.doubleValue() / values.length;
        return matchType(total, avg);
    }

    // --------------------------------------------------------------------
    //               R O U N D
    // --------------------------------------------------------------------

    /**
     * @param num the number
     * @return the smallest integer that is not
     *         less than the given number
     */
    public static Integer ceil(Object num) {
        Number n = ConversionUtils.toNumber(num);
        if (n == null) {
            return null;
        }
        return Integer.valueOf((int) java.lang.Math.ceil(n.doubleValue()));
    }

    /**
     * Round a number mantainig desired number of decimal digits.<br>
     * i.e. round(1.234, 2) = 1.23<br>
     * i.e. round(1.236, 2) = 1.24
     * @param value
     * @param decimals
     * @return
     */
    public static double round(double value, int decimals) {
        final double x = decimals == 0 ? 1 : Math.pow(10, decimals);  // 1 or 10^decimals
        return Math.round(value * x) / x;
    }

    /**
     * @param num the number
     * @return the integer portion of the number
     */
    public static Integer floor(Object num) {
        Number n = ConversionUtils.toNumber(num);
        if (n == null) {
            return null;
        }
        return Integer.valueOf((int) java.lang.Math.floor(n.doubleValue()));
    }


    /**
     * Rounds a number to the nearest whole Integer
     *
     * @param num the number to round
     * @return the number rounded to the nearest whole Integer
     *         or <code>null</code> if it's invalid
     * @see java.lang.Math#rint(double)
     */
    public static Integer round(final Object num) {
        Number n = ConversionUtils.toNumber(num);
        if (n == null) {
            return null;
        }
        return Integer.valueOf((int) java.lang.Math.rint(n.doubleValue()));
    }


    /**
     * Rounds a number to the specified number of decimal places.
     * This is particulary useful for simple display formatting.
     * If you want to round an number to the nearest integer, it
     * is better to use {@link #round}, as that will return
     * an {@link Integer} rather than a {@link Double}.
     *
     * @param num      the number to round
     * @param decimals the number of decimal places
     * @return the value rounded to the specified number of
     *         decimal places or <code>null</code> if it's invalid
     */
    public static Double roundTo(final Object num, final Object decimals) {
        final Number i = ConversionUtils.toNumber(decimals);
        final Number d = ConversionUtils.toNumber(num);
        if (i == null || d == null) {
            return null;
        }
        //ok, go ahead and do the rounding
        int places = i.intValue();
        double value = d.doubleValue();
        int delta = 10;
        for (int j = 1; j < places; j++) {
            delta *= 10;
        }
        return new Double((double) java.lang.Math.round(value * delta) / delta);
    }

    /**
     * Round ceil a number, mantaining desired decimals.
     * i.e. roundCeil(1.121, 2) = 1.13
     * @param value the value
     * @param decimals number of decimals digits
     * @return rounded value
     */
    public static double roundCeil(double value, int decimals) {
        final double x = decimals == 0 ? 1 : Math.pow(10, decimals);  // 1 or 10^decimals
        return Math.ceil(value * x) / x;
    }

    /**
     * Round floor a number, mantaining desired decimals.
     * i.e. roundFloor(1.129, 2) = 1.12
     * @param value the value
     * @param decimals number of decimals digits
     * @return rounded value 
     */
    public static double roundFloor(double value, int decimals) {
        final double x = decimals == 0 ? 1 : Math.pow(10, decimals);  // 1 or 10^decimals
        return Math.floor(value * x) / x;
    }
    
    /**
     * Return an array of long values. Length of array is "mod" value.
     * @param value
     * @param mod
     * @param div
     * @return
     */
    public static long[] modArray(double value, int mod, int div){
        final long[] result = new long[mod];
        if(result.length>0){
            result[0]=100;
            if(result.length>1){
                final long base = ((long) (value / (mod*div)))*div;
                final long last = (long) (value-base*(mod-1));
                for(int i=0;i<result.length;i++){
                    if(i==result.length-1){
                       result[i]=last; 
                    } else {
                        result[i]=base;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Calculate number of pages to contain items.<br/>
     * i.e.
     * pageSize=10, items=9, result=1.
     * pageSize=10, items=11, result=2.
     * @param pageSize Max number of items for each page.
     * @param items Number of items to store in pages.
     * @return Number of pages. i.e. pageSize=10, items=9, result=1. pageSize=10, items=11, result=2.
     */
    public static int paging (final int pageSize, final int items){
        int result = 1;
        if(pageSize>0 && pageSize<items){
            result = items/pageSize;
            if(items % pageSize > 0){
                result++;
            }
        }
        return result;
    }

    /**
     * @see #matchType(double, Number...)
     */
    public static Number matchType(Number in, double out) {
        return matchType(out, new Number[]{in});
    }

    /**
     * @see #matchType(double, Number...)
     */
    public static Number matchType(Number in1, Number in2, double out) {
        return matchType(out, new Number[]{in1, in2});
    }

    /**
     * Takes the original argument(s) and returns the resulting value as
     * an instance of the best matching type (Integer, Long, or Double).
     * If either an argument or the result is not an integer (i.e. has no
     * decimal when rendered) the result will be returned as a Double.
     * If not and the result is < -2147483648 or > 2147483647, then a
     * Long will be returned.  Otherwise, an Integer will be returned.
     */
    public static Number matchType(double out, Number... in) {
        //NOTE: if we just checked class types, we could miss custom
        //      extensions of java.lang.Number, and if we only checked
        //      the mathematical value, $math.div('3.0', 1) would render
        //      as '3'.  To get the expected result, we check what we're
        //      concerned about: the rendered string.

        // first check if the result is even a whole number
        boolean isIntegral = (java.lang.Math.rint(out) == out);
        if (isIntegral) {
            for (Number n : in) {
                if (n == null) {
                    break;
                } else if (hasFloatingPoint(n.toString())) {
                    isIntegral = false;
                    break;
                }
            }
        }

        if (!isIntegral) {
            return new Double(out);
        } else if (out > Integer.MAX_VALUE || out < Integer.MIN_VALUE) {
            return Long.valueOf((long) out);
        } else {
            return Integer.valueOf((int) out);
        }
    }

    public static boolean hasFloatingPoint(final String value) {
        return value.indexOf('.') >= 0;
    }
}
