package org.smartly.packages.zxing.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.StringUtils;

import java.io.IOException;
import java.util.Hashtable;

/**
 *
 */
public final class CodeCreator {


    public BitMatrix createCode(final String content,
                                final BarcodeFormat format,
                                final int width,
                                final int height) {
        try {
            final BarcodeFormat fmt = null != format ? format : BarcodeFormat.QR_CODE;
            final Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);


            final MultiFormatWriter writer = new MultiFormatWriter();
            return writer.encode(content, fmt,
                    width, height, hints);


        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private BarcodeFormat getFormat(final String format) {
        if (StringUtils.hasText(format)) {
            try {
                return BarcodeFormat.valueOf(format);
            } catch (Throwable ignored) {
            }
        }
        return BarcodeFormat.QR_CODE;
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static CodeCreator __instance;

    public static CodeCreator getInstance() {
        if (null == __instance) {
            __instance = new CodeCreator();
        }
        return __instance;
    }

    public static BarcodeFormat toFormat(final String format) {
        return getInstance().getFormat(format);
    }

    public static byte[] asBytes(final String content) throws IOException {
        return asBytes(content, BarcodeFormat.QR_CODE, 300, 300);
    }

    public static byte[] asBytes(final String content,
                                 final BarcodeFormat format,
                                 final int width,
                                 final int height) throws IOException {
        final BitMatrix code = getInstance().createCode(content, format, width, height);
        return null != code ? MatrixToImageWriter.toByteArray(code, "png") : new byte[0];
    }

    public static String asBase64(final String content) throws IOException {
        return asBase64(content, BarcodeFormat.QR_CODE, 300, 300);
    }

    public static String asBase64(final String content,
                                  final BarcodeFormat format,
                                  final int width,
                                  final int height) throws IOException {
        final BitMatrix code = getInstance().createCode(content, format, width, height);
        return null != code ? MatrixToImageWriter.toString(code, "png") : "";
    }
}
