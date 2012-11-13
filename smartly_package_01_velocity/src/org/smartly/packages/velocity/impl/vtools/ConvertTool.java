package org.smartly.packages.velocity.impl.vtools;

import org.smartly.commons.util.ConversionUtils;

/**
 * Conversion utility
 */
public class ConvertTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "convert";

    public ConvertTool() {

    }

    public String toString(final Object item) {
        return null != item ? item.toString() : "";
    }

    public boolean toBoolean(final Object item) {
        return ConversionUtils.toBoolean(item);
    }

    public int toInt() {
        return this.toInt(-1);
    }

    public int toInt(final Object value) {
        return this.toInt(value, -1);
    }

    public int toInt(final Object value, final int defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.intValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    public double toDouble() {
        return this.toDouble(-1.0);
    }

    public double toDouble(final Object value) {
        return this.toDouble(value, -1.0);
    }

    public double toDouble(final Object value, final double defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.doubleValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    public long toLong() {
        return this.toLong(-1);
    }

    public long toLong(final Object value) {
        return this.toLong(value, -1);
    }

    public long toLong(final Object value, final int defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.longValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
