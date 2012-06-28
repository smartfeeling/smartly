package org.smartly.commons.repository.deploy;

import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.repository.FileRepository;
import org.smartly.commons.repository.Resource;
import org.smartly.commons.util.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author angelo.geminiani
 */
public abstract class FileDeployer {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    public static final String CHARSET = CharEncoding.getDefault();

    public static final String SUFFIX_MIN = ".mini";

    private static final String[] EXCLUDE = new String[]{
            ".class"
    };

    private static final String DIRECTIVE_VERSION = "[RT-VERSION]";
    private static final String DIRECTIVE_VERSION_VALUE = GUID.create();
    private static final String DIRECTIVE_DEBUG_APP = "[RT-DEBUG]";
    private static final String DIRECTIVE_DEBUG_JS = "[RT-DEBUGJS]";
    private static final String[] DIRECTIVE_FILES = new String[]{".js", ".vm"}; // array of files that support compiler directives
    private static final String[] COMPRESS_FILES = new String[]{".js", ".css"};
    // ------------------------------------------------------------------------
    //                      Variables
    // ------------------------------------------------------------------------
    private final Set<String> _compilableFiles;
    private final Map<String, String> _compilableValues;
    private final List<FileItem> _resources;
    private final String _startFolder;
    private final String _targetFolder;
    private boolean _overwriteAll;
    private String[] _overwriteItems;
    private final boolean _verbose;
    private final boolean _debugApp;
    private final boolean _debugJs;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public FileDeployer(final String startFolder,
                        final String targetFolder,
                        final boolean verbose,
                        final boolean debugApp,
                        final boolean debugJs) {
        this.logInfo("Creating FileDeployer '{0}'. "
                + "Start Folder: '{1}', Target Folder: '{2}'",
                this.getClass().getSimpleName(), startFolder, targetFolder);
        _compilableFiles = new HashSet<String>(Arrays.asList(DIRECTIVE_FILES));
        _compilableValues = new HashMap<String, String>();
        _resources = new LinkedList<FileItem>();
        _startFolder = startFolder;
        _targetFolder = targetFolder;
        _overwriteAll = false;
        _overwriteItems = new String[0];
        _verbose = verbose;
        _debugApp = debugApp;
        _debugJs = debugJs;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------
    public boolean isOverwrite() {
        return _overwriteAll;
    }

    public void setOverwrite(boolean overwrite) {
        _overwriteAll = overwrite;
    }

    public String[] getOverwriteItems() {
        return _overwriteItems;
    }

    public void setOverwriteItems(final String[] value) {
        _overwriteItems = value;
    }

    public Set<String> getCompilableFiles() {
        return _compilableFiles;
    }

    public Map<String, String> getCompilableValues() {
        return _compilableValues;
    }

    public void deployChildren() {
        this.deploy(_targetFolder, true);
    }

    public void deploy() {
        this.deploy(_targetFolder, false);
    }

    /**
     * Deploy content into target
     *
     * @param targetFolder Parent root. i.e. "c:\", "ftp://USERNAME:PASSWORD@host:21/myfolder/mysubfolder"
     * @param children Deploy only content of startFolder into targetFolder
     */
    public void deploy(final String targetFolder,
                       final boolean children) {
        this.loadResources(_startFolder);

        this.logStart();

        for (final FileItem item : _resources) {
            final String message = this.deploy(targetFolder, item);
            if (StringUtils.hasText(message)) {
                this.logInfo(message);
            }
        }
    }

    public abstract byte[] compress(final byte[] data, final String filename);

    public abstract byte[] beforeDeploy(final byte[] data, final String filename);

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        //-- init compilable values --//
        _compilableValues.put(DIRECTIVE_VERSION, DIRECTIVE_VERSION_VALUE);
        _compilableValues.put(DIRECTIVE_DEBUG_APP, _debugApp + "");
        _compilableValues.put(DIRECTIVE_DEBUG_JS, _debugJs + "");
    }

    private void logStart() {
        this.getLogger().log(Level.INFO, FormatUtils.format(
                "FILE DEPLOYER: Running FileDeployer: {0}\n" +
                        "\t Target Path: {1}, \n" +
                        "\t Overwrite Target: {2}",
                this.getClass().getSimpleName(),
                _targetFolder, _overwriteAll));
    }

    private void logInfo(final String text, final Object... args) {
        if (_verbose) {
            this.getLogger().log(Level.INFO, FormatUtils.format(
                    "FILE DEPLOYER: " + text, args));
        }
    }

    private String deploy(final String targetFolder, final FileItem item) {
        String message = "";
        final String filename = item.getFileName();
        // check if file extension is not excluded
        if (this.isDeployable(filename)) {
            final String targetPath = PathUtils.merge(targetFolder, filename);
            final File target = new File(targetPath);
            final boolean exists = target.exists();
            final boolean overwrite = this.isOverwritable(filename);
            final boolean compilable = this.isCompilable(filename);
            final boolean compressible = FileDeployer.isCompressible(filename);
            String compressedPath = null;
            Exception exc = null;
            int deployed = 0;
            if (!exists || overwrite) {
                if (!item.isDirectory()) {
                    try {
                        FileUtils.mkdirs(target.getAbsolutePath());
                        final String packagename = item.getPackageName();
                        final InputStream in = this.read(packagename);
                        if (null != in) {
                            deployed = 1;
                            byte[] binaryData = ByteUtils.getBytes(in);
                            if (compilable) {
                                binaryData = this.compile(binaryData);
                            }

                            //-- before deploy event --//
                            binaryData = this.beforeDeploy(binaryData, target.getName());

                            //-- deploy file --//
                            FileUtils.copy(binaryData, target);

                            //-- compress file --//
                            if (compressible) {
                                // creates new minified file
                                final byte[] compressedData = this.compress(binaryData, target.getName());
                                if (null != compressedData && compressedData.length > 0) {
                                    compressedPath = getMiniFilename(target.getAbsolutePath());
                                    FileUtils.copy(binaryData, new File(compressedPath));
                                }
                            }

                            try {
                                in.close();
                            } catch (Throwable t) {
                            }
                        } else {
                            deployed = 0;
                        }
                    } catch (Exception ex) {
                        exc = ex;
                        deployed = -1;
                    }
                } else {
                    deployed = 2;
                }
            }
            // log deploy status
            if (deployed == 0) {
                message = FormatUtils.format(
                        "FAULT! File '{0}' not deployed into '{1}': exists={2} and overwrite={3}",
                        item.getPackageName(), targetPath, exists, overwrite);
            } else if (deployed == -1) {
                message = FormatUtils.format(
                        "FAULT! File '{0}' not deployed into '{1}': {2}",
                        item.getPackageName(), targetPath, exc);
            } else if (deployed == 2) {
                message = FormatUtils.format(
                        "INFO! '{0}' is a Directory and has not been deployed.",
                        item.getPackageName());
            } else {
                message = FormatUtils.format(
                        "SUCCESS! File '{0}' deployed into '{1}': exists={2} and overwrite={3}",
                        item.getPackageName(), targetPath, exists, overwrite);
                if (StringUtils.hasLength(compressedPath)) {
                    message += "\n\t";
                    message += FormatUtils.format("COMPRESSED File '{0}' into '{1}'", targetPath, compressedPath);
                }
            }
        }
        return message;
    }

    private boolean isDeployable(final String item) {
        final String ext = PathUtils.getFilenameExtension(item, true);
        return !CollectionUtils.contains(EXCLUDE, ext);
    }

    private boolean isOverwritable(final String item) {
        if (!_overwriteAll) {
            if (!CollectionUtils.isEmpty(_overwriteItems)) {
                final String name = PathUtils.getFilename(item, true);
                for (final String pattern : _overwriteItems) {
                    final String regex = pattern.replaceAll("\\*", ".*");
                    if (this.match(name, regex)) {
                        return true;
                    }
                }
            }
        }
        return _overwriteAll;
    }

    private boolean isCompilable(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return _compilableFiles.contains(ext);
    }

    private InputStream read(final String packagename) throws FileNotFoundException {
        return ClassLoaderUtils.getResourceAsStream(packagename);
    }

    private byte[] compile(final byte[] text) throws UnsupportedEncodingException {
        String result = new String(text);
        if (StringUtils.hasText(result)) {
            final Set<String> keys = _compilableValues.keySet();
            for (final String key : keys) {
                if (result.contains(key)) {
                    result = StringUtils.replace(result, key, _compilableValues.get(key));
                }
            }
        }
        return result.getBytes(CharEncoding.getDefault());
    }

    /*private String compress(final String sourcePath, final byte[] data) {
        final String targetPath = getMiniFilename(sourcePath);
        if (StringUtils.hasText(targetPath)) {
            try {
                final Compressor compressor = new Compressor();
                compressor.compress(new String(data), targetPath);
                return targetPath;
            } catch (Throwable t) {
                this.getLogger().log(Level.SEVERE,
                        StringUtils.format("PROBLEM COMPRESSING '{0}': {1}", sourcePath, t),
                        t);
            }
        }
        return "";
    }*/

    private void loadResources(final String startFolder) {
        _resources.clear();
        try {
            final String root = this.getRootFullPath();
            final String folder = PathUtils.concat(root, startFolder);

            this.logInfo("LOADING resources from Root: '{0}', "
                    + "Folder: '{1}'",
                    root, folder);

            final String[] resources;
            if (PathUtils.isJar(folder)) {
                resources = this.getResourcesFromJar(folder);
            } else {
                resources = this.getResourcesFromRepository(folder);
            }
            for (final String child : resources) {
                _resources.add(new FileItem(this, root, child));
            }


            this.logInfo("Created FileDeployer '{0}'. Resources: {1}",
                    this.getClass().getSimpleName(), _resources.size());

        } catch (Throwable t) {
            this.getLogger().severe(FormatUtils.format("Unable to Create FileDeployer: {0}", t));
        }
    }

    private boolean match(String text, String pattern) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        return m.find();
    }

    private String getRootFullPath() {
        final URL url = ClassLoaderUtils.getResource(null, this.getClass(), "");
        return null != url ? url.getPath() : "";
    }

    private String[] getResourcesFromRepository(final String path) throws IOException {
        final Set<String> result = new HashSet<String>();
        final FileRepository repository = new FileRepository(path);
        final Resource[] children = repository.getResources(true);
        for (final Resource child : children) {
            result.add(child.getPath());
        }

        return result.toArray(new String[result.size()]);
    }

    private String[] getResourcesFromJar(final String path) throws IOException {
        /* A JAR path */
        final int jarIdx = path.indexOf("!");
        if (jarIdx == -1) {
            return new String[0];
        }
        // strin out checkpath
        final String checkpath = path.substring(jarIdx + 2);
        // strip out only the JAR file
        final String jarPath = path.substring(5, jarIdx);
        final File jarFile = new File(URLDecoder.decode(
                jarPath, CHARSET));
        if (!jarFile.exists()) {
            return new String[0];
        }
        final JarFile jar = new JarFile(jarFile);
        final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        final Set<String> resNames = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
        while (entries.hasMoreElements()) {
            final String name = entries.nextElement().getName();
            if (name.startsWith(checkpath)) { //filter according to the path
                String entry = name.substring(checkpath.length());
                if (StringUtils.hasText(entry)) {
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        //entry = entry.substring(0, checkSubdir);
                    }
                    final String resname = "jar:" + PathUtils.concat(path, entry);
                    resNames.add(resname);
                    // debug logging
                    this.getLogger().log(Level.FINER,
                            FormatUtils.format("path='{0}', name='{1}', "
                                    + "entry='{2}', resname='{3}'",
                                    path, name, entry, resname));
                }
            }
        }
        return resNames.toArray(new String[resNames.size()]);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static final List<FileDeployer> _deployers = Collections.synchronizedList(new LinkedList<FileDeployer>());

    public static void register(final FileDeployer deployer) {
        synchronized (_deployers) {
            if (!_deployers.contains(deployer)) {
                _deployers.add(deployer);
            }
        }
    }

    public static void deployAll() {
        synchronized (_deployers) {
            for (final FileDeployer deployer : _deployers) {
                deployer.deploy();
            }
            // remove deployed
            _deployers.clear();
        }
    }

    public static boolean isCompressible(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return CollectionUtils.indexOf(COMPRESS_FILES, ext) > -1;
    }

    /**
     * Returns minified file name or empty string if file is already a minified file.
     *
     * @param sourcePath Source File Name
     * @return Empty String or minified file name. If sourcePath is already a minified file, returns empty string.
     */
    public static String getMiniFilename(final String sourcePath) {
        if (isCompressible(sourcePath)) {
            final String name = PathUtils.getFilename(sourcePath, false);
            final String ext = PathUtils.getFilenameExtension(sourcePath, true);
            if (!name.endsWith(".min") && !name.endsWith("_min") && !name.endsWith("-min")) {
                return PathUtils.changeFileName(sourcePath, name.concat(SUFFIX_MIN).concat(ext));
            }
        }
        return null;
    }
}
