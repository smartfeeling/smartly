package org.smartly.commons.util;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: angelo.geminiani
 */
public class ZipUtilsTest {

    public ZipUtilsTest() {

    }


    @Test
    public void testZip() throws Exception {
        String archive = "c:/_test/archive.zip";
        String file = "c:/_test/file.txt";

        ZipUtils.zip(archive, new String[]{file}, false);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
