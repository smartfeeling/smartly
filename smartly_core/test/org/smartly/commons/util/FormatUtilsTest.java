package org.smartly.commons.util;

import org.junit.Test;

import java.util.Locale;

/**
 * User: angelo.geminiani
 */
public class FormatUtilsTest {

    public FormatUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testFormatNumber() throws Exception {
        double value = 23456789.345767;
        final Locale locale = Locale.ITALIAN;
        final int decimals = 2;

        String result = FormatUtils.formatNumber(value, locale, decimals);

        System.out.println(result);
        org.junit.Assert.assertEquals("23.456.789,3458", result);

        value = 23456789.3;
        result = FormatUtils.formatNumber(value, locale, decimals);
        System.out.println(result);
        org.junit.Assert.assertEquals("23.456.789,30", result);
    }
}
