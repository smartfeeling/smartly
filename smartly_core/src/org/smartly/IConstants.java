package org.smartly;

public interface IConstants {

    public static final String SYSPROP_HOME = "smartly.home";
    public static final String SYSPROP_CHARSET = "smartly.charset";

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
}
