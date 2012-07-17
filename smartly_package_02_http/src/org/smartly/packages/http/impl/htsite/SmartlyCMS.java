package org.smartly.packages.http.impl.htsite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.http.SmartlyHttp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * CMS Manager.
 * <p/>
 * Register a CMS manager calling  SmartlyHttp.registerCMS(<code>cms-instance</code>)
 * in "ready" method of your AbstractPackage implementation.
 */
public class SmartlyCMS {

    public static final String CHARSET = CharEncoding.getDefault();
    private static final String DOC_ROOT = SmartlyHttp.getDocRoot();

    private final String _root;
    private final Map<String, JSONObject> _sitemap;
    private final Map<String, SmartlyCMSPage> _pages;
    private final Map<String, String> _templates;
    private SmartlyCMSRepository _repo;

    public SmartlyCMS() {
        _root = getRootFullPath(this.getClass());
        _sitemap = new HashMap<String, JSONObject>();
        _pages = new HashMap<String, SmartlyCMSPage>();
        _templates = new HashMap<String, String>();
        _repo = new SmartlyCMSRepository(_root);

        final JSONObject sitemap = _repo.getJSONObject("sitemap.json");
        if (null != sitemap && sitemap.length() > 0) {
            this.init(sitemap);
        }
    }

    public boolean contains(final String path) {
        return _sitemap.containsKey(path);
    }

    public SmartlyCMSPage getPage(final String path){
       return _pages.get(path);
    }

    public String getPageTemplate(final String path){
        final JSONObject page = _sitemap.get(path);
        final String templateUrl = JsonWrapper.getString(page, "template");
        return _templates.get(templateUrl);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init(final JSONObject sitemap) {
        final JSONArray pages = JsonWrapper.getArray(sitemap, "pages");
        if (null != pages && pages.length() > 0) {
            for (int i = 0; i < pages.length(); i++) {
                final JSONObject page = pages.optJSONObject(i);
                if (null != page) {
                    try {
                        // url
                        final String url = JsonWrapper.getString(page, "url");
                        // page
                        final String path = JsonWrapper.getString(page, "path");
                        final SmartlyCMSPage sp = this.createPage(path);
                        // template
                        final String templatePath = JsonWrapper.getString(page, "template");
                        final String tpl = this.readTemplate(templatePath);
                        // add all together if no errors
                        _sitemap.put(url, page);
                        _pages.put(url, sp);
                        _templates.put(templatePath, tpl);
                    } catch (Throwable t) {
                        this.getLogger().log(Level.SEVERE, null, t);
                    }
                }
            }
        }
    }

    private String readTemplate(final String path) throws IOException {
        final String fullPath = PathUtils.concat(DOC_ROOT, path);
        return new String(FileUtils.copyToByteArray(new File(fullPath)), CHARSET);
    }

    private SmartlyCMSPage createPage(final String path) throws Exception {
        final String hhead = _repo.getString(PathUtils.concat(path, "hhead.smrt"));
        final String hheader = _repo.getString(PathUtils.concat(path, "hheader.smrt"));
        final String hcontent = _repo.getString(PathUtils.concat(path, "hcontent.smrt"));
        final String hfooter = _repo.getString(PathUtils.concat(path, "hfooter.smrt"));
        final JSONObject labels = _repo.getJSONObject(PathUtils.concat(path, "labels.json"));

        final SmartlyCMSPage result = new SmartlyCMSPage(path);
        result.setContent(hcontent);
        result.setFooter(hfooter);
        result.setHead(hhead);
        result.setHeader(hheader);
        result.setLocalizations(labels);

        return result;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String getRootFullPath(final Class aclass) {
        final URL url = ClassLoaderUtils.getResource(null, aclass, "");
        return null != url ? url.getPath() : "";
    }

}
