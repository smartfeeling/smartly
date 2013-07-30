/*
 * PathUtils.java
 *
 */
package org.smartly.commons.util;

import org.smartly.IConstants;
import org.smartly.Smartly;
import org.smartly.commons.lang.CharEncoding;

import java.io.File;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains utility for path management.
 * Depends on StringUtils.
 *
 * @author
 */
public abstract class PathUtils
        implements IConstants {

    /**
     * Array of file system types. i.e. ["http://", "ftp://", ...] *
     */
    public static final String[] FILESYSTEM_TYPES = SystemUtils.FileType.getValues();

    private static final boolean IS_WINDOWS = SystemUtils.isWindows();
    private static final String TEMP = "/temp";

    /**
     * Returns temp folder.
     *
     * @return temp folder name for current user
     */
    public static String getTemporaryDirectory() {
        final String temp = concat(IConstants.USER_HOME, TEMP);
        try {
            FileUtils.mkdirs(temp);
        } catch (Throwable ignored) {
        }
        return temp;
    }

    /**
     * Returns a temp folder
     *
     * @param subFolder Sub folder
     * @return Temp folder name for current user
     */
    public static String getTemporaryDirectory(final String subFolder) {
        final String temp = concat(getTemporaryDirectory(), subFolder);
        try {
            FileUtils.mkdirs(temp);
        } catch (Throwable ignored) {
        }
        return temp;
    }

    /**
     * Extract parent Path from file name. i.e. "c:/mydir/filename.txt" returns
     * "c:/mydir/".
     *
     * @param path File path. i.e. "c:/mydir/filename.txt"
     * @return Parent path. i.e. "c:/mydir/"
     */
    public static String getParent(final String path) {
        try {
            final int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
            final String result;
            if (separatorIndex == -1) {
                result = "";
            } else if (separatorIndex == 0) {
                result = FOLDER_SEPARATOR;
            } else {
                result = path.substring(0, separatorIndex);
            }
            return result;
            //final File file = new File(clean);
            //return file.getParent();
        } catch (Throwable ignored) {
        }
        return path;
    }

    /**
     * Extract the filename from the given path,
     * e.g. "mypath/myfile.txt" -> "myfile.txt".
     *
     * @param path the file path (may be <code>null</code>)
     * @return the extracted filename, or <code>null</code> if none
     */
    public static String getFilename(final String path) {
        return getFilename(path, true);
    }

    /**
     * Extract the filename from the given path.
     * If "includeextension" is true the returned value will include
     * file extension (i.e. "myfile.txt"), otherwise file name extension
     * is stripped (i.e. "myfile").
     *
     * @param path
     * @param includeextension
     * @return
     */
    public static String getFilename(final String path,
                                     final boolean includeextension) {
        if (path == null) {
            return null;
        }
        final int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        final String filename = separatorIndex != -1
                ? path.substring(separatorIndex + 1)
                : path;
        if (!includeextension) {
            return stripFilenameExtension(filename);
        } else {
            return filename;
        }
    }

    /**
     * Extract the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "txt".
     *
     * @param path the file path (may be <code>null</code>)
     * @return the extracted filename extension, or <code>null</code> if none
     */
    public static String getFilenameExtension(final String path) {
        return getFilenameExtension(path, false);
    }

    /**
     * Extract the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "txt".
     *
     * @param path       the file path (may be <code>null</code>)
     * @param includeDot if true result is ".ext", else "ext"
     * @return the extracted filename extension, or <code>null</code> if none
     */
    public static String getFilenameExtension(final String path,
                                              boolean includeDot) {
        if (path == null) {
            return null;
        }
        final int sepIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        final int dotIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (sepIndex > -1 && dotIndex < sepIndex) {
            return null;
        }
        if (includeDot) {
            return (dotIndex != -1 ? path.substring(dotIndex) : null);
        } else {
            return (dotIndex != -1 ? path.substring(dotIndex + 1) : null);
        }
    }

    /**
     * Return true if path has an extension.
     *
     * @param path
     * @return
     */
    public static boolean isFile(final String path) {
        try {
            final String ext = PathUtils.getFilenameExtension(path);
            if (StringUtils.hasText(ext)) {
                return true;
            } else {
                final File file = new File(path);
                return file.isFile() || file.exists();
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * Return false is path has an extension or if it's not a directory.
     *
     * @param path
     * @return
     */
    public static boolean isDirectory(final String path) {
        final String ext = PathUtils.getFilenameExtension(path);
        if (StringUtils.hasText(ext)) {
            return false;
        } else {
            final File file = new File(path);
            return file.isDirectory();
        }
    }

    /**
     * Strip the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "mypath/myfile".
     *
     * @param path the file path (may be <code>null</code>)
     * @return the path with stripped filename extension,
     *         or <code>null</code> if none
     */
    public static String stripFilenameExtension(final String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
    }

    public static String changeFileExtension(final String path, String extension) {
        final String filename = stripFilenameExtension(path);
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return filename.concat(extension);
    }

    public static String changeFileName(final String path, final String newName) {
        final String parent = PathUtils.getParent(path);
        return PathUtils.join(parent, newName);
    }

    public static String suggestFileName(final String fileName) {
        return PathUtils.suggestFileName(fileName, true);
    }

    public static String suggestFileName(final String fileName,
                                         final boolean preserveExtension) {
        final File file = new File(fileName);
        if (file.exists()) {
            final String name = stripFilenameExtension(file.getName());
            final String path = file.getParent();
            String ext = preserveExtension ? PathUtils.getFilenameExtension(fileName) : "";    // txt

            if (StringUtils.hasText(ext)) {
                ext = ".".concat(ext);
            } else {
                ext = "";
            }

            Integer i = 1;
            while (true) {
                final String tmpName;
                if (StringUtils.hasText(path)) {
                    tmpName = path.concat(FOLDER_SEPARATOR).concat(name).concat("_").concat(i.toString()).concat(ext);
                } else {
                    tmpName = name.concat("_").concat(i.toString()).concat(ext);
                }
                final File tmpFile = new File(tmpName);
                if (!tmpFile.exists()) {
                    return tmpFile.getAbsolutePath();
                }
                i++;
            }
        } else {
            return fileName;
        }
    }

    /**
     * Return true if path's extension start with passed parameter.
     * This is case insentive.
     *
     * @param path      File path. i.e. : "c:/file.txt"
     * @param extension Extension. i.e.: ".txt", "txt", ...
     * @return True if path's extension start with passed extension.
     */
    public static boolean extensionStartWith(final String path, final String extension) {
        if (!StringUtils.hasText(extension) || !StringUtils.hasText(path)) {
            return false;
        }
        final String curext = getFilenameExtension(path);
        if (!StringUtils.hasText(curext)) {
            return false;
        }
        return curext.toLowerCase().startsWith(getFilenameExtension(".".concat(extension).toLowerCase()));
    }

    /**
     * Replace all windows folder separator "\" with java separator "/".
     * Remove also duplicates.
     *
     * @param path path to convert. i.e. "c:\\myfile.txt"
     * @return converted path. i.e. : "c:/myfile.txt"
     */
    public static String rawConvertAndReplaceDuplicates(final String path) {
        if (StringUtils.hasText(path)) {
            //-- replace '\' with '/' --//
            final String result = path.replace(WINDOWS_FOLDER_SEPARATOR,
                    FOLDER_SEPARATOR);
            return StringUtils.replaceDuplicates(
                    result,
                    FOLDER_SEPARATOR,
                    FILESYSTEM_TYPES);
        }
        return path;
    }

    /**
     * Replace all windows folder separator "\" with java separator "/".
     * Remove also duplicates, but preserve protocols ("http://", "ftp://", etc..).
     *
     * @param path path to convert. i.e. "c:\\myfile.txt", "file:///path//folder/file.txt"
     * @return converted path. i.e. : "c:/myfile.txt", "file:///path/folder/file.txt"
     */
    public static String toUnixPath(final String path) {
        return PathUtils.toUnixPath(path, FILESYSTEM_TYPES);
    }

    /**
     * Replace all windows folder separator "\" with java separator "/".
     * Remove also duplicates excluding those declared in "exclusion".
     *
     * @param path      path to convert. i.e. "c:\\myfile.txt"
     *                  are removed. i.e. "//" become "/".
     * @param exclusion Array of esclusions from duplicates replacement.
     *                  i.e. ["file://", "http://"]
     * @return converted path. i.e. : "c:/myfile.txt"
     */
    public static String toUnixPath(final String path,
                                    final String[] exclusion) {
        if (StringUtils.hasText(path)) {
            final String result = path.replace(WINDOWS_FOLDER_SEPARATOR,
                    FOLDER_SEPARATOR);
            return null != exclusion && exclusion.length > 0
                    ? StringUtils.replaceDuplicates(result, FOLDER_SEPARATOR, exclusion)
                    : StringUtils.replaceDuplicates(result, FOLDER_SEPARATOR);
        }
        return path;
    }

    /**
     * Replace all windows folder separator "\" with java separator "/".
     * Remove also duplicates and add a separator at the end
     * if path is not a file path.
     *
     * @param path Path to validate. i.e. "c:\\path"
     * @return validated path. i.e. : "c:/mypath/". If original path
     *         is a file name
     *         (i.e. "c:/myfile.txt"), no separator is added at the and of path.
     */
    public static String validateFolderSeparator(final String path) {
        final String result = PathUtils.toUnixPath(path);
        // add separator at the end if result has none, and if is not a file name
        if (!StringUtils.hasText(PathUtils.getFilenameExtension(result))
                && !result.endsWith(FOLDER_SEPARATOR)) {
            return result.concat(FOLDER_SEPARATOR);
        } else {
            return result;
        }
    }

    /**
     * Apply the given relative path to the given path,
     * assuming standard Java folder separation (i.e. "/" separators);<br>
     * i.e. full="<b>c:/fullfilepath/text.txt</b>" relative="/relative/relativefile.jpg"
     * output="c:/fullfilepath/relative/relativefile.jpg"<br>
     * i.e. full="<b>c:/fullfilepath/</b>" relative="/relative/relativefile.jpg"
     * output="c:/fullfilepath/relative/relativefile.jpg"<br>
     * i.e. full="<b>c:/fullfilepath</b>" relative="/relative/relativefile.jpg"
     * output="c:/relative/relativefile.jpg"; (Absolute path has no slash at the end).<br>
     *
     * @param path         the path to start from (usually a full file path)
     * @param relativePath the relative path to apply
     *                     (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    public static String applyRelativePath(final String path, final String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }

    /**
     * Count the path levels.
     *
     * @param path The path. i.e. : "c:/test/folder1/folder2"
     * @return
     */
    public static int getPathLevels(final String path) {
        int counter = 0;
        String[] tokens = toUnixPath(path).split("/");
        for (String token : tokens) {
            if (StringUtils.hasText(token)) {
                if (!StringUtils.hasText(getFilenameExtension(token))) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static String getPathRoot(final String path) {
        final String[] tokens = toUnixPath(path).split("/");
        if (tokens.length > 0) {
            for (final String token : tokens) {
                if (StringUtils.hasLength(token)) {
                    return FOLDER_SEPARATOR + token;
                }
            }
        }
        return path;
    }


    public static String splitPathRoot(final String path) {
        final String root = getPathRoot(path);
        if (StringUtils.hasText(root) && !root.equalsIgnoreCase("/")) {
            return path.substring(path.indexOf(root) + root.length());
        }
        return path;
    }

    public static String getCanonicalPath(final String path) {
        try {
            return (new File(path)).getCanonicalPath();
        } catch (Throwable ignored) {
            return path;
        }
    }

    /**
     * Check a path for folders, and return a sequence of "../"
     * to use as a prefix for relative path navigation in http application.<br>
     * i.e.: "/folder1/folder2/page1.html" return "../../". If you want navigate
     * to "/page2.html" from "/folder1/folder2/page1.html" you can use
     * "../../page2.html".
     *
     * @param path the path to check
     * @return sequence of "../" for each path folder.
     */
    public static String getPathLevelsPrefix(final String path) {
        final int count = getPathLevels(path);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append("../");
        }

        return result.toString();
    }

    /**
     * Transform a path in relative path adding a dot (.) at beginning.<br/>
     * If path has a protocol (i.e. "http://"), the absolute path is returned.
     *
     * @param path Path. i.e. "/system/templates"
     * @return Relative path. i.e. "./system/templates"
     */
    public static String getRelativePath(final String path) {
        if (StringUtils.hasText(path)
                && !PathUtils.hasProtocol(path)) {
            if (PathUtils.isAbsolute(path)) {
                if (path.startsWith(FOLDER_SEPARATOR)) {
                    return ".".concat(path);
                } else {
                    return ".".concat(FOLDER_SEPARATOR).concat(path);
                }
            }
        }

        return path;
    }

    /**
     * Return a relative URL from navigation from a starting point to a destination.<br>
     * i.e. getRelativePath("/folder1/page1.html", "home.html") returns "../home.html"<br>
     * If I want navigate from page1.html to home.html, I must consider that page1.html is
     * child of "folder1" folder.
     *
     * @param startingPoint Starting page
     * @param destination   Destination page
     * @return Relative URL for http navigation from "startingPoint" to "destination.".<br>
     *         i.e. getRelativePath("/folder1/page1.html", "home.html") returns "../home.html"
     */
    public static String getRelativePath(final String startingPoint, final String destination) {
        final String prefix = getPathLevelsPrefix(startingPoint);
        return PathUtils.join(prefix, destination);
    }

    /**
     * Return true if passed file match at least with one of wildChars.<br>
     * e.g. file "c:/file.txt" match with ["*.jpg", "*.txt"].<br>
     * e.g. file "c:/file.txt" does not match with ["*.jpg", "*.gif"].<br>
     *
     * @param path      file name or full file path. e.g. "file.txt", "c:\file.txt"
     * @param wildChars Array of wild-chars to check. e.g. ["*.txt", "file*.gif", "another.*"]
     * @return true if at least one wild-char is matched with path.
     */
    public static boolean fileMatch(String path, String[] wildChars) {
        for (String wildChar : wildChars) {
            if (fileMatch(path, wildChar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if passed file match with wildChar.<br>
     * e.g. file "c:/file.txt" match with ["*.jpg", "*.txt"].<br>
     * e.g. file "c:/file.txt" does not match with ["*.jpg", "*.gif"].<br>
     *
     * @param path     file name or full file path. e.g. "file.txt", "c:\file.txt"
     * @param wildChar wild-char to check. e.g. "*.txt"
     * @return true if wild-char is matched with path.
     */
    public static boolean fileMatch(String path, String wildChar) {
        String mypath = toUnixPath(path);
        String[] tokens = wildChar.split("\\.");
        boolean result = false;
        String nameFilter = tokens[0].trim();
        String extFilter = tokens[1].trim();
        String fileName = getFilename(mypath).toLowerCase();
        String clearNameFilter = nameFilter.replace("*", "");
        String clearExtFilter = extFilter.replace("*", "");
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
                if (stripFilenameExtension(fileName).equalsIgnoreCase(nameFilter)) {
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
        return result;
    }

    /**
     * Return true if file name is base file name.<br>
     * Base file name is a file name, without any localization info.
     * i.e. "file_it.properties" is not a base name.<br>
     * i.e. "file.properties" is a base name.
     *
     * @param path Path to check
     * @return
     */
    public static boolean isBaseName(final String path) {
        final String fileName = PathUtils.getFilename(path, false);
        final String[] tokens = fileName.split("_");
        if (null != tokens && tokens.length > 0) {
            final String token = tokens[tokens.length - 1];
            if (LocaleUtils.isISOLanguage(token) || LocaleUtils.isISOCountry(token)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if path is an absolute path.<br>
     * Linux path and windows have same result.
     * All paths are considered relatives path if starts with "." (dot).
     *
     * @param path Path to check
     * @return true if path is absolute.
     */
    public static boolean isAbsolute(final String path) {
        final String checkpath = toUnixPath(path);
        if (checkpath.startsWith(".")) {
            return false;
        } else if (checkpath.indexOf(":") > 0
                || checkpath.startsWith(FOLDER_SEPARATOR)) {
            // 'c:', 'http://'
            return true;
        }
        return false;
    }

    /**
     * Return true if passed path is a jar path.
     *
     * @param path Path
     * @return True is path contains String ".jar!", i.e. "file:/c:/myjar.jar!/org/sf/path"
     */
    public static boolean isJar(final String path) {
        return path.indexOf(".jar!") > -1;
    }

    /**
     * Return true if path is an absolute path.<br/>
     * This method uses O.S. specific functions, so
     * Linux path and windows path may have different results.
     *
     * @param path Path to check
     * @return true if path is absolute.
     */
    public static boolean isAbsoluteOS(final String path) {
        final String checkpath = toUnixPath(path);
        if (checkpath.indexOf(":") > 0) {
            // 'c:', 'http://'
            return true;
        } else if (checkpath.startsWith("./") || checkpath.startsWith("../")) {
            // './', '../'
            return false;
        } else if (IS_WINDOWS) {
            // windows path check
            if (checkpath.startsWith("/")) {
                return false;
            }
        }

        // Operating System decision.
        return (new File(path)).isAbsolute();
    }

    /**
     * Return true if path is an Http path.
     *
     * @param path Path to check
     * @return true if path is absolute.
     */
    public static boolean isHttp(final String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        final String lower = path.toLowerCase();
        return lower.startsWith("http:") || lower.startsWith("https:");
    }

    public static boolean isFtp(final String path) {
        return PathUtils.hasProtocol(path, SystemUtils.FileType.ftp.getValue())
                || PathUtils.hasProtocol(path, SystemUtils.FileType.sftp.getValue());
    }

    /**
     * Return true if text is a valid URI.
     *
     * @param path absolute or relative path or what else.
     * @return true, if path has a protocol or start with relative path.
     */
    public static boolean isURI(final String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        return hasProtocol(path)
                || path.startsWith(".")
                || path.startsWith("/")
                || path.startsWith("\\");
    }

    public static boolean hasProtocol(final String path) {
        if (StringUtils.hasText(path)) {
            for (final String protocol : FILESYSTEM_TYPES) {
                if (path.startsWith(protocol)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasProtocol(final String path, final String prot) {
        if (StringUtils.hasText(path)) {
            for (final String protocol : FILESYSTEM_TYPES) {
                if (path.startsWith(protocol)) {
                    if (protocol.equalsIgnoreCase(prot)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getProtocol(final String path) {
        if (StringUtils.hasText(path)) {
            for (final String protocol : FILESYSTEM_TYPES) {
                if (path.startsWith(protocol)) {
                    return protocol;
                }
            }
        }
        return "";
    }

    public static String stripProtocol(final String path) {
        final String protocol = getProtocol(path);
        if (StringUtils.hasText(protocol)) {
            return path.substring(protocol.length());
        }
        return path;
    }

    /**
     * Add parameters to URI. i.e. "/uri/page?param1=1234&param2=567"
     *
     * @param uri    URI
     * @param params Map of parameters
     * @return URI with parameters . i.e. "/uri/page?param1=1234&param2=567"
     */
    public static String addURIParameters(final String uri,
                                          final Map<String, ? extends Object> params) {
        return addURIParameters(uri, params, true);
    }

    public static String addURIParameters(final String uri,
                                          final Map<String, ? extends Object> params,
                                          final boolean encodeValues) {
        final StringBuilder result = new StringBuilder();
        result.append(uri);
        if (!CollectionUtils.isEmpty(params)) {
            result.append("?");
            final StringBuilder paramsStr = new StringBuilder();
            final Set<String> keys = params.keySet();
            for (final String key : keys) {
                final Object value = params.get(key);
                if (null != value) {
                    if (paramsStr.length() > 0) {
                        paramsStr.append("&");
                    }
                    paramsStr.append(key).append("=");
                    if (encodeValues && !StringUtils.isURLEncoded(value.toString())) {
                        paramsStr.append(encode(value.toString()));
                    } else {
                        paramsStr.append(value.toString());
                    }
                }
            }
            result.append(paramsStr);
        }

        return result.toString();
    }

    /**
     * Resolve path from c:/dir1/dir2/../file.txt to C:/dir1/file.txt
     *
     * @param url Path. ie: c:/dir1/dir2/../file.txt
     * @return Resolved Path  C:/dir1/file.txt
     */
    public static String resolve(final String url) {
        final String protocol = PathUtils.getProtocol(url);
        final String path = StringUtils.hasText(protocol) ? url.substring(protocol.length()) : url;
        final List<String> list = new LinkedList<String>();
        final String[] tokens = StringUtils.split(path, "/");
        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
            if (StringUtils.hasText(token)) {
                if (token.equalsIgnoreCase("..")) {
                    // move back
                    if (!list.isEmpty()) {
                        list.remove(list.size() - 1);
                    }
                } else if (!token.equalsIgnoreCase(".")) {
                    // add token
                    list.add(i == tokens.length - 1 ? token : token + "/");
                }
            }
        }
        return protocol + (list.isEmpty() ? path : CollectionUtils.toString(list));
    }

    /**
     * Concat two paths, but does not check for duplicate folder separators
     * or folder separators format (unix or windows).<br>
     * This method assumes that path are unix like.
     *
     * @param path1
     * @param path2
     * @return Concatenated path.
     */
    public static String concat(final String path1, final String path2) {
        if (StringUtils.hasText(path1)) {
            if (!path1.endsWith(FOLDER_SEPARATOR)
                    && !path2.startsWith(FOLDER_SEPARATOR)) {
                return path1.concat(FOLDER_SEPARATOR).concat(path2);
            } else {
                if (path1.equalsIgnoreCase(FOLDER_SEPARATOR)) {
                    return path2;
                }
                if (path1.endsWith(FOLDER_SEPARATOR) && path2.startsWith(FOLDER_SEPARATOR)) {
                    return path1.concat(path2.substring(1));
                }
                return path1.concat(path2);
            }
        } else {
            return path2;
        }
    }

    /**
     * Like concat, but clean path 2
     *
     * @param path1
     * @param path2
     * @return Concatenated path.
     */
    public static String join(final String path1, final String path2) {
        final String cleanpath2 = clean(path2);
        if (StringUtils.hasText(path1)) {
            if (!path1.endsWith(FOLDER_SEPARATOR)
                    && !cleanpath2.startsWith(FOLDER_SEPARATOR)) {
                return path1.concat(FOLDER_SEPARATOR).concat(cleanpath2);
            } else {
                if (path1.equalsIgnoreCase(FOLDER_SEPARATOR)) {
                    return cleanpath2;
                }
                if (path1.endsWith(FOLDER_SEPARATOR) && path2.startsWith(FOLDER_SEPARATOR)) {
                    return path1.concat(cleanpath2.substring(1));
                }
                return path1.concat(cleanpath2);
            }
        } else {
            return cleanpath2;
        }
    }

    /**
     * Merge two paths. <br>
     * Ex: merge ("c:\myRoot", "myFileName")
     * result = "c:\myRoot\myFileName"
     *
     * @param path1 First path
     * @param path2 Second path
     * @return Merged path
     */
    public static String merge(final String path1, final String path2) {
        //-- Remove characters like '//', '..//../' --//
        final String root = PathUtils.toUnixPath(path1);
        final String path = PathUtils.toUnixPath(path2);

        return mergeCleanPath(root, path, false);
    }

    /**
     * Combine two paths melting folders with same names.
     * Ex: combine("d:\myRoot\folder1", "folder1\myFileName")
     * result = "d:\myRoot\folder1\myFileName";
     *
     * @param path1 root path. i.e. "d:\myRoot\folder1"
     * @param path2 second path. i.e. "d:\myRoot\folder1\myFileName"
     * @return The fusion between two paths. i.e. "d:\myRoot\folder1\myFileName"
     */
    public static String combine(final String path1, final String path2) {
        final String root = PathUtils.toUnixPath(path1,
                SystemUtils.FileType.getValues());
        final String path = PathUtils.toUnixPath(path2);

        return mergeCleanPath(root, path, true);
    }

    /**
     * Subtract first path to second one.
     * <p>
     * subtractPath = "c:/forder1/"<br>
     * inPath = "c:/forder1/dir/test.txt"<br>
     * result = "/dir/test.txt";
     * </p>
     *
     * @param subtractPath Path to remove from inPath
     * @param fullPath     Original path
     * @return Resultant path
     */
    public static String subtract(String subtractPath, String fullPath) {

        //-- Clean the subtract path --//
        subtractPath = PathUtils.validateFolderSeparator(subtractPath);
        //subtractPath = PathUtils.cleanPath(subtractPath);
        if (subtractPath.startsWith(FOLDER_SEPARATOR)
                && subtractPath.endsWith(FOLDER_SEPARATOR)) {
            subtractPath = subtractPath.substring(1);
        }
        //-- Clean inputpath (the result) --//
        String result = fullPath;
        if (!PathUtils.isAbsolute(subtractPath)) {
            if (!subtractPath.endsWith(FOLDER_SEPARATOR)) {
                subtractPath = subtractPath.concat(FOLDER_SEPARATOR);
            }
            if (!subtractPath.startsWith(FOLDER_SEPARATOR)) {
                subtractPath = FOLDER_SEPARATOR.concat(subtractPath);
            }
            result = result.replace(subtractPath, "/");
        } else {
            result = result.replace(subtractPath, "");
        }

        // if result path is not absolute, check if start with /
        if (!PathUtils.isAbsolute(result) && !result.startsWith(FOLDER_SEPARATOR)) {
            result = FOLDER_SEPARATOR.concat(result);
        }
        return result;
    }

    /**
     * Return absolute path relative at current folder.
     */
    public static String getAbsolutePath(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return path;
        }
        final File f = new File(Smartly.getHome());
        final String converted;
        final String relativePath = path.startsWith(".") ? path.substring(1) : path;
        if (f.getAbsolutePath().endsWith(".")) {
            converted = PathUtils.toUnixPath(f.getAbsolutePath().replace(".", relativePath));
        } else {
            converted = PathUtils.toUnixPath(join(f.getAbsolutePath(), relativePath));
        }
        return converted;
    }

    public static String getPackagePath(final Class clazz) {
        if (null != clazz) {
            return clazz.getPackage().getName().replace(".", "/");
        } else {
            return null;
        }
    }

    public static String getClassPath(final Class clazz) {
        return clazz.getName().replace(".", "/");
    }

    public static String getClassPath(final String className) {
        if (className.indexOf("/") == -1 && className.indexOf("\\") == -1) {
            return className.replace(".", "/");
        } else {
            return className;
        }
    }

    /**
     * Short cut to File.exists() method
     */
    public static boolean exists(String path) {
        final File file = new File(path);
        return file.exists();
    }

    /**
     * Creates a path based on current date and time.<br/>
     *
     * @param detail 0=yyyy/MM/dd, 1=yyyy/, 2=yyyy/MM, 3=yyyy/MM/dd, 4=yyyy/MM/dd/hh, 5=yyyy/MM/dd/hh/mm, 6=yyyy/MM/dd/hh/mm/ss/
     * @return Path. i.e. "2013/01/25/18"
     */
    public static String getDateTimePath(final int detail) {
        final DateWrapper dt = new DateWrapper(DateUtils.now());
        final String year = dt.getYear() + FOLDER_SEPARATOR;
        final String month = dt.getMonth() + FOLDER_SEPARATOR;
        final String day = dt.getDay() + FOLDER_SEPARATOR;
        final String hour = dt.getHour() + FOLDER_SEPARATOR;
        final String min = dt.getMinute() + FOLDER_SEPARATOR;
        final String sec = dt.getSecond() + FOLDER_SEPARATOR;
        if (detail == 1) {
            return FOLDER_SEPARATOR.concat(year);
        } else if (detail == 2) {
            return FOLDER_SEPARATOR.concat(year).concat(month);
        } else if (detail == 3) {
            return FOLDER_SEPARATOR.concat(year).concat(month).concat(day);
        } else if (detail == 4) {
            return FOLDER_SEPARATOR.concat(year).concat(month).concat(day).concat(hour);
        } else if (detail == 5) {
            return FOLDER_SEPARATOR.concat(year).concat(month).concat(day).concat(hour).concat(min);
        } else if (detail == 6) {
            return FOLDER_SEPARATOR.concat(year).concat(month).concat(day).concat(hour).concat(min).concat(sec);
        } else {
            return FOLDER_SEPARATOR.concat(year).concat(month).concat(day);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static String mergeCleanPath(final String root,
                                         final String path2, final boolean mergeNames) {
        if (!StringUtils.hasText(root)) {
            return path2;
        }
        final String path = clean(path2);
        final String result;
        final StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(path)) {
            if (mergeNames) {
                sb.append(PathUtils.mergeNames(root, path));
            } else {
                sb.append(root);
            }
            //-- Append last '/' --//
            if (!sb.toString().endsWith(FOLDER_SEPARATOR)
                    && !path.startsWith(FOLDER_SEPARATOR)) {
                sb.append(FOLDER_SEPARATOR);
            }
            //-- append path to root --//
            sb.append(path);
            result = sb.toString(); //cleanPath(sb.toString());
        } else {
            sb.append(root);
            result = sb.toString();
        }

        return result;
    }

    private static String mergeNames(final String root,
                                     final String path) {
        final StringBuilder sb = new StringBuilder();
        final String[] rootTokens = root.split(FOLDER_SEPARATOR);
        for (int i = 0; i < rootTokens.length; i++) {
            final String token = rootTokens[i];
            if (StringUtils.hasText(token)) {
                if (path.startsWith(token)
                        || path.startsWith(FOLDER_SEPARATOR + token)) {
                    break;
                } else {
                    final boolean abs = i == 0
                            ? PathUtils.isAbsolute(token)
                            : false;
                    if (!abs && !sb.toString().endsWith(FOLDER_SEPARATOR)) {
                        sb.append(FOLDER_SEPARATOR);
                    }
                    // is root?
                    if (sb.length() == 0) {
                        sb.append(checkFsType(token));
                    } else {
                        sb.append(token);
                    }
                }
            }
        }
        // add folder separator only if already does not end with it.
        if (sb.lastIndexOf(FOLDER_SEPARATOR) != sb.length() - 1
                && !path.startsWith(FOLDER_SEPARATOR)) {
            sb.append(FOLDER_SEPARATOR);
        }
        return sb.toString();
    }

    private static String checkFsType(final String text) {
        final SystemUtils.FileType type = SystemUtils.FileType.getType(text);
        if (null == type) {
            return text;
        } else {
            return type.getValue();
        }
    }

    private static String clean(final String path) {
        if (StringUtils.hasText(path)
                && path.startsWith(".")
                && path.length() > 1) {
            return path.substring(path.indexOf(".") + 1);
        } else {
            return path;
        }
    }

    private static String encode(final String s) {
        try {
            return URLEncoder.encode(s, CharEncoding.getDefault());
        } catch (Exception ignored) {
        }
        return s;
    }


}
