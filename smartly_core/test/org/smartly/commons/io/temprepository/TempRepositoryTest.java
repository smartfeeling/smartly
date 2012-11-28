package org.smartly.commons.io.temprepository;

import org.junit.Test;

/**
 *
 */
public class TempRepositoryTest {

    public TempRepositoryTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testTemp() throws Exception {

        TempRepository temp = new TempRepository("c:/_test/temp");

        temp.join();

        System.out.println("exit");
    }
}
