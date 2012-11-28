package org.smartly.commons.io.repository.deploy;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

/**
 * @author angelo.geminiani
 */
public class FileItem {

    private String _fileName;
    private String _absolutePath;
    private String _relativePath;

    public FileItem(final Object parent,
                    final String dir,
                    final String absolutePath) {
        final String root = this.getRoot(dir, absolutePath);
        _absolutePath = PathUtils.validateFolderSeparator(absolutePath);
        try {
            if (!this.isJar(_absolutePath)) {
                _fileName = PathUtils.subtract(root, _absolutePath);
                _relativePath = PathUtils.merge(
                        PathUtils.getPackagePath(parent.getClass()),
                        _fileName);
            } else {
                // root: file:/C:/lib/BEEingX.jar!/org/sf/quickpin/htdocs/
                // absolute path: jar:file:/C:/lib/BEEingX.jar!/org/sf/bee/app/server/web/htdocs/vtlinfo.vhtml
                _fileName = PathUtils.subtract(root, _absolutePath);
                _relativePath = _absolutePath.substring(_absolutePath.indexOf(".jar!") + 6);
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String path) {
        this._fileName = path;
    }

    public String getAbsolutePath() {
        return _absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        _absolutePath = absolutePath;
    }

    public boolean isJar() {
        return this.isJar(_absolutePath);
    }

    public String getPackageName() {
        if (this.isJar()) {
            return _relativePath;
        } else {
            return _relativePath;
        }
    }

    public boolean isDirectory() {
        return !StringUtils.hasText(PathUtils.getFilenameExtension(_fileName));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private boolean isJar(final String path) {
        return PathUtils.isJar(path);
    }

    private String getRoot(final String root, final String absolutePath) {
        if (this.isJar(absolutePath)) {
            if (!root.startsWith("jar:")) {
                return "jar:".concat(root);
            }
        }
        return root;
    }
}
