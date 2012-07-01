package org.smartly.packages.sitebuilder.impl;


public interface ISiteBuilderConstants {

    public static final String PATH_DB = "/db";
    public static final String PATH_DIC = "/dic";
    public static final String PATH_TEMPLATE = "/template";

    public static final String FILE_METADATA = "metadata.json";

    public static final String COMPILE_DOMAIN = "[DOMAIN]";
    public static final String COMPILE_SITE_NAME = "[SITE-NAME]";

    //-- velocity context variables --//
    public static final String CTX_LANG = "lang";
    public static final String CTX_DATA = "data";
    public static final String CTX_PAGEDATA = "pagedata";
    public static final String CTX_PAGECOUNT = "pagecount";
    public static final String CTX_PAGENR = "pagenr";
    public static final String CTX_LINKPREV = "linkprev";
    public static final String CTX_LINKNEXT = "linknext";
    public static final String CTX_LINKCURRENT = "linkcurrent";
}
