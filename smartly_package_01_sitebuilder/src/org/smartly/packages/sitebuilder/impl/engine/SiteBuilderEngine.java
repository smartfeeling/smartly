package org.smartly.packages.sitebuilder.impl.engine;


import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smartly.Smartly;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.htmlparser.impl.HtmlParser;
import org.smartly.packages.sitebuilder.SmartlySiteBuilder;
import org.smartly.packages.sitebuilder.impl.ISiteBuilderConstants;
import org.smartly.packages.sitebuilder.impl.engine.vtool.Db;
import org.smartly.packages.sitebuilder.impl.engine.vtool.Dic;
import org.smartly.packages.velocity.impl.VLCManager;
import org.smartly.packages.velocity.impl.engine.VLCEngine;
import org.smartly.packages.velocity.impl.vtools.Formatter;
import org.smartly.packages.velocity.impl.vtools.Math;
import org.smartly.packages.velocity.impl.vtools.System;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SiteBuilderEngine {

    private final String _templatesRoot = SmartlySiteBuilder.getPathTemplates();
    private final String _outputRoot = SmartlySiteBuilder.getPathOutput();
    private final String _templateName;
    private final String _templatePath;
    private final String _templateBase; // path of templates file folder
    private final String _outputPath;
    private JsonWrapper _metadata;
    private String _domain;
    private String[] _languages;
    private JSONObject[] _pages;
    private boolean _initialized;
    private VelocityEngine _velocityEngine;
    private VelocityContext _velocityContext;

    public SiteBuilderEngine(final String templateName) {
        _templateName = templateName;
        _templatePath = PathUtils.join(_templatesRoot, _templateName);
        _templateBase = PathUtils.join(_templatePath, ISiteBuilderConstants.PATH_TEMPLATE);
        _outputPath = PathUtils.join(_outputRoot, _templateName);
        _initialized = false;

    }

    public void build() throws Exception {
        // init engine if not initialized yet
        this.init();

        final Map<String, Object> context = new HashMap<String, Object>();
        for (final String lang : _languages) {
            // add context variable lang
            context.put(ISiteBuilderConstants.CTX_LANG, lang);
            for (final JSONObject page : _pages) {
                this.build(page, lang, new VelocityContext(context, _velocityContext));
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() throws Exception {
        if (_initialized) {
            return;
        }
        // metadata
        this.initMetadata();

        //-- start initializing --//
        final VLCEngine engine = new VLCEngine();
        // settings
        engine.setFileResourceLoaderPath(_templatePath);
        // engine
        _velocityEngine = engine.getNativeEngine();
        // context
        final VelocityContext innerContext = new VelocityContext();
        // context smartly
        innerContext.put(System.NAME, new System());
        innerContext.put(Formatter.NAME, new Formatter());
        innerContext.put(Math.NAME, new Math());
        // context builder
        innerContext.put(Dic.NAME, new Dic(PathUtils.join(_templatePath, ISiteBuilderConstants.PATH_DIC)));
        innerContext.put(Db.NAME, new Db(PathUtils.join(_templatePath, ISiteBuilderConstants.PATH_DB)));
        _velocityContext = new VelocityContext(innerContext);

        _initialized = true;
    }

    private void initMetadata() throws Exception {
        // metadata
        _metadata = this.getMetadata(_templatePath);
        _languages = JsonWrapper.toArrayOfString(_metadata.getJSONArray("languages"));
        _domain = _metadata.getString("domain");
        _pages = JsonWrapper.toArrayOfJSONObject(_metadata.getJSONArray("pages"));
    }

    private JsonWrapper getMetadata(final String templatePath) throws IOException {
        final String path = PathUtils.join(templatePath, ISiteBuilderConstants.FILE_METADATA);
        final byte[] bytes = FileUtils.copyToByteArray(new File(path));
        return JsonWrapper.wrap(new String(bytes, Smartly.getCharset()));
    }

    private int countPages(final int pageSize, final Object pageContent) {
        if (pageSize > 0 && pageContent instanceof JSONArray) {
            return MathUtils.paging(pageSize, ((JSONArray) pageContent).length());
        }
        return 1;
    }

    private List<Object> toList(final Object data) {
        final List<Object> result = new LinkedList<Object>();
        if (data instanceof JSONArray) {
            final JSONArray array = (JSONArray) data;
            for (int i = 0; i < array.length(); i++) {
                final Object item = array.opt(i);
                if (null != item) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    private List<Object> getPageData(final int index, final int size, final Object data) {
        final List<Object> result = new LinkedList<Object>();
        final int step = index * size;
        if (data instanceof JSONArray) {
            final JSONArray array = (JSONArray) data;
            int count = 0;
            for (int i = 0; i < array.length(); i++) {
                if (i < step) {
                    continue;
                }
                if (count == size) {
                    break;
                }
                count++;
                final Object item = array.opt(i);
                if (null != item) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    private void build(final JSONObject page, final String lang, final VelocityContext context) throws Exception {
        final String page_uri = JsonWrapper.getString(page, "page_uri");
        final String page_data = JsonWrapper.getString(page, "page_data");
        final int page_size = JsonWrapper.getInt(page, "page_size");
        final String file_name = JsonWrapper.getString(page, "file_name");
        final String content = FileUtils.copyToString(new FileReader(PathUtils.join(_templateBase, page_uri)));
        // page name and ext
        final String path = PathUtils.join(lang, PathUtils.getParent(page_uri));
        final String name = PathUtils.getFilename(page_uri, false);
        final String ext = PathUtils.getFilenameExtension(page_uri, true);
        final String new_ext = ".html";
        // resolve data
        final Db db = (Db) context.get(Db.NAME);
        final Object data = db.get(page_data);
        final int countPages = this.countPages(page_size, data);
        // add context variable: data, pagecount
        context.put(ISiteBuilderConstants.CTX_DATA, data);
        context.put(ISiteBuilderConstants.CTX_PAGECOUNT, countPages);

        final PageHistory history = new PageHistory();

        //-- loop on pages to produce --//
        for (int i = 0; i < countPages; i++) {
            final List<Object> pageData = this.getPageData(i, page_size, data);
            final int pageNr = i + 1;
            final String httpRoot = PathUtils.join(_domain, path);
            // current, prev, next page
            this.updatePageHistory(history, i, pageNr, countPages, httpRoot, file_name, path, name, new_ext, this.toList(data));
            // add context variable: pagenr
            context.put(ISiteBuilderConstants.CTX_PAGENR, pageNr);
            context.put(ISiteBuilderConstants.CTX_LINKNEXT, history.next);
            context.put(ISiteBuilderConstants.CTX_LINKPREV, history.prev);
            context.put(ISiteBuilderConstants.CTX_LINKCURRENT, history.current);
            context.put(ISiteBuilderConstants.CTX_PAGEDATA, pageData);

            //-- merge velocity template --//
            String out = VLCManager.getInstance().evaluateText(_velocityEngine, page_uri, content, context);

            //-- parse html and replace links with domain links --//
            out = this.updateLinks(httpRoot, out);

            // save output --//
            final String fileName = history.current;
            FileUtils.mkdirs(fileName);
            FileUtils.copy(out.getBytes(Smartly.getCharset()), new File(fileName));
        }
    }

    /**
     * Update page history using 3 criteria:<br/>
     * <ul>
     * <li>Progression Number: [parenr] Base file name is template file name. All pages are generated adding a number suffix to base name</li>
     * <li>Prefix and Prograssion Number: [prefix:test_] Base file name is Prefix. All pages are generated adding a number suffix to base name</li>
     * <li>Attribute value of data list: [attr:name] Base file name is value of attribute of current data item. All pages are generated using only the base name.</li>
     * <ul/>
     */
    private void updatePageHistory(final PageHistory history, final int i, final int pageNr, final int countPages,
                                   final String httpRoot, final String pattern, final String path,
                                   final String baseName, final String ext, final List<Object> data) {
        String current = "";
        String next = "";
        String prev = "";
        if (pattern.startsWith("attr:") && !CollectionUtils.isEmpty(data)) {
            final String attr = pattern.substring(5);
            final String attrValue = toValidFileName(BeanUtils.getValueIfAny(data.get(i), attr).toString());
            final String attrNextValue = data.size() > pageNr + 1 ? toValidFileName(BeanUtils.getValueIfAny(data.get(i + 1), attr).toString()) : "";
            current = PathUtils.join(_outputPath, PathUtils.join(path, attrValue + ext));
            prev = pageNr == 1 ? "" : history.current;
            next = PathUtils.join(_outputPath, PathUtils.join(path, attrNextValue + ext));
        } else if (pattern.startsWith("prefix:") && !CollectionUtils.isEmpty(data)) {
            final String prefix = pattern.substring(7);
            current = i == 0
                    ? PathUtils.join(_outputPath, PathUtils.join(path, prefix + ext))
                    : PathUtils.join(_outputPath, PathUtils.join(path, prefix + "_" + (pageNr) + ext));
            next = pageNr == countPages ? "" : PathUtils.join(httpRoot, prefix + "_" + (pageNr + 1) + ext);
            prev = pageNr == 1 ? "" : PathUtils.join(httpRoot, prefix + "_" + (pageNr - 1) + ext);
        } else {
            // standard progressive baseName based names
            current = i == 0
                    ? PathUtils.join(_outputPath, PathUtils.join(path, baseName + ext))
                    : PathUtils.join(_outputPath, PathUtils.join(path, baseName + "_" + (pageNr) + ext));
            next = pageNr == countPages ? "" : PathUtils.join(httpRoot, baseName + "_" + (pageNr + 1) + ext);
            prev = pageNr == 1 ? "" : PathUtils.join(httpRoot, baseName + "_" + (pageNr - 1) + ext);
        }
        history.current = current;
        history.next = next;
        history.prev = prev;
    }

    private String toValidFileName(final String name) {
        if (StringUtils.hasText(name)) {
            //final String noSpace = name.replaceAll(" ", "_");
            return RegExUtils.replaceNoAlphanumericChar(name);
        }
        return GUID.create(false, true);
    }

    private String updateLinks(final String httpRoot, final String html) {
        if (StringUtils.hasText(html)) {
            final HtmlParser parser = new HtmlParser(html);
            final Elements links = parser.select("a");
            boolean changed = false;
            if (null != links && !links.isEmpty()) {
                for (final Element link : links) {
                    final String href = link.attr("href");
                    if (StringUtils.hasText(href)) {
                        link.attr("href", this.mergeLink(httpRoot, href));
                        changed = true;
                    }
                }
            }
            if(changed){
                return parser.getDocument().toString();
            }
        }
        return html;
    }

    private String mergeLink(final String httpRoot, final String href){
        final String path = PathUtils.concat(httpRoot, href);
        final int count = StringUtils.countOccurrencesOf(href, "../");
        return count>0?PathUtils.resolve(path):path;
    }

    // ------------------------------------------------------------------------
    //                      C L A S S
    // ------------------------------------------------------------------------

    private class PageHistory {

        String current;
        String next;
        String prev;

    }
}
