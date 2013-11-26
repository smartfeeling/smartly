package org.smartly.packages.pdf.impl;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 */
public class PDFUtilsTest {


    private static final String pdfFile = "/ARCHIVIO/Smartly/workspace/_smartly_pdf/_test/file.pdf";
    private static final String output = "/ARCHIVIO/Smartly/workspace/_smartly_pdf/_test/";

    @Test
    public void testToImage() throws Exception {

        File file = new File(pdfFile);
        PDFUtils.toImage(file, output, false, BufferedImage.TYPE_INT_RGB, 96);

    }

}
