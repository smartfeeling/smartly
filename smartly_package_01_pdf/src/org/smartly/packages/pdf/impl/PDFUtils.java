package org.smartly.packages.pdf.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.apache.pdfbox.util.PDFImageWriter;
import org.smartly.commons.Delegates;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * utility to PDF conversion
 */
public final class PDFUtils {

    private static final String SUFFIX_PAGE = "_pg_";
    private static final String EXT_IMAGE = "jpg";

    public static void forEachPage(final File pdfFile,
                                   final boolean nonSequential,
                                   Delegates.Action callback) throws IOException {
        final PDDocument doc = nonSequential ? PDDocument.loadNonSeq(pdfFile, null) : PDDocument.load(pdfFile);
        try {
            final List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
            int count = 0;
            for (final PDPage page : pages) {
                if (null != callback) {
                    callback.handle(page, count);
                    count++;
                }
            }
        } finally {
            doc.close();
        }
    }

    public static void toImage(final File pdfFile,
                               final String outputRoot,
                               final boolean nonSequential) throws IOException {
        final int imageType = BufferedImage.TYPE_INT_RGB;
        final int resolution = 96;
        toImage(pdfFile, outputRoot, nonSequential, imageType, resolution);
    }

    public static void toImage(final File pdfFile,
                               final String outputRoot,
                               final boolean nonSequential,
                               final int imageType,
                               final int resolution) throws IOException {
        final String name = PathUtils.getFilename(pdfFile.getName(), false);
        forEachPage(pdfFile, nonSequential, new Delegates.Action() {
            @Override
            public void handle(Object... args) {
                try {
                    final PDPage page = (PDPage) args[0];
                    final int index = (Integer) args[1];
                    final String path = PathUtils.concat(outputRoot,
                            name.concat(SUFFIX_PAGE).concat(index + ".").concat(EXT_IMAGE));
                    BufferedImage image = page.convertToImage(imageType, resolution);
                    ImageIOUtil.writeImage(image, EXT_IMAGE, path, imageType, resolution);
                } catch (Throwable t) {
                    getLogger().log(Level.SEVERE, null, t);
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Logger getLogger() {
        return LoggingUtils.getLogger(PDFUtils.class);
    }

}
