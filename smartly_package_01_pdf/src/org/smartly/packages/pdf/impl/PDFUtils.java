package org.smartly.packages.pdf.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.smartly.commons.Delegates;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * utility to PDF conversion
 */
public final class PDFUtils {

    private static final String SUFFIX_PAGE = "_pg_";
    private static final String EXT_IMAGE = "jpg";

    public static void forEachPage(final File pdfFile,
                                   final boolean nonSequential,
                                   Delegates.Function<Boolean> callback) throws IOException {
        final PDDocument doc = nonSequential ? PDDocument.loadNonSeq(pdfFile, null) : PDDocument.load(pdfFile);
        try {
            final List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
            final int len = pages.size();
            int count = 1;
            for (final PDPage page : pages) {
                if (null != callback) {
                    if (callback.handle(page, count, len)) {
                        count++;
                    } else {
                        break;
                    }
                }
            }
        } finally {
            doc.close();
        }
    }

    public static List<String> toImage(final File pdfFile,
                                       final String outputRoot,
                                       final boolean nonSequential) throws IOException {
        final int imageType = BufferedImage.TYPE_INT_RGB;
        final int resolution = 96;
        return toImage(pdfFile, outputRoot, nonSequential, imageType, resolution, 0, 0);
    }

    public static List<String> toImage(final File pdfFile,
                                       final String outputRoot,
                                       final boolean nonSequential,
                                       final int imageType,
                                       final int resolution,
                                       final int fromPage,
                                       final int toPage) throws IOException {
        final List<String> pages = new LinkedList<String>();
        final String name = PathUtils.getFilename(pdfFile.getName(), false);
        forEachPage(pdfFile, nonSequential, new Delegates.Function<Boolean>() {
            @Override
            public Boolean handle(Object... args) {
                try {
                    final PDPage page = (PDPage) args[0];
                    final int index = (Integer) args[1]; // base 1
                    final int count = (Integer) args[2]; // total pages
                    if (isInRange(index, fromPage, toPage)) {
                        final String path = PathUtils.concat(outputRoot,
                                name.concat(SUFFIX_PAGE).concat(index + ".").concat(EXT_IMAGE));
                        BufferedImage image = page.convertToImage(imageType, resolution);
                        ImageIOUtil.writeImage(image, EXT_IMAGE, path, imageType, resolution);
                        pages.add(path);
                        return true;
                    }
                } catch (Throwable t) {
                    getLogger().log(Level.SEVERE, null, t);
                }
                return false;
            }
        });
        return pages;
    }

    public static List<BufferedImage> toImage(final File pdfFile,
                                              final boolean nonSequential) throws IOException {
        final int imageType = BufferedImage.TYPE_INT_RGB;
        final int resolution = 96;
        return toImage(pdfFile, nonSequential, imageType, resolution, 0, 0);
    }

    public static List<BufferedImage> toImage(final File pdfFile,
                                              final boolean nonSequential,
                                              final int imageType,
                                              final int resolution,
                                              final int fromPage,
                                              final int toPage) throws IOException {
        final List<BufferedImage> pages = new LinkedList<BufferedImage>();
        forEachPage(pdfFile, nonSequential, new Delegates.Function<Boolean>() {
            @Override
            public Boolean handle(Object... args) {
                try {
                    final PDPage page = (PDPage) args[0];
                    final int index = (Integer) args[1]; // base 1
                    final int count = (Integer) args[2]; // total pages
                    if (isInRange(index, fromPage, toPage)) {
                        final BufferedImage image = page.convertToImage(imageType, resolution);
                        pages.add(image);
                        return true;
                    }
                } catch (Throwable t) {
                    getLogger().log(Level.SEVERE, null, t);
                }
                return false;
            }
        });
        return pages;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Logger getLogger() {
        return LoggingUtils.getLogger(PDFUtils.class);
    }

    private static boolean isInRange(final int index, final int from, final int to) {
        if (from > 0 && to > 0) {
            return index >= from && index <= to;
        }
        return true;
    }

}
