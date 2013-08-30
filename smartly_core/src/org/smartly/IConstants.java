package org.smartly;

public interface IConstants {

    public static final String SYSPROP_HOME = "smartly.home";
    public static final String SYSPROP_CHARSET = "smartly.charset";
    public static final String SYSPROP_USE_PROXIES = "smartly.useSystemProxies";

    public static final String USER_DIR = System.getProperty("user.dir");   // application directory
    public static final String USER_HOME = System.getProperty("user.home"); // user home directory

    /**
     * Line separator. i.e. "\n" *
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String FOLDER_SEPARATOR = "/";
    public static final String PATH_WHILDCHAR = "*";
    public static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    public static final String TOP_PATH = "..";
    public static final String CURRENT_PATH = ".";
    public static final char EXTENSION_SEPARATOR = '.';
    public static final String PLACEHOLDER_PREFIX = "{";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String NULL = "NULL";

    public static final String PATH_PACKAGES = "./packages";
    public static final String PATH_LIBRARIES = "./lib";
    public static final String PATH_LOG = "./logs";
    public static final String PATH_CONFIGFILES = "./config";
    public static final String PATH_CONFIGFILES_DATABASES = PATH_CONFIGFILES + "/databases";

    public static final String DEF_LANG = "en";
    public static final String DEF_COUNTRY = "US";

    //-- Content-Type --//
    public static final String TYPE_JSON = "application/json";
    public static final String TYPE_PNG = "image/png";
    public static final String TYPE_TEXT = "text/plain;charset=UTF-8";
    public static final String TYPE_HTML = "text/html;charset=UTF-8";
    public static final String TYPE_ZIP = "application/zip";
    public static final String TYPE_XML = "application/xml";
    public static final String TYPE_ALL ="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
}
