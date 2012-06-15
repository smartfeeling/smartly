/*
 * 
 */
package org.smartly.commons.util;

/**
 * 
 * 
 * @author 
 */
public abstract class MathUtils {
    
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
     * i.e. pageSize=10, items=9, result=1. pageSize=10, items=11, result=2.
     * @param pageSize Max number of items for each page.
     * @param items Number of items to store in pages.
     * @return Number of pages. i.e. pageSize=10, items=9, result=1. pageSize=10, items=11, result=2.
     */
    public static int paging (final int pageSize, final int items){
        int result = 1;
        if(pageSize<items){
            result = items/pageSize;
            if(items % pageSize > 0){
                result++;
            }
        }
        return result;
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
}
