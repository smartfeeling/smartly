/*
 * Zipper.java
 *
 */
package org.smartly.commons.util;


import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

/**
 * Utility
 */
public class ZipUtils {

    private ZipUtils() {
    }

    public static void zip(final String filename, final List<File> fileList) throws IOException {
        zip(filename, fileList, true);
    }

    public static void zip(final String filename, final List<File> fileList,
                           final boolean includepaths) throws IOException {
        zip(filename, fileList, includepaths, null);
    }

    public static void zip(final String filename, final List<File> fileList,
                           final boolean includepaths, final String pathtoremove) throws IOException {
        final List<String> fileNames = new ArrayList<String>();
        for (final File file : fileList) {
            fileNames.add(file.getAbsolutePath());
        }
        zip(filename,
                fileNames.toArray(new String[fileNames.size()]),
                includepaths,
                pathtoremove);
    }

    public static void zip(final String filename, final String[] files) throws IOException {
        zip(filename, files, true, null);
    }

    public static void zip(final String filename, final String[] files,
                           final boolean includepaths) throws IOException {
        zip(filename, files, includepaths, null);
    }

    public static void zip(final String filename, final String[] files,
                           final boolean includepaths, final String pathtoremove) throws IOException {
        // check
        final File zip = new File(filename);
        if (zip.exists()) {
            throw new IOException("File " + filename + " already exist.");
            //return;
        }

        // open out file and write input files
        final String[] fileNames = enumAllFilesInDirectory(files, null);
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));

        zip(out, fileNames, includepaths, pathtoremove);
    }

    public static void unzip(final String source, final String target) throws IOException {
        final String targetFolder = PathUtils.isDirectory(target) ? target : PathUtils.getParent(target);
        final char sep = File.separatorChar;
        byte[] buff = new byte[1024];
        int m;
        int n;
        // for each file in zip
        final ZipFile zip = new ZipFile(source);
        final Enumeration e = zip.entries();
        while (e.hasMoreElements()) {
            // get filename using local separator
            final ZipEntry entry = (ZipEntry) e.nextElement();
            final String entryName = PathUtils.concat(targetFolder, entry.getName());
            final StringBuilder fixed = new StringBuilder(entryName);
            for (int i = 0; i < fixed.length(); ++i) {
                if (fixed.charAt(i) == '/') {
                    fixed.setCharAt(i, sep);
                }
            }
            final File file = new File(fixed.toString());

            // create dir
            if (entry.isDirectory()) {
                file.mkdirs();
                continue;
            }
            final String dir = file.getParent();
            if (dir != null) {
                new File(dir).mkdirs();
            }
            // unzip file
            //System.out.println("unzipping: " + file);
            final OutputStream out = new FileOutputStream(file);
            final InputStream in = zip.getInputStream(entry);
            try {
                while ((n = in.read(buff, 0, buff.length)) != -1) {
                    out.write(buff, 0, n);
                }
            } finally {
                out.flush();
                out.close();
                in.close();
            }
        }
        zip.close();
    }

    public static StringBuilder list(final String filename) throws IOException {
        final StringBuilder result = new StringBuilder();

        final ZipFile zip = new ZipFile(filename);
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy.mm.dd hh:mm:ss");
        final Enumeration e = zip.entries();
        result.append("      Size |         Date        | Name");
        result.append("\n");
        result.append("-----------+---------------------+--------------------------");
        result.append("\n");
        while (e.hasMoreElements()) {
            final ZipEntry entry = (ZipEntry) e.nextElement();
            String sz = "         " + entry.getSize();
            sz = sz.substring(sz.length() - 10);
            final String tm = fmt.format(new Date(entry.getTime()));
            result.append(sz).append(" | ").append(tm).append(" | ").append(entry.getName());
            result.append("\n");
        }

        return result;
    }

    public static String[] getEntryNames(final String filename) throws IOException {
        final List<String> result = new LinkedList<String>();
        final ZipFile zip = new ZipFile(filename);
        final Enumeration e = zip.entries();
        while (e.hasMoreElements()) {
            final ZipEntry entry = (ZipEntry) e.nextElement();
            result.add(entry.getName());
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Gzip something.
     *
     * @param in original content
     * @return size gzipped content
     */
    public static byte[] gzip(byte[] in) throws IOException {
        final Logger logger = getLogger();
        if (in != null && in.length > 0) {
            long tstart = System.currentTimeMillis();
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            GZIPOutputStream gout = new GZIPOutputStream(bout);
            gout.write(in);
            gout.flush();
            gout.close();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "gzipping took {0} msec",
                        (System.currentTimeMillis() - tstart));
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }

    public static InputStream gzip(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final GZIPOutputStream gout = new GZIPOutputStream(out);

        int c;
        while ((c = in.read()) != -1) {
            gout.write(c);
        }
        in.close();
        gout.close();

        byte[] data = out.toByteArray();
        return new ByteArrayInputStream(data);
    }

    public static byte[] gunzip(byte[] in) throws IOException {
        final Logger logger = getLogger();
        if (in != null && in.length > 0) {
            long tstart = System.currentTimeMillis();
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final ByteArrayInputStream bin = new ByteArrayInputStream(in);
            final GZIPInputStream gzipInputStream = new GZIPInputStream(bin);
            byte[] buf = new byte[1024];  //size can be changed according to programmer's need.
            int len;
            while ((len = gzipInputStream.read(buf)) > 0) {
                bout.write(buf, 0, len);
            }
            bout.flush();
            bout.close();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "gzipping took {0} msec",
                        (System.currentTimeMillis() - tstart));
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }

    public static InputStream gunzip(final InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static Logger getLogger() {
        return LoggingUtils.getLogger(ZipUtils.class);
    }

    /**
     * Enum recursively all files in directory and sub directory.
     *
     * @return return an array of filenames with full path.
     */
    private static String[] enumAllFilesInDirectory(final String[] files, final String dir) {
        final char sep = File.separatorChar;
        final List<String> v = new LinkedList<String>();
        for (int i = 0; i < files.length; ++i) {
            final String filename = dir == null ? files[i] : dir + sep + files[i];
            final File file = new File(filename);
            v.add(filename);
            if (file.isDirectory()) {
                final String[] res = enumAllFilesInDirectory(file.list(), filename);
                for (int j = 0; j < res.length; ++j) {
                    v.add(res[j]);
                }
            }
        }
        final String[] res = v.toArray(new String[v.size()]);
        return res;
    }

    private static void zip(final ZipOutputStream out,
                            final String[] files,
                            boolean includepath,
                            final String pathtoremove) throws IOException {
        InputStream in;
        int n;
        final byte[] buff = new byte[1024];
        try {
            for (final String fileName : files) {
                final File file = new File(fileName);
                if (file.isDirectory()) {
                    continue;
                }
                final String filePath = getPath(file, includepath, pathtoremove);
                final ZipEntry entry = new ZipEntry(filePath);
                out.putNextEntry(entry);
                in = new FileInputStream(file);
                try {
                    while ((n = in.read(buff, 0, buff.length)) != -1) {
                        out.write(buff, 0, n);
                    }
                } finally {
                    try {
                        if (null != out) {
                            out.closeEntry();
                        }
                    } catch (Throwable t) {
                        getLogger().log(Level.SEVERE, null, t);
                    }
                    try {
                        if (null != in) {
                            in.close();
                        }
                    } catch (Throwable t) {
                        getLogger().log(Level.SEVERE, null, t);
                    }
                }
            }
        } finally {
            if (null != out) {
                out.finish();
                out.flush();
                out.close();
            }
        }
    }

    private static String getPath(final File file,
                                  boolean includepath,
                                  final String pathtoremove) {
        if (includepath) {
            return file.getPath();
        } else {
            if (null == pathtoremove) {
                return file.getName();
            } else {
                return PathUtils.subtract(pathtoremove, file.getPath());
            }
        }
    }
}
