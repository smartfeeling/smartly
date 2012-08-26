package org.smartly.packages.cms.impl.cms.endpoint;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.cms.SmartlyHttpCms;
import org.smartly.packages.http.SmartlyHttp;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * CMS Manager.
 * <p/>
 * <p>
 * Extend SmartlyCMS for your site CMS manager.
 * </p>
 * <p/>
 * <p/>
 * Register a CMS manager calling  SmartlyHttp.registerCMSEndPoint(<code>cms-instance</code>)
 * in "ready" method of your AbstractPackage implementation.
 */
public class CMSRouter {

    public static final String CHARSET = CharEncoding.getDefault();

    private static final String FILE_HEAD = "hhead.ly";
    private static final String FILE_HEADER = "hheader.ly";
    private static final String FILE_CONTENT = "hcontent.ly";
    private static final String FILE_FOOTER = "hfooter.ly";
    private static final String FILE_SCRIPT = "hscript.ly";
    private static final String FILE_SCRIPT_DBG = "hscript_dbg.ly";
    private static final String FILE_LABELS = "labels.json";

    private final String _root;
    private final Map<String, JSONObject> _sitemap;
    private final Map<String, CMSEndPointPage> _pages;
    private final Map<String, String> _templates;
    private final List<CMSUrl> _urls;
    private CMSEndPointRepository _repo;
    private boolean _restful;

    public CMSRouter() {
        _restful = false;
        _root = getRootFullPath(this.getClass());
        _sitemap = new HashMap<String, JSONObject>();
        _pages = new HashMap<String, CMSEndPointPage>();
        _templates = new HashMap<String, String>();
        _urls = new ArrayList<CMSUrl>();
        _repo = new CMSEndPointRepository(_root);

        final JSONObject sitemap = _repo.getJSONObject("sitemap.json");
        if (null != sitemap && sitemap.length() > 0) {
            this.init(sitemap);
        }

        this.logReport();
    }

    public boolean contains(final String path) {
        if (!_restful) {
            return _sitemap.containsKey(path);
        } else {
            return null != this.getUrl(path);
        }
    }

    public CMSEndPointPage getPage(final String path) {
        if (!_restful) {
            return _pages.get(path);
        } else {
            final CMSUrl url = this.getUrl(path);
            return null != url ? _pages.get(url.getPath()) : null;
        }
    }

    public String getPageTemplate(final String path) {
        final String key;
        if (!_restful) {
            key = path;
        } else {
            final CMSUrl url = this.getUrl(path);
            key = null != url ? url.getPath() : "";
        }
        if (StringUtils.hasText(key)) {
            final JSONObject page = _sitemap.get(key);
            final String templateUrl = JsonWrapper.getString(page, "template");
            return _templates.get(templateUrl);
        }
        return "";
    }

    public Map<String, String> getRestParams(final String path){
       if(!_restful){
          return null;
       } else {
           final CMSUrl url = this.getUrl(path);
           return null!=url?url.getParams(path):null;
       }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return SmartlyHttpCms.getCMSLogger();
    }

    private void init(final JSONObject sitemap) {
        final JSONArray pages = JsonWrapper.getArray(sitemap, "pages");
        if (null != pages && pages.length() > 0) {
            for (int i = 0; i < pages.length(); i++) {
                final JSONObject page = pages.optJSONObject(i);
                if (null != page) {
                    try {
                        // url
                        final String[] urls = StringUtils.split(JsonWrapper.getString(page, "url"), ",");
                        // page
                        final String path = JsonWrapper.getString(page, "path");
                        final CMSEndPointPage sp = this.createPage(path);
                        // template
                        final String templatePath = JsonWrapper.getString(page, "template");
                        final String tpl = this.readTemplate(templatePath);

                        // add urls
                        this.initSitemapAndPages(urls, page, sp);

                        // add template
                        _templates.put(templatePath, tpl);
                    } catch (Throwable t) {
                        this.getLogger().log(Level.SEVERE, null, t);
                    }
                }
            }
        }

        //-- add CMS endpoints to Http Module --//
        SmartlyHttp.registerCMSPaths(_sitemap.keySet());
    }

    private void initSitemapAndPages(final String[] paths,
                                     final JSONObject smPage,
                                     final CMSEndPointPage epPage) {
        // add urls
        for (final String path : paths) {
            final CMSUrl url = new CMSUrl(path);
            if (!_urls.contains(url)) {
                _urls.add(url);
            }
            if(url.hasParams()){
                _restful=true;
            }
            _sitemap.put(path, smPage);
            _pages.put(path, epPage);
        }
    }

    private CMSUrl getUrl(final String path) {
        for (final CMSUrl url : _urls) {
            if (url.match(path)) {
                return url;
            }
        }
        return null;
    }

    private String readTemplate(final String path) throws IOException {
        return SmartlyHttp.readFile(path);
    }

    private CMSEndPointPage createPage(final String path) throws Exception {
        final String hhead = _repo.getString(PathUtils.concat(path, FILE_HEAD));
        final String hheader = _repo.getString(PathUtils.concat(path, FILE_HEADER));
        final String hcontent = _repo.getString(PathUtils.concat(path, FILE_CONTENT));
        final String hfooter = _repo.getString(PathUtils.concat(path, FILE_FOOTER));
        final String hscript = _repo.getString(PathUtils.concat(path, FILE_SCRIPT));
        final String hscriptdbg = _repo.getString(PathUtils.concat(path, FILE_SCRIPT_DBG));
        final JSONObject labels = _repo.getJSONObject(PathUtils.concat(path, FILE_LABELS));

        final CMSEndPointPage result = new CMSEndPointPage(path);
        result.setContent(hcontent);
        result.setFooter(hfooter);
        result.setHead(hhead);
        result.setHeader(hheader);
        result.setScript(isDebug() ? (StringUtils.hasText(hscriptdbg) ? hscriptdbg : hscript) : hscript);
        result.setLocalizations(labels);

        return result;
    }

    private void logReport() {
        final StringBuilder sb = new StringBuilder();

        sb.append("------- CMS ---------\n\t");
        sb.append("CMS Root: " + _root).append("\n\t");
        sb.append("CMS Repository Size: " + (null != _repo ? _repo.size() : 0)).append("\n\t");

        // sitemaps
        sb.append("SITE MAP:\n\t");
        final Set<String> keys = _sitemap.keySet();
        for (final String key : keys) {
            sb.append(key).append("\n\t");
        }

        if (_sitemap.size() != _pages.size()) {
            sb.append("WARNING!! - Sitemap and Pages have different lengths. ");
            sb.append("\n\t");
        }
        sb.append(_pages.size()).append(" pages and ").append(_sitemap.size()).append(" sitemap items");
        sb.append("\n\t");

        this.getLogger().info(sb.toString());
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static boolean isDebug() {
        return Smartly.isDebugMode();
    }

    private static String getRootFullPath(final Class aclass) {
        final URL url = ClassLoaderUtils.getResource(null, aclass, "");
        final String path = null != url ? url.getPath() : "";
        if (StringUtils.hasText(path) && path.indexOf(":/") > -1 && path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

}
