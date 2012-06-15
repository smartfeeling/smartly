package org.smartly.packages;


import org.smartly.IConstants;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.repository.FileRepository;
import org.smartly.commons.repository.Resource;
import org.smartly.commons.repository.deploy.FileDeployer;
import org.smartly.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SmartlyPackageLoader {

    private final String _root;
    private final Map<String, AbstractPackage> _packages;
    private List<AbstractPackage> _sortList;
    private boolean _runned;
    private AbstractPackage _modalPackage;

    public SmartlyPackageLoader() throws IOException {
        _root = PathUtils.concat(Smartly.getHome(), IConstants.PATH_PACKAGES);
        _packages = Collections.synchronizedMap(new HashMap<String, AbstractPackage>());
        _runned = false;
        _modalPackage = null;

        // ensure dir exists
        FileUtils.mkdirs(_root);
    }

    public Set<String> getPackageNames(){
        return _packages.keySet();
    }

    /**
     * Programmatically registration of a package.
     * Usually packages are loaded from package folder, but when launched from IDE you may
     * find useful manual registration.
     *
     * @param instance Package Instance
     */
    public void register(final AbstractPackage instance) {
        if (null != instance) {
            final String key = instance.getId();
            synchronized (_packages) {
                if (!_packages.containsKey(key)) {
                    if (instance instanceof ISmartlyModalPackage) {
                        if (null != _modalPackage) {
                            final String msg = FormatUtils.format("Modal Package already registered. Only one modal " +
                                    "package is allowed. '{0}->{1}' will not be registered.",
                                    key, instance.getClass().getCanonicalName());
                            this.getLogger().log(Level.WARNING, msg);
                            return;
                        }
                        _modalPackage = instance;
                    }
                    _packages.put(key, instance);
                    // ensure directory exists
                    this.ensureExists(instance);
                    this.getLogger().info(FormatUtils.format("REGISTERED MODULE: {0}", key));
                } else {
                    final AbstractPackage existing = _packages.get(key);
                    if (!existing.getClass().getCanonicalName().equalsIgnoreCase(instance.getClass().getCanonicalName())) {
                        final String msg = FormatUtils.format("TWO PACKAGES WITH SAME ID: " +
                                "Package '{0}' already exists and is of type '{1}'. Package ID must be unique. " +
                                "You are trying to register another package with same ID but of type '{2}'.",
                                key, existing.getClass().getCanonicalName(), instance.getClass().getCanonicalName());
                        this.getLogger().log(Level.WARNING, msg);
                    }
                }
            }
        }
    }

    public void load() throws Exception {
        if (_runned) {
            return;
        }
        _runned = true;

        //-- load from repository--//
        this.loadPackages();

        //-- run all packages in order --//
        _sortList = this.sort();
        this.load(_sortList);
    }

    public void ready() {
        if (!CollectionUtils.isEmpty(_sortList)) {
            for (final AbstractPackage item : _sortList) {
                try {
                    //-- Modal package is last --//
                    if (!item.equals(_modalPackage)) {
                        item.ready();
                    }
                } catch (Throwable t) {
                    this.getLogger().log(Level.SEVERE,
                            FormatUtils.format("ERROR CALLING METHOD 'ready()' FROM PACKAGE '{0}': {1}",
                                    item.getId(), ExceptionUtils.getRealMessage(t)));
                }
            }
            if (null != _modalPackage) {
                this.getLogger().log(Level.INFO, FormatUtils.format(
                        "Smartly started [{0}]::{1} as MODAL.",
                        _modalPackage.getId(), _modalPackage.getClass().getName()));
                _modalPackage.ready();
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void loadPackages() throws IOException {
        final FileRepository repository = new FileRepository(_root);
        final Resource[] resources = repository.getResources(true);
        for (final Resource resource : resources) {
            if (null != resource) {
                this.loadPackage(resource);
            }
        }
    }

    private void loadPackage(final Resource resource) {
        try {
            final String content = resource.getContent(Smartly.getCharset());
            final JsonWrapper json = new JsonWrapper(content);
            final String name = json.optString("name");
            final String main = json.optString("main");
            if (!StringUtils.hasText(main)) {
                final String msg = FormatUtils.format(
                        "Unable to load Package '{0}': Missing 'main' attribute.",
                        name);
                this.getLogger().severe(msg);
            } else {
                this.registerPackageLauncher(name, main);
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE,
                    FormatUtils.format("Unmanaged Exception loading Packages: '{0}'", t),
                    t);
        }
    }

    private void registerPackageLauncher(final String name, final String className) {
        try {
            final Class clazz = ClassLoaderUtils.forName(className);
            if (null == clazz) {
                throw new Exception("Class not found in current loader.");
            }
            final AbstractPackage launcher = (AbstractPackage) clazz.newInstance();
            this.register(launcher);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE,
                    FormatUtils.format("Exception loading Package '{0}' from class '{1}': '{2}'", name, className, t),
                    t);
        }
    }

    private List<AbstractPackage> sort() {
        final Collection<AbstractPackage> packages = _packages.values();
        List<AbstractPackage> list = new ArrayList<AbstractPackage>(packages);
        Collections.sort(list);
        return list;
    }

    private void load(final List<AbstractPackage> list) {
        for (final AbstractPackage item : list) {
            try {
                item.load();
                this.getLogger().info(FormatUtils.format("STARTED MODULE: {0}", item.getId()));
                // flush deployers
                FileDeployer.deployAll();
            } catch (Throwable t) {
                final String msg = FormatUtils.format("Error running Package '{0}': '{1}'", item.getId(), t);
                this.getLogger().log(Level.SEVERE, msg, t);
            }
        }
    }

    private void ensureExists(final AbstractPackage pkg) {
        try {
            final String packagePath = PathUtils.concat(_root, pkg.getId());
            FileUtils.mkdirs(packagePath);
            final File packageJson = new File(PathUtils.concat(packagePath, "package.json"));
            if (!packageJson.exists()) {
                // read default
                this.copyDefault(packageJson, pkg);
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void copyDefault(final File packageJson, final AbstractPackage pkg) throws IOException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader(); //this.getClass().getClassLoader();
        final String path = PathUtils.getPackagePath(this.getClass());
        final String filePath = PathUtils.concat(path, packageJson.getName());
        final InputStream is = cl.getResourceAsStream(filePath);
        if(null!=is){
            final byte[] content = ByteUtils.getBytes(is);

            //format content with class data
            final Map<String, String> data = new HashMap<String, String>();
            data.put("name", pkg.getId());
            data.put("main", pkg.getClass().getCanonicalName());
            data.put("version", pkg.getVersion());
            data.put("description", pkg.getDescription());
            data.put("maintainer_mail", pkg.getMaintainerMail());
            data.put("maintainer_url", pkg.getMaintainerUrl());
            data.put("maintainer_name", pkg.getMaintainerName());

            final String json = FormatUtils.formatTemplate(new String(content), "<", ">", data);

            FileUtils.copy(json.getBytes(), packageJson);
        } else {
            this.getLogger().warning(FormatUtils.format("RESOURCE '{0}' not found. " +
                    "Ensure you included it in your package distribution " +
                    "(i.e. check IDE settings for Compiler Options.)", filePath));
        }
    }
}
