package org.smartly.packages.cms.impl.cms.endpoint;

import org.json.JSONObject;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.repository.FileRepository;
import org.smartly.commons.repository.Resource;
import org.smartly.commons.repository.deploy.FileItem;
import org.smartly.commons.util.*;
import org.smartly.packages.cms.SmartlyHttpCms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
public class CMSEndPointRepository {

    public static final String CHARSET = CMSRouter.CHARSET;

    private final String _root;
    private final List<FileItem> _resources;
    private final Map<String, String> _cache;
    private boolean _is_jar;
    private boolean _use_cache;

    public CMSEndPointRepository(final String root) {
        _root = root; // PathUtils.toUnixPath((new File(root)).getAbsolutePath());
        _resources = new LinkedList<FileItem>();
        _cache = Collections.synchronizedMap(new HashMap<String, String>());
        _is_jar = false;
        _use_cache = true;

        this.loadResources("");
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public int size() {
        return _resources.size();
    }

    public String getString(final String path) {
        try {
            return this.read(path);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    public String getString(final String path, final String defa) {
        try {
            return this.read(path);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    public JSONObject getJSONObject(final String path) {
        try {
            final String json = this.read(path);
            return StringUtils.hasText(json) ? new JSONObject(json) : new JSONObject();
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return SmartlyHttpCms.getCMSLogger();
    }

    private void loadResources(final String startFolder) {
        _resources.clear();
        try {
            final String folder = PathUtils.join(_root, startFolder);
            _is_jar = PathUtils.isJar(folder);
            final String[] resources;
            if (_is_jar) {
                resources = this.getResourcesFromJar(folder);
            } else {
                resources = this.getResourcesFromRepository(folder);
            }

            // add resources to internal list
            for (final String child : resources) {
                _resources.add(new FileItem(this, _root, child));
            }

        } catch (Throwable t) {
            this.getLogger().severe(FormatUtils.format("Unable to Create FileDeployer: {0}", t));
        }
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

    private String getAbsolutePath(final String path) {
        if (_is_jar) {
            if (!path.startsWith("jar:")) {
                return "jar:" + PathUtils.concat(_root, path);
            }
        }
        return PathUtils.concat(_root, path);
    }

    private FileItem getFile(final String path) {
        if (_resources.size() > 0) {
            final String fullPath = this.getAbsolutePath(path); //_is_jar ? "jar:" + PathUtils.concat(_root, path) : PathUtils.concat(_root, path);

            // this.getLogger().info("LOOKING FOR: " + fullPath);

            for (final FileItem item : _resources) {

                // this.getLogger().info("CHECKING: " + item.getAbsolutePath());

                if (fullPath.equalsIgnoreCase(item.getAbsolutePath())) {
                    return item;
                }
            }
        }
        return null;
    }

    private String read(final String path) throws Exception {
        if (_resources.size() > 0 || _cache.size() > 0) {
            if (_use_cache && _cache.containsKey(path)) {
                return _cache.get(path);
            }
            return _use_cache ? this.readSync(path) : readNoSync(path);
        }
        return null;
    }

    private String readNoSync(final String path) throws Exception {
        final FileItem item = this.getFile(path);
        if (null != item) {
            final String result;
            if (_is_jar) {
                final InputStream is = ClassLoaderUtils.getResourceAsStream(item.getPackageName());
                result = ClassLoaderUtils.getString(is, CHARSET);
            } else {
                result = new String(FileUtils.copyToByteArray(new File(item.getAbsolutePath())), CHARSET);
            }
            return result;
        }
        return null;
    }

    private String readSync(final String path) throws Exception {
        synchronized (_cache) {
            final String result = this.readNoSync(path);
            if (StringUtils.hasText(result) && _use_cache) {
                _cache.put(path, result);
            }
            return result;
        }
    }

}
