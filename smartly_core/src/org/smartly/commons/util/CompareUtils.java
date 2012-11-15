package org.smartly.commons.util;


public class CompareUtils {

    /**
     * Compare 2 objects. NULL values are greater than not null.
     *
     * @param o1
     * @param o2
     * @return
     */
    public static int compare(final Object o1, final Object o2) {
        if (null != o1 && null != o2) {
            if (o1.getClass().equals(o2.getClass())) {
                return compareSameTypes(o1, o2);
            } else {
                if (o1 instanceof Number && o2 instanceof Number) {
                    return compareSameTypes(((Number)o1).doubleValue(), ((Number)o2).doubleValue());
                } else {
                    return compareSameTypes(o1.toString(), o2.toString());
                }
            }
        } else if (null == o1) {
            return 1;
        } else if (null == o2) {
            return -1;
        }
        if (o1.equals(o2)) {
            return 0;
        }
        return -1;
    }

    public static boolean equals(final Object o1, final Object o2) {
        return compare(o1, o2) == 0;
    }

    public static boolean equals(final Number o1, final Number o2) {
        return compare(o1, o2) == 0;
    }

    public static boolean greater(final Object o1, final Object o2) {
        return compare(o1, o2) > 0;
    }

    public static boolean lower(final Object o1, final Object o2) {
        return compare(o1, o2) < 0;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static int compareSameTypes(final Object o1, final Object o2) {
        if (o1 instanceof Long) {
            return ((Long) o1).compareTo((Long) o2);
        } else if (o1 instanceof Integer) {
            return ((Integer) o1).compareTo((Integer) o2);
        } else if (o1 instanceof Byte) {
            return ((Byte) o1).compareTo((Byte) o2);
        } else if (o1 instanceof Double) {
            return ((Double) o1).compareTo((Double) o2);
        } else if (o1 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        }
        return -1;
    }

}
