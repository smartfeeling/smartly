/*
 * 
 */
package org.smartly.commons.util;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.*;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * @author
 */
public abstract class FileUtils {

    private static final Logger logger = LoggingUtils.getLogger(FileUtils.class.getName());
    public static final int BLOCK_SIZE = 1024 * 4;
    /**
     * Preserves the last modified time and other attributes if possible.
     *
     * @see #copy(java.io.File, java.io.File, int)
     */
    public static final int CP_PRESERVE = 0x0001;
    /**
     * Copy only when the source is newer or when the destination is missing.
     *
     * @see #copy(java.io.File, java.io.File, int)
     */
    public static final int CP_UPDATE = 0x0002;
    /**
     * Overwrites the destination file.
     *
     * @see #copy(java.io.File, java.io.File, int)
     */
    public static final int CP_OVERWRITE = 0x0004;

    /**
     * Ensure all directories are created
     *
     * @param fileName
     * @throws IOException
     */
    public static String mkdirs(final String fileName) throws IOException {
        File dir = null;
        final String ext = PathUtils.getFilenameExtension(fileName);
        if (!StringUtils.hasText(ext)) {
            dir = new File(fileName);
        } else {
            final File file = new File(fileName);
            dir = file.getParentFile();
        }
        if (null != dir && !dir.exists()) {
            boolean done = dir.mkdirs();
            if (!done) {
                throw new IOException("Unable to create Directory: " + dir.getAbsolutePath());
            }
        }
        return fileName;
    }

    public static boolean exists(final String fileName) {
        return (new File(fileName)).exists();
    }

    public static long getCRC(final InputStream is) {
        try {
            // Computer CRC32 checksum
            final CheckedInputStream cis = new CheckedInputStream(
                    is, new CRC32());

            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }
            long checksum = cis.getChecksum().getValue();
            // System.out.println(checksum + " " + fileSize + " " + fileName);
            return checksum;
        } catch (Throwable t) {
        }
        return 0;
    }

    public static long getSize(final InputStream is) {
        long size = 0L;
        try {
            byte[] barr = new byte[1024];
            while (true) {
                int r = is.read(barr);
                if (r <= 0) {
                    break;
                }
                size += r;
            }
        } catch (Throwable t) {
        }
        return size;
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.File
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given input File to the given output File.
     *
     * @param in  the file to copy from
     * @param out the file to copy to
     * @throws java.io.IOException in case of I/O errors
     */
    public static void copy(final File in, final File out) throws IOException {
        copy(
                new BufferedInputStream(new FileInputStream(in)),
                new BufferedOutputStream(new FileOutputStream(out)));
    }

    /**
     * Copy the contents of the given byte array to the given output File.
     *
     * @param in  the byte array to copy from
     * @param out the file to copy to
     * @throws java.io.IOException in case of I/O errors
     */
    public static void copy(byte[] in, File out) throws IOException {
        ByteArrayInputStream inStream = new ByteArrayInputStream(in);
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
        copy(inStream, outStream);
    }

    /**
     * Copy the contents of the given input File into a new byte array.
     *
     * @param in the file to copy from
     * @return the new byte array that has been copied to
     * @throws java.io.IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(final File in) throws IOException {
        return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
    }

    /**
     * Copies a file or a directory into another.
     * <p/>
     * <p>If neither {@link #CP_UPDATE} nor {@link #CP_OVERWRITE},
     * IOException is thrown if the destination exists.
     *
     * @param flags any combination of {@link #CP_UPDATE}, {@link #CP_PRESERVE},
     *              {@link #CP_OVERWRITE}.
     */
    public static void copy(File src, File dst, int flags)
            throws IOException {
        if (!src.exists()) {
            throw new FileNotFoundException(src.toString());
        }

        if (dst.isDirectory()) {
            if (src.isDirectory()) {
                copyDir(src, dst, flags);
            } else {
                copyFile(src, new File(dst, src.getName()), flags);
            }
        } else if (dst.isFile()) {
            if (src.isDirectory()) {
                throw new IOException("Unable to copy a directory, " + src + ", to a file, " + dst);
            } else {
                copyFile(src, dst, flags);
            }
        } else {
            if (src.isDirectory()) {
                copyDir(src, dst, flags);
            } else {
                copyFile(src, dst, flags);
            }
        }
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.File
    //---------------------------------------------------------------------

    /**
     * Copies a reader into a file (the original content, if any, are erased).
     * The source and destination files will be closed after copied.
     *
     * @param dst     the destination
     * @param reader  the source
     * @param charset the charset; null as default (ISO-8859-1).
     */
    public static void copy(Reader reader, File dst, String charset)
            throws IOException {
        final File parent = dst.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        final Writer writer = charset != null ? new OutputStreamWriter(new FileOutputStream(dst), charset) : new FileWriter(dst);

        copy(reader, writer, true);
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.File
    //---------------------------------------------------------------------

    /**
     * Copies an input stream into a file
     * (the original content, if any, are erased).
     * The file will be closed after copied.
     *
     * @param in  the source
     * @param dst the destination
     */
    public static void copy(final InputStream in, final File dst)
            throws IOException {
        final File parent = dst.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        final OutputStream out =
                new BufferedOutputStream(new FileOutputStream(dst));

        copy(in, out);

    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.InputStream / java.io.OutputStream
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * Closes both streams when done.
     *
     * @param in  the stream to copy from
     * @param out the stream to copy to
     * @throws java.io.IOException in case of I/O errors
     */
    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[BLOCK_SIZE];
            int nrOfBytes = -1;
            while ((nrOfBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, nrOfBytes);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Could not close InputStream", ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Could not close OutputStream", ex);
            }
        }
    }

    /**
     * Copy the contents of the given byte array to the given OutputStream.
     *
     * @param in  the byte array to copy from
     * @param out the OutputStream to copy to
     * @throws java.io.IOException in case of I/O errors
     */
    public static void copy(byte[] in, OutputStream out) throws IOException {
        copy(new ByteArrayInputStream(in), out);
    }

    /**
     * Copy the contents of the given InputStream into a new byte array.
     *
     * @param in the stream to copy from
     * @return the new byte array that has been copied to
     * @throws java.io.IOException in case of I/O errors
     */
    public static byte[] copyToByteArray(final InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    //---------------------------------------------------------------------
    // Copy methods for java.io.Reader / java.io.Writer
    //---------------------------------------------------------------------

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param reader the Reader to copy from
     * @param writer the Writer to copy to
     * @throws java.io.IOException reader case of I/O errors
     */
    public static void copy(final Reader reader, final Writer writer) throws IOException {
        copy(reader, writer, true);
    }

    /**
     * Copy the contents of the given Reader to the given Writer.
     * Closes both when done.
     *
     * @param reader the Reader to copy from
     * @param writer the Writer to copy to
     * @param close  True if reader and writer must be closed on exit.
     * @throws java.io.IOException reader case of I/O errors
     */
    public static void copy(final Reader reader, final Writer writer, final boolean close) throws IOException {
        try {
            final char[] buf = new char[BLOCK_SIZE];
            for (int v; (v = reader.read(buf)) >= 0; ) {
                if (v > 0) {
                    writer.write(buf, 0, v);
                }
            }
            writer.flush();
        } finally {
            try {
                if (close) {
                    reader.close();
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Could not close Reader", ex);
            }
            try {
                if (close) {
                    writer.close();
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Could not close Writer", ex);
            }
        }
    }

    /**
     * Copy the contents of the given String to the given output Writer.
     *
     * @param in  the String to copy from
     * @param out the Writer to copy to
     * @throws java.io.IOException in case of I/O errors
     */
    public static void copy(String in, Writer out) throws IOException {
        copy(new StringReader(in), out, true);
    }

    /**
     * Copy the contents of the given Reader into a String.
     *
     * @param in the reader to copy from
     * @return the String that has been copied to
     * @throws java.io.IOException in case of I/O errors
     */
    public static String copyToString(final Reader in) throws IOException {
        final StringWriter out = new StringWriter();
        copy(in, out, true);
        return out.toString();
    }

    /**
     * Returns all characters reader the reader, never null
     * (but its length might zero).
     * <p>Notice: this method is memory hungry.
     */
    public static StringBuffer copyToStringBuffer(Reader in)
            throws IOException {
        final StringWriter out = new StringWriter(BLOCK_SIZE);
        copy(in, out, true);
        return out.getBuffer();
    }

    /**
     * Shortcut to copy(string.getBytes(encoding), output);
     * @param output File
     * @param data  Data to write
     * @param encoding charset
     * @throws IOException
     */
    public static void writeStringToFile(final File output,
                                         final String data,
                                         final String encoding) throws IOException{
       final byte[] bytes = data.getBytes(encoding);
        copy(bytes, output);
    }

    /**
     * Shortcut to new String(copyToByteArray(file));
     * @param file File
     * @return String
     * @throws IOException
     */
    public static String readFileToString(final File file) throws IOException {
        final byte[] bytes = copyToByteArray(file);
        return new String(bytes);
    }

    //---------------------------------------------------------------------
    // List methods
    //---------------------------------------------------------------------

    public static void listFiles(final List<File> fileList,
                                 final File startDir) {
        listFiles(fileList, startDir, "*.*", null, -1);
    }

    public static void listFiles(final List<File> fileList,
                                 final File startDir,
                                 final String includeWildChars) {
        listFiles(fileList, startDir, includeWildChars, null, -1);
    }

    /**
     * Get all the files under the specified folder (including all the files under sub-folders)
     *
     * @param startDir         Initial folder
     * @param includeWildChars Allowed comma separated wild chars. i.e. "*.html, *.zip" or "*.*"
     * @param excludeWildChars Disallowed comma separated wild chars. i.e. "template*.html, *.zip".
     * @param fileList         - the fileList to be returned
     */
    public static void listFiles(final List<File> fileList,
                                 final File startDir,
                                 final String includeWildChars,
                                 final String excludeWildChars) {
        listFiles(fileList, startDir, includeWildChars, excludeWildChars, -1);
    }

    /**
     * List all files included in includeWildChars rage, but not included in
     * excludeWildChars range.
     *
     * @param startDir         Starting directory
     * @param includeWildChars Comma separated values of wild-char to include. i.e.
     *                         "*.png, *.jpg"
     * @param excludeWildChars Comma separated values of wild-char to exclude. i.e.
     *                         "template*.png, template*.jpg"
     * @param deepLevel        Level of recursion. If -1, all folders will be explored.
     * @param fileList         A list to fill with retrieved file names.
     */
    public static void listFiles(final List<File> fileList,
                                 final File startDir,
                                 final String includeWildChars,
                                 final String excludeWildChars,
                                 int deepLevel) {
        final String iwc;
        if (!StringUtils.hasLength(includeWildChars)) {
            iwc = "*.*";
        } else {
            iwc = includeWildChars;
        }
        final String ewc;
        if (!StringUtils.hasLength(excludeWildChars)) {
            ewc = null;
        } else {
            ewc = excludeWildChars;
        }
        final String[] iwcTokens = StringUtils.split(iwc, ",");
        final String[] ewcTokens = StringUtils.split(ewc, ",");
        listAllFiles(fileList,
                startDir, // starting dir
                iwcTokens, // include tokens
                ewcTokens, // exclude tokens
                0, // current level
                deepLevel // max deep level
        );
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /**
     * Assumes both dst and src is a file.
     */
    private static void copyFile(File src, File dst, int flags)
            throws IOException {
        assert src.isFile();
        if (dst.equals(src)) {
            throw new IOException("Copy to the same file, " + src);
        }

        if ((flags & CP_OVERWRITE) == 0) {
            if ((flags & CP_UPDATE) != 0) {
                if (dst.lastModified() >= src.lastModified()) {
                    return;
                } //nothing to do
            } else if (dst.exists()) {
                throw new IOException("The destination already exists, " + dst);
            }
        }

        copy(new FileInputStream(src), dst);

        if ((flags & CP_PRESERVE) != 0) {
            dst.setLastModified(src.lastModified());
        }
    }

    /**
     * Assumes both dst and src is a directory.
     */
    private static void copyDir(File src, File dst, int flags)
            throws IOException {
        assert src.isDirectory();
        final File[] srcs = src.listFiles();
        for (int j = 0; j < srcs.length; ++j) {
            copy(srcs[j], new File(dst, srcs[j].getName()), flags); //recursive
        }
    }

    private static void listAllFiles(final List<File> fileList,
                                     final File startDir,
                                     final String[] includeWildChars,
                                     final String[] excludeWildChars,
                                     final int currentLevel,
                                     final int deepLevel) {
        int level = currentLevel;
        if (startDir.exists() && startDir.isDirectory()) {
            final File[] files = startDir.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    final File file = files[i];
                    if (file.isFile()) {
                        if (fileMatch(file, includeWildChars, excludeWildChars)) {
                            fileList.add(file);
                        }
                    } else if (file.isDirectory()) {
                        if ((deepLevel > -1) && (level >= deepLevel)) {
                            continue;
                        }
                        level++;
                        try {
                            listAllFiles(fileList,
                                    file,
                                    includeWildChars,
                                    excludeWildChars,
                                    level,
                                    deepLevel);
                        } catch (Exception e) {
                        }
                        level--;
                    }
                }
            }
        }// if
    }

    private static boolean fileMatch(final File file,
                                     final String[] includeWildChars,
                                     String... excludeWildChars) {
        if (null == excludeWildChars || excludeWildChars.length == 0) {
            return fileMatch(file, includeWildChars);
        } else {
            return fileMatch(file, includeWildChars) && !fileMatch(file, excludeWildChars);
        }
    }

    private static boolean fileMatch(final File file,
                                     final String[] wildChars) {
        boolean result = false;

        for (final String wildChar : wildChars) {
            final String[] tokens = wildChar.split("\\.");
            final String nameFilter = tokens[0];
            final String extFilter = tokens[1];
            final String fileName = file.getName().toLowerCase();
            final String clearNameFilter = nameFilter.replace("*", "");
            final String clearExtFilter = extFilter.replace("*", "");
            if (extFilter.equals("*")) {
                // All extensions
                if (nameFilter.equals("*")) {
                    // *.*
                    result = true;
                } else if (nameFilter.endsWith("*")) {
                    // file*.*
                    if (fileName.startsWith(clearNameFilter.toLowerCase())) {
                        result = true;
                    }
                } else {
                    // file.*
                    if (fileName.equalsIgnoreCase(nameFilter)) {
                        result = true;
                    }
                }
            } else {
                // only certain extensions
                if (nameFilter.equals("*")) {
                    // *.EXT
                    if (fileName.endsWith(extFilter.toLowerCase())) {
                        result = true;
                    }
                } else if (nameFilter.endsWith("*")) {
                    // file*.EXT
                    if (fileName.startsWith(clearNameFilter.toLowerCase()) && fileName.endsWith(extFilter.toLowerCase())) {
                        result = true;
                    }
                } else {
                    // file.EXT
                    if (fileName.equalsIgnoreCase(nameFilter + "." + extFilter)) {
                        result = true;
                    }
                }
            }
            if (result) {
                break;
            }
        }

        return result;
    }



}
