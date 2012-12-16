package org.smartly.commons.io.repository.deploy;

import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper for file deployer settings
 */
public class FileDeployerSettings {

    private static final String SUFFIX_MIN = ".mini";

    private final Map<String, String> _compileFiles = new HashMap<String, String>(); // pair: source ext, target ext. i.e. ".less", ".css"
    private final Set<String> _compressFiles = new HashSet<String>();
    private final Set<String> _excludeFiles = new HashSet<String>();
    private final Set<String> _preprocessorFiles = new HashSet<String>(); // Arrays.asList(PREPROCESS_FILES)
    private final Map<String, String> _preprocessorValues = new HashMap<String, String>();

    public FileDeployerSettings() {

    }

    public FileDeployerSettings(final FileDeployerSettings parent) {
        if (null != parent) {
            _compileFiles.putAll(parent.getCompileFiles());
            _compressFiles.addAll(parent.getCompressFiles());
            _excludeFiles.addAll(parent.getExcludeFiles());
            _preprocessorFiles.addAll(parent.getPreProcessorFiles());
            _preprocessorValues.putAll(parent.getPreprocessorValues());
        }
    }

    public void clear() {
        _compileFiles.clear();
        _excludeFiles.clear();
        _compressFiles.clear();
        _preprocessorFiles.clear();
        _preprocessorValues.clear();
    }

    public Set<String> getExcludeFiles() {
        return _excludeFiles;
    }

    public Set<String> getPreProcessorFiles() {
        return _preprocessorFiles;
    }

    public Map<String, String> getPreprocessorValues() {
        return _preprocessorValues;
    }

    public Set<String> getCompressFiles() {
        return _compressFiles;
    }

    public Map<String, String> getCompileFiles() {
        return _compileFiles;
    }

    public boolean isExcluded(final String file_or_ext) {
        final String ext = PathUtils.getFilenameExtension(file_or_ext, true);
        return StringUtils.hasText(ext)
                ? _excludeFiles.contains(ext) || _excludeFiles.contains(file_or_ext)
                : _excludeFiles.contains(file_or_ext);
    }

    public boolean isPreProcessableFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isPreProcessableExt(ext);
    }

    public boolean isPreProcessableExt(final String ext) {
        return _preprocessorFiles.contains(ext);
    }


    public boolean isCompilableFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isCompilableExt(ext);
    }

    public boolean isCompilableExt(final String ext) {
        return _compileFiles.containsKey(ext);
    }

    public boolean isCompressibleFile(final String filename) {
        final String ext = PathUtils.getFilenameExtension(filename, true);
        return isCompressibleExt(ext);
    }

    public boolean isCompressibleExt(final String ext) {
        return _compressFiles.contains(ext);
    }


    public String getMiniFilename(final String sourcePath) {
        if (this.isCompressibleFile(sourcePath)) {
            final String name = PathUtils.getFilename(sourcePath, false);
            final String ext = PathUtils.getFilenameExtension(sourcePath, true);
            if (!name.endsWith(".min") && !name.endsWith("_min") && !name.endsWith("-min")) {
                return PathUtils.changeFileName(sourcePath, name.concat(SUFFIX_MIN).concat(ext));
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
