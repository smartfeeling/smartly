package org.smartly.commons.image;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.IBufferedImageFactory;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.jpeg.segments.GenericSegment;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ImageIOUtils {

    public static final int COLOR_TYPE_RGB = 1;
    public static final int COLOR_TYPE_CMYK = 2;
    public static final int COLOR_TYPE_YCCK = 3;

    private static final String ISO_COATED_V2_300 = "/eci/ISOcoated_v2_300_eci.icc";

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /**
     * Read all formats included CYMK
     *
     * @param file
     * @return
     * @throws IOException
     * @throws ImageReadException
     */
    public static BufferedImage readExt(final File file) throws IOException, ImageReadException {
        final ImageReaderExt reader = new ImageReaderExt();
        return reader.readImage(file);
    }

    /**
     * Read standard formats
     *
     * @param file
     * @return
     * @throws ImageReadException
     * @throws IOException
     */
    public static BufferedImage read(final File file)
            throws ImageReadException, IOException {
        final Map<String, Object> params = new HashMap<String, Object>();

        // set optional parameters if you like
        params.put(ImagingConstants.BUFFERED_IMAGE_FACTORY,
                new ManagedImageBufferedImageFactory());

        // params.put(ImagingConstants.PARAM_KEY_VERBOSE, Boolean.TRUE);

        // read image
        final BufferedImage image = Imaging.getBufferedImage(file, params);

        return image;
    }

    public static ImageSize getImageSize(final File file) {
        final ImageSize size = new ImageSize();
        try {
            final BufferedImage image = readExt(file);
            if (null != image) {
                size.setWidth(image.getWidth());
                size.setHeight(image.getHeight());
            }
        } catch (Throwable ignored) {
        }
        return size;
    }

    public static void metadataExample(final File file) throws ImageReadException,
            IOException {
        // get all metadata stored in EXIF format (ie. from JPEG or TIFF).
        final IImageMetadata metadata = Imaging.getMetadata(file);

        // System.out.println(metadata);

        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            // Jpeg EXIF metadata is stored in a TIFF-based directory structure
            // and is identified with TIFF tags.
            // Here we look for the "x resolution" tag, but
            // we could just as easily search for any other tag.
            //
            // see the TiffConstants file for a list of TIFF tags.

            System.out.println("file: " + file.getPath());

            // print out various interesting EXIF tags.
            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_XRESOLUTION);
            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
            printTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO);
            printTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
            printTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            printTagValue(jpegMetadata,
                    ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE);
            printTagValue(jpegMetadata,
                    GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            printTagValue(jpegMetadata,
                    GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

            System.out.println();

            // simple interface to GPS data
            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
            if (null != exifMetadata) {
                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
                if (null != gpsInfo) {
                    final String gpsDescription = gpsInfo.toString();
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                    System.out.println("    " + "GPS Description: "
                            + gpsDescription);
                    System.out.println("    "
                            + "GPS Longitude (Degrees East): " + longitude);
                    System.out.println("    "
                            + "GPS Latitude (Degrees North): " + latitude);
                }
            }

            // more specific example of how to manually access GPS values
            final TiffField gpsLatitudeRefField = jpegMetadata
                    .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
            final TiffField gpsLatitudeField = jpegMetadata
                    .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
            final TiffField gpsLongitudeRefField = jpegMetadata
                    .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
            final TiffField gpsLongitudeField = jpegMetadata
                    .findEXIFValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
            if (gpsLatitudeRefField != null && gpsLatitudeField != null
                    && gpsLongitudeRefField != null
                    && gpsLongitudeField != null) {
                // all of these values are strings.
                final String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
                final RationalNumber gpsLatitude[] = (RationalNumber[]) (gpsLatitudeField
                        .getValue());
                final String gpsLongitudeRef = (String) gpsLongitudeRefField
                        .getValue();
                final RationalNumber gpsLongitude[] = (RationalNumber[]) gpsLongitudeField
                        .getValue();

                final RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
                final RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
                final RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

                final RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
                final RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
                final RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

                // This will format the gps info like so:
                //
                // gpsLatitude: 8 degrees, 40 minutes, 42.2 seconds S
                // gpsLongitude: 115 degrees, 26 minutes, 21.8 seconds E

                System.out.println("    " + "GPS Latitude: "
                        + gpsLatitudeDegrees.toDisplayString() + " degrees, "
                        + gpsLatitudeMinutes.toDisplayString() + " minutes, "
                        + gpsLatitudeSeconds.toDisplayString() + " seconds "
                        + gpsLatitudeRef);
                System.out.println("    " + "GPS Longitude: "
                        + gpsLongitudeDegrees.toDisplayString() + " degrees, "
                        + gpsLongitudeMinutes.toDisplayString() + " minutes, "
                        + gpsLongitudeSeconds.toDisplayString() + " seconds "
                        + gpsLongitudeRef);

            }

            System.out.println();

            final List<IImageMetadata.IImageMetadataItem> items = jpegMetadata.getItems();
            for (int i = 0; i < items.size(); i++) {
                final IImageMetadata.IImageMetadataItem item = items.get(i);
                System.out.println("    " + "item: " + item);
            }

            System.out.println();
        }
    }

    private static void printTagValue(final JpegImageMetadata jpegMetadata,
                                      final TagInfo tagInfo) {
        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field == null) {
            System.out.println(tagInfo.name + ": " + "Not Found.");
        } else {
            System.out.println(tagInfo.name + ": "
                    + field.getValueDescription());
        }
    }

    /**
     * This example illustrates how to set the GPS values in JPEG EXIF metadata.
     *
     * @param jpegImageFile A source image file.
     * @param dst           The output file.
     * @throws IOException
     * @throws ImageReadException
     * @throws ImageWriteException
     */
    public void setExifGPSTag(final File jpegImageFile, final File dst, final double latitude, final double longitude) throws IOException,
            ImageReadException, ImageWriteException {
        OutputStream os = null;
        boolean canThrow = false;
        try {
            TiffOutputSet outputSet = null;

            // note that metadata might be null if no metadata is found.
            final IImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                // note that exif might be null if no Exif metadata is found.
                final TiffImageMetadata exif = jpegMetadata.getExif();

                if (null != exif) {
                    // TiffImageMetadata class is immutable (read-only).
                    // TiffOutputSet class represents the Exif data to write.
                    //
                    // Usually, we want to update existing Exif metadata by
                    // changing
                    // the values of a few fields, or adding a field.
                    // In these cases, it is easiest to use getOutputSet() to
                    // start with a "copy" of the fields read from the image.
                    outputSet = exif.getOutputSet();
                }
            }

            // if file does not contain any exif metadata, we create an empty
            // set of exif metadata. Otherwise, we keep all of the other
            // existing tags.
            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            {
                // Example of how to add/update GPS info to output set.

                // New York City
                // final double longitude = -74.0; // 74 degrees W (in Degrees East)
                // final double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees
                // North)

                outputSet.setGPSInDegrees(longitude, latitude);
            }

            os = new FileOutputStream(dst);
            os = new BufferedOutputStream(os);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
            canThrow = true;
        } finally {
            try {
                os.close();
            } catch (Throwable ignored) {
            }
            //IoUtils.closeQuietly(canThrow, os);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static void convertYcckToCmyk(final WritableRaster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        int stride = width * 4;
        int[] pixelRow = new int[stride];
        for (int h = 0; h < height; h++) {
            raster.getPixels(0, h, width, 1, pixelRow);

            for (int x = 0; x < stride; x += 4) {
                int y = pixelRow[x];
                int cb = pixelRow[x + 1];
                int cr = pixelRow[x + 2];

                int c = (int) (y + 1.402 * cr - 178.956);
                int m = (int) (y - 0.34414 * cb - 0.71414 * cr + 135.95984);
                y = (int) (y + 1.772 * cb - 226.316);

                if (c < 0) c = 0;
                else if (c > 255) c = 255;
                if (m < 0) m = 0;
                else if (m > 255) m = 255;
                if (y < 0) y = 0;
                else if (y > 255) y = 255;

                pixelRow[x] = 255 - c;
                pixelRow[x + 1] = 255 - m;
                pixelRow[x + 2] = 255 - y;
            }

            raster.setPixels(0, h, width, 1, pixelRow);
        }
    }

    private static void convertInvertedColors(final WritableRaster raster) {
        int height = raster.getHeight();
        int width = raster.getWidth();
        int stride = width * 4;
        int[] pixelRow = new int[stride];
        for (int h = 0; h < height; h++) {
            raster.getPixels(0, h, width, 1, pixelRow);
            for (int x = 0; x < stride; x++)
                pixelRow[x] = 255 - pixelRow[x];
            raster.setPixels(0, h, width, 1, pixelRow);
        }
    }

    private static BufferedImage convertCmykToRgb(final Raster cmykRaster, ICC_Profile cmykProfile) throws IOException {
        if (cmykProfile == null)
            cmykProfile = ICC_Profile.getInstance(ImageIOUtils.class.getResourceAsStream(ISO_COATED_V2_300));

        if (cmykProfile.getProfileClass() != ICC_Profile.CLASS_DISPLAY) {
            byte[] profileData = cmykProfile.getData();

            if (profileData[ICC_Profile.icHdrRenderingIntent] == ICC_Profile.icPerceptual) {
                intToBigEndian(ICC_Profile.icSigDisplayClass, profileData, ICC_Profile.icHdrDeviceClass); // Header is first

                cmykProfile = ICC_Profile.getInstance(profileData);
            }
        }

        ICC_ColorSpace cmykCS = new ICC_ColorSpace(cmykProfile);
        BufferedImage rgbImage = new BufferedImage(cmykRaster.getWidth(), cmykRaster.getHeight(), BufferedImage.TYPE_INT_RGB);
        WritableRaster rgbRaster = rgbImage.getRaster();
        ColorSpace rgbCS = rgbImage.getColorModel().getColorSpace();
        ColorConvertOp cmykToRgb = new ColorConvertOp(cmykCS, rgbCS, null);
        cmykToRgb.filter(cmykRaster, rgbRaster);
        return rgbImage;
    }

    private static void intToBigEndian(int value, byte[] array, int index) {
        array[index] = (byte) (value >> 24);
        array[index + 1] = (byte) (value >> 16);
        array[index + 2] = (byte) (value >> 8);
        array[index + 3] = (byte) (value);
    }

    // --------------------------------------------------------------------
    //               E M B E D D E D
    // --------------------------------------------------------------------

    private static class ImageReaderExt {
        private int colorType = COLOR_TYPE_RGB;
        private boolean hasAdobeMarker = false;


        public BufferedImage readImage(final File file) throws IOException, ImageReadException {
            colorType = COLOR_TYPE_RGB;
            hasAdobeMarker = false;

            final ImageInputStream stream = ImageIO.createImageInputStream(file);
            final Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
            while (iter.hasNext()) {
                ImageReader reader = iter.next();
                reader.setInput(stream);

                BufferedImage image;
                ICC_Profile profile = null;
                try {
                    image = reader.read(0);
                } catch (IIOException e) {
                    colorType = COLOR_TYPE_CMYK;
                    checkAdobeMarker(file);
                    profile = Imaging.getICCProfile(file);
                    WritableRaster raster = (WritableRaster) reader.readRaster(0, null);
                    if (colorType == COLOR_TYPE_YCCK)
                        convertYcckToCmyk(raster);
                    if (hasAdobeMarker)
                        convertInvertedColors(raster);
                    image = convertCmykToRgb(raster, profile);
                }

                return image;
            }

            return null;
        }

        public void checkAdobeMarker(final File file) throws IOException, ImageReadException {
            final JpegImageParser parser = new JpegImageParser();
            final ByteSource byteSource = new ByteSourceFile(file);
            final List<Segment> segments = parser.readSegments(byteSource, new int[]{0xffee}, true);
            if (segments != null && segments.size() >= 1) {
                final Segment segment = segments.get(0);
                GenericSegment app14Segment = (GenericSegment) segment;
                byte[] data = app14Segment.bytes;
                if (data.length >= 12 && data[0] == 'A' && data[1] == 'd' && data[2] == 'o' && data[3] == 'b' && data[4] == 'e') {
                    hasAdobeMarker = true;
                    int transform = app14Segment.bytes[11] & 0xff;
                    if (transform == 2)
                        colorType = COLOR_TYPE_YCCK;
                }
            }
        }
    }

    private static class ManagedImageBufferedImageFactory implements
            IBufferedImageFactory {

        public BufferedImage getColorBufferedImage(final int width, final int height,
                                                   final boolean hasAlpha) {
            final GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
            final GraphicsDevice gd = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gd.getDefaultConfiguration();
            return gc.createCompatibleImage(width, height,
                    Transparency.TRANSLUCENT);
        }

        public BufferedImage getGrayscaleBufferedImage(final int width, final int height,
                                                       final boolean hasAlpha) {
            return getColorBufferedImage(width, height, hasAlpha);
        }
    }


}
