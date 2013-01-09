package org.smartly.commons.util;

import org.junit.Test;

import java.util.Date;

/**
 * User: angelo.geminiani
 */
public class DateUtilsTest {

    public DateUtilsTest() {

    }

    @Test
    public void testPostponeWorkingDay() throws Exception {
        Date now = DateUtils.now();
        Date date = DateUtils.postponeWorkingDay(now, DateUtils.DAY, 30, new Long[0]);
        System.out.println(date.toString());
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
