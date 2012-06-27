package org.smartly;

import org.json.JSONObject;
import org.smartly.commons.jsonrepository.JsonRepository;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.repository.deploy.FileDeployer;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.config.Deployer;
import org.smartly.packages.SmartlyPackageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Smartly {

    private SmartlyPackageLoader _packageLoader;

    public Smartly() {

    }

    public void setLauncherArgs(final Map<String, Object> args){
        _launcherArgs = args;
    }

    public void run(final SmartlyPackageLoader packageLoader) throws Exception {
        this.init(packageLoader);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final SmartlyPackageLoader packageLoader) throws Exception {

        // Run Smartly configuration deployer
        (new Deployer(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES))).deploy();

        //-- init package loader --//
        _packageLoader = packageLoader;

        // run all packages and flush deployers queue.
        // this allow packages to access deployed files
        _packageLoader.load();

        // packages are loaded
        // now run remaining deployers if any
        FileDeployer.deployAll();

        // set configuration last time
        Smartly._configuration = new JsonRepository(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES));

        // set loaded package names
        Smartly._packages = _packageLoader.getPackageNames();

        // notify all packages Smartly is ready.
        _packageLoader.ready();
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static String __home;
    private static ClassLoader __classLoader;
    private static String[] __langCodes;
    private static JsonRepository _configuration;
    private static Set<String> _packages; // loaded packages
    private static Map<String, Object> _launcherArgs;

    public static String getHome() {
        if (null == __home) {
            __home = System.getProperty(IConstants.SYSPROP_HOME);
            if (!StringUtils.hasText(__home)) {
                __home = ".";
            }
        }
        return __home;
    }

    public static boolean hasPackage(final String name){
        return null!=_packages?_packages.contains(name):false;
    }

    public static ClassLoader getClassLoader() {
        if (null == __classLoader) {
            __classLoader = Thread.currentThread().getContextClassLoader();
        }
        return __classLoader;
    }

    public static Map<String, Object> getLauncherArgs(){
        return _launcherArgs;
    }

    public static boolean isTestUnitMode(){
        return getLauncherArgs().containsKey("t") && (Boolean)getLauncherArgs().get("t");
    }


    public static String getCharset() {
        return CharEncoding.getDefault();
    }

    public static JsonRepository getConfiguration() {
        return _configuration;
    }

    public static JsonRepository getConfiguration(final boolean live) throws Exception {
        return live ? new JsonRepository(PathUtils.getAbsolutePath(IConstants.PATH_CONFIGFILES)) :_configuration;
    }

    public static String getConfigurationPath() {
        return getAbsolutePath(IConstants.PATH_CONFIGFILES);
    }

    public static String getAbsolutePath(final String relativePath) {
        return PathUtils.getAbsolutePath(relativePath);
    }

    public static String getLangCode() {
        return "en";
    }

    public static String[] getLanguages() {
        if (null == __langCodes) {
            final Set<String> langs = new HashSet<String>();
            final List<JSONObject> configLanguages = _configuration.getList("languages");
            for (final JSONObject lang : configLanguages) {
                langs.add(JsonWrapper.getString(lang, "code"));
            }
            __langCodes = langs.toArray(new String[langs.size()]);
        }
        return __langCodes;
    }

    public static void register(final FileDeployer deployer) {
        FileDeployer.register(deployer);
    }



}
