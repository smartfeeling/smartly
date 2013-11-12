package org.smartly.commons.io.repository.deploy;

import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.io.repository.FileRepository;
import org.smartly.commons.io.repository.Resource;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
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

    private static final String CHARSET = CharEncoding.getDefault();
    private static final String DIRECTIVE_VERSION = "[RT-VERSION]";
    private static final String DIRECTIVE_VERSION_VALUE = GUID.create();
    private static final String DIRECTIVE_DEBUG_APP = "[RT-DEBUG]";
    private static final String DIRECTIVE_DEBUG_JS = "[RT-DEBUGJS]";

    // ------------------------------------------------------------------------
    //                      Variables
    // ------------------------------------------------------------------------

    private final FileDeployerSettings _settings;
    private final String _startFolder;
    private final String _targetFolder;
    private boolean _overwrite;
    private String[] _always_overwrite_items;
    private String[] _never_overwrite_items;
    private final boolean _silent;  // completly silent
    private final boolean _verbose; // log details
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
        this(startFolder, targetFolder, false, verbose, debugApp, debugJs);
    }

    public FileDeployer(final String startFolder,
                        final String targetFolder,
                        final boolean silent,
                        final boolean verbose,
                        final boolean debugApp,
                        final boolean debugJs) {
        this.logInfo("Creating FileDeployer '{0}'. "
                + "Start Folder: '{1}', Target Folder: '{2}'",
                this.getClass().getSimpleName(), startFolder, targetFolder);

        _settings = new FileDeployerSettings(_globalSettings);
        _startFolder = startFolder;
        _targetFolder = targetFolder;
        _overwrite = false;
        _always_overwrite_items = new String[0];
        _never_overwrite_items = new String[0];
        _silent = silent;
        _verbose = verbose;
        _debugApp = debugApp;
        _debugJs = debugJs;

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------

    public FileDeployerSettings getSettings() {
        return _settings;
    }

    public String getSourceFolder() {
        return _startFolder;
    }

    public String getTargetFolder() {
        return _targetFolder;
    }

    public boolean isOverwrite() {
        return _overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        _overwrite = overwrite;
    }

    public String[] getAlwaysOverwriteItems() {
        return _always_overwrite_items;
    }

    public void setAlwaysOverwriteItems(final String[] value) {
        _always_overwrite_items = value;
    }

    public String[] getNeverOverwriteItems() {
        return _never_overwrite_items;
    }

    public void setNeverOverwriteItems(final String[] value) {
        _never_overwrite_items = value;
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
     * @param children     Deploy only content of startFolder into targetFolder
     */
    public void deploy(final String targetFolder,
                       final boolean children) {
        //-- get resources and start deploy --//
        final List<FileItem> resources = this.loadResources(_startFolder);

        this.logStart();

        for (final FileItem item : resources) {
            final String message = this.deploy(targetFolder, item, children);
            if (StringUtils.hasText(message)) {
                this.logInfo(message);
            }
        }
    }

    public abstract byte[] compress(final byte[] data, final String filename);

    public abstract byte[] compile(final byte[] data, final String filename);

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {
        _settings.getPreprocessorValues().put(DIRECTIVE_DEBUG_APP, _debugApp + "");
        _settings.getPreprocessorValues().put(DIRECTIVE_DEBUG_JS, _debugJs + "");
    }

    private void logStart() {
        if (!_silent) {
            this.getLogger().log(Level.INFO, FormatUtils.format(
                    "FILE DEPLOYER: Running FileDeployer: {0}\n" +
                            "\t Target Path: {1}, \n" +
                            "\t Overwrite Target: {2}",
                    this.getClass().getName(),
                    _targetFolder, _overwrite));
        }
    }

    private void logInfo(final String text, final Object... args) {
        if (_verbose) {
            this.getLogger().log(Level.INFO, FormatUtils.format(
                    "FILE DEPLOYER: " + text, args));
        }
    }

    private String deploy(final String targetFolder,
                          final FileItem item,
                          final boolean children) {
        String message = "";
        final String filename = item.getFileName();
        // check if file extension is not excluded
        if (this.isDeployable(filename)) {
            // targetPath is absolute file name of deployed file
            String targetPath;
            if (children) {
                targetPath = (new File(PathUtils.merge(targetFolder, PathUtils.subtract(_startFolder, filename)))).getAbsolutePath();
            } else {
                targetPath = (new File(PathUtils.merge(targetFolder, filename))).getAbsolutePath();
            }
            final String targetName = (new File(targetPath)).getName();
            final boolean exists = PathUtils.exists(targetPath); //target.exists();
            final boolean overwrite = this.isOverwritable(filename);
            final String ext = PathUtils.getFilenameExtension(filename, true);
            String compressedPath = null;
            Exception exc = null;
            int deployed = 0;
            if (!exists || overwrite) {
                if (!item.isDirectory()) {
                    try {
                        FileUtils.mkdirs(targetPath);
                        final String packagename = item.getPackageName();
                        final InputStream in = this.read(packagename);
                        if (null != in) {
                            deployed = 1;
                            byte[] binaryData = ByteUtils.getBytes(in);

                            //-- pre-process file  --//
                            if (_settings.isPreProcessableExt(ext)) {
                                // pre-process
                                binaryData = this.preProcess(binaryData);
                            }

                            //-- compile file  --//
                            if (_settings.isCompilableExt(ext)) {
                                // compile
                                final byte[] compiledData = this.compile(binaryData, targetName);
                                if (null != compiledData && compiledData.length > 0) {
                                    // replace data with compiled data
                                    binaryData = compiledData;
                                    final String outExt = _settings.getCompileFiles().get(ext);
                                    if (StringUtils.hasText(outExt) && !outExt.equalsIgnoreCase(ext)) {
                                        // change target file name
                                        targetPath = PathUtils.changeFileExtension(targetPath, outExt);
                                    }
                                }
                            }

                            //-- deploy file --//
                            FileUtils.copy(binaryData, new File(targetPath));

                            //-- compress file --//
                            if (_settings.isCompressibleExt(ext(targetPath))) {
                                // creates new minified file
                                final byte[] compressedData = this.compress(binaryData, targetPath);
                                if (null != compressedData && compressedData.length > 0) {
                                    compressedPath = _settings.getMiniFilename(targetPath);
                                    FileUtils.copy(compressedData, new File(compressedPath));
                                }
                            }

                            try {
                                in.close();
                            } catch (Throwable ignored) {
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

    private String ext(final String file) {
        return PathUtils.getFilenameExtension(file, true);
    }

    private boolean isDeployable(final String item) {
        return !_settings.isExcluded(item);
    }

    private boolean isOverwritable(final String item) {
        if (!_overwrite) {
            if (!CollectionUtils.isEmpty(_always_overwrite_items)) {
                final String name = PathUtils.getFilename(item, true);
                for (final String pattern : _always_overwrite_items) {
                    final String regex = pattern.replaceAll("\\*", ".*");
                    if (this.match(name, regex)) {
                        return true;
                    }
                }
            }
        }

        //-- never overwrite this items --//
        if (!CollectionUtils.isEmpty(_never_overwrite_items)) {
            final String name = PathUtils.getFilename(item, true);
            for (final String pattern : _never_overwrite_items) {
                final String regex = pattern.replaceAll("\\*", ".*");
                if (this.match(name, regex)) {
                    return false;
                }
            }
        }

        return _overwrite;
    }

    private InputStream read(final String packagename) throws FileNotFoundException {
        return ClassLoaderUtils.getResourceAsStream(packagename);
    }

    private byte[] preProcess(final byte[] text) throws UnsupportedEncodingException {
        String result = new String(text);
        if (StringUtils.hasText(result)) {
            final Set<String> keys = _settings.getPreprocessorValues().keySet();
            for (final String key : keys) {
                if (result.contains(key)) {
                    result = StringUtils.replace(result, key, _settings.getPreprocessorValues().get(key));
                }
            }
        }
        return result.getBytes(CharEncoding.getDefault());
    }

    private List<FileItem> loadResources(final String startFolder) {
        final List<FileItem> result = new LinkedList<FileItem>();
        try {
            final String root = this.getRootFullPath();
            final String folder = PathUtils.join(root, startFolder);

            this.logInfo("LOADING resources from Root: '{0}', "
                    + "Folder: '{1}'",
                    root, folder);

            //-- get children names --//
            final String[] children;
            if (PathUtils.isJar(folder)) {
                children = this.getResourcesFromJar(folder);
            } else {
                children = this.getResourcesFromRepository(folder);
            }

            //-- creates resource and add to list --//
            for (final String child : children) {
                result.add(new FileItem(this, root, child));
            }

            this.logInfo("Created FileDeployer '{0}'. Resources: {1}",
                    this.getClass().getSimpleName(), result.size());

        } catch (Throwable t) {
            this.getLogger().severe(FormatUtils.format("Unable to Create FileDeployer: {0}", t));
        }

        return result;
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
                    final String resname = "jar:" + PathUtils.join(path, entry);
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
    //-- global settings --//
    private static final FileDeployerSettings _globalSettings = new FileDeployerSettings();

    {
        //-- init pre-processor values --//
        _globalSettings.getPreprocessorValues().put(DIRECTIVE_VERSION, DIRECTIVE_VERSION_VALUE);
        //-- add exclusions --//
        _globalSettings.getExcludeFiles().add(".class");
    }

    public static FileDeployerSettings getGlobalSettings() {
        return _globalSettings;
    }

    public static Set<String> getPreProcessorFiles() {
        return _globalSettings.getPreProcessorFiles();
    }

    public static Map<String, String> getPreprocessorValues() {
        return _globalSettings.getPreprocessorValues();
    }

    public static Set<String> getCompressFiles() {
        return _globalSettings.getCompressFiles();
    }

    public static Map<String, String> getCompileFiles() {
        return _globalSettings.getCompileFiles();
    }

    /**
     * Returns minified file name or empty string if file is already a minified file.
     *
     * @param sourcePath Source File Name
     * @return Empty String or minified file name. If sourcePath is already a minified file, returns empty string.
     */
    public static String getMiniFilename(final String sourcePath) {
        return getGlobalSettings().getMiniFilename(sourcePath);
    }

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


}
