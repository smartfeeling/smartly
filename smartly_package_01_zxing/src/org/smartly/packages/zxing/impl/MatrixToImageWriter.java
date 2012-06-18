/*
 * 
 */
package org.smartly.packages.zxing.impl;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import org.smartly.commons.lang.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author angelo.geminiani
 */
public class MatrixToImageWriter {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private MatrixToImageWriter() {
    }

    /**
     * Renders a {@link com.google.zxing.common.BitMatrix} as an image, where "false" bits are rendered
     * as white, and "true" bits are rendered as black.
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * Renders a {@link com.google.zxing.qrcode.encoder.ByteMatrix} as an image, as a
     * {@link java.awt.image.BufferedImage}. The byte values are construed as (unsigned)
     * luminance values, in theory. However, anything but 0 will be rendered as
     * white, and 0 will be rendered as black.
     */
    public static BufferedImage toBufferedImage(ByteMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) == 0 ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * Writes a {@link com.google.zxing.common.BitMatrix} to a file.
     *
     * @see #toBufferedImage(com.google.zxing.common.BitMatrix)
     */
    public static void writeToFile(final BitMatrix matrix,
                                   final String format, final File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, file);
    }

    /**
     * Writes a {@link com.google.zxing.qrcode.encoder.ByteMatrix} to a file.
     *
     * @see #toBufferedImage(com.google.zxing.qrcode.encoder.ByteMatrix)
     */
    public static void writeToFile(final ByteMatrix matrix,
                                   final String format, final File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, file);
    }

    /**
     * Writes a {@link com.google.zxing.common.BitMatrix} to a stream.
     *
     * @see #toBufferedImage(com.google.zxing.common.BitMatrix)
     */
    public static void writeToStream(final BitMatrix matrix,
                                     final String format, final OutputStream stream)
            throws IOException {
        final BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, stream);
    }

    /**
     * Writes a {@link com.google.zxing.qrcode.encoder.ByteMatrix} to a stream.
     *
     * @see #toBufferedImage(com.google.zxing.qrcode.encoder.ByteMatrix)
     */
    public static void writeToStream(final ByteMatrix matrix,
                                     final String format, final OutputStream stream)
            throws IOException {
        final BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, stream);
    }

    public static byte[] toByteArray(final BitMatrix matrix,
                                     final String format)
            throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStream os = new BufferedOutputStream(baos);
        writeToStream(matrix, format, os);
        baos.flush();
        return baos.toByteArray();
    }

    public static byte[] toByteArray(final ByteMatrix matrix,
                                     final String format)
            throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStream os = new BufferedOutputStream(baos);
        writeToStream(matrix, format, os);
        return baos.toByteArray();
    }

    public static String toString(final BitMatrix matrix,
                                  final String format)
            throws IOException {
        return Base64.encodeBytes(toByteArray(matrix, format));
    }

    public static String toString(final ByteMatrix matrix,
                                  final String format)
            throws IOException {
        return Base64.encodeBytes(toByteArray(matrix, format));
    }
}
