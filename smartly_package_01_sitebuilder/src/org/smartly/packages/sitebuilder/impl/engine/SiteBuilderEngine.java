package org.smartly.packages.sitebuilder.impl.engine;


import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.MathUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.sitebuilder.SmartlySiteBuilder;
import org.smartly.packages.sitebuilder.impl.ISiteBuilderConstants;
import org.smartly.packages.sitebuilder.impl.engine.vtool.Db;
import org.smartly.packages.sitebuilder.impl.engine.vtool.Dic;
import org.smartly.packages.velocity.impl.VLCManager;
import org.smartly.packages.velocity.impl.engine.VLCEngine;
import org.smartly.packages.velocity.impl.vtools.Formatter;
import org.smartly.packages.velocity.impl.vtools.Math;
import org.smartly.packages.velocity.impl.vtools.System;

import java.io.*;
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
        _templatePath = PathUtils.concat(_templatesRoot, _templateName);
        _templateBase = PathUtils.concat(_templatePath, ISiteBuilderConstants.PATH_TEMPLATE);
        _outputPath = PathUtils.concat(_outputRoot, _templateName);
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
        innerContext.put(Dic.NAME, new Dic(PathUtils.concat(_templatePath, ISiteBuilderConstants.PATH_DIC)));
        innerContext.put(Db.NAME, new Db(PathUtils.concat(_templatePath, ISiteBuilderConstants.PATH_DB)));
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
        final String path = PathUtils.concat(templatePath, ISiteBuilderConstants.FILE_METADATA);
        final byte[] bytes = FileUtils.copyToByteArray(new File(path));
        return JsonWrapper.wrap(new String(bytes, Smartly.getCharset()));
    }

    private int countPages(final int pageSize, final Object pageContent) {
        if (pageSize > 1 && pageContent instanceof JSONArray) {
            return MathUtils.paging(pageSize, ((JSONArray) pageContent).length());
        }
        return 1;
    }

    private List<Object> getPageData(final int index, final int size, final Object data) {
        final List<Object> result = new LinkedList<Object>();
        final int step = index*size;
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
        final String pageName = JsonWrapper.getString(page, "name");
        final String pageData = JsonWrapper.getString(page, "data");
        final int pageSize = JsonWrapper.getInt(page, "page_size");
        final String content = FileUtils.copyToString(new FileReader(PathUtils.concat(_templateBase, pageName)));
        // page name and ext
        final String path = PathUtils.concat(lang, PathUtils.getParent(pageName));
        final String name = PathUtils.getFilename(pageName, false);
        final String ext = PathUtils.getFilenameExtension(pageName, true);
        final String new_ext = ".html";
        // resolve data
        final Db db = (Db) context.get(Db.NAME);
        final Object data = db.get(pageData);
        final int countPages = this.countPages(pageSize, data);
        // add context variable: data, pagecount
        context.put(ISiteBuilderConstants.CTX_DATA, data);
        context.put(ISiteBuilderConstants.CTX_PAGECOUNT, countPages);

        //-- loop on pages to produce --//
        for (int i = 0; i < countPages; i++) {
            final int pageNr = i + 1;
            final String httpRoot = PathUtils.concat(_domain, path);
            final String linknext = pageNr == countPages ? "" : PathUtils.concat(httpRoot, name + "_" + (pageNr + 1) + ext);
            final String linkprev = pageNr == 1 ? "" : PathUtils.concat(httpRoot, name + "_" + (pageNr - 1) + ext);
            // add context variable: pagenr
            context.put(ISiteBuilderConstants.CTX_PAGENR, pageNr);
            context.put(ISiteBuilderConstants.CTX_LINKNEXT, linknext);
            context.put(ISiteBuilderConstants.CTX_LINKPREV, linkprev);
            context.put(ISiteBuilderConstants.CTX_PAGEDATA, this.getPageData(i, pageSize, data));
            
            //-- merge velocity template --//
            final String out = VLCManager.getInstance().evaluateText(_velocityEngine, pageName, content, context);

            //-- parse html and replace links with domain links --//


            // save output --//
            final String fileName = i == 0
                    ? PathUtils.concat(_outputPath, PathUtils.concat(path, name + new_ext))
                    : PathUtils.concat(_outputPath, PathUtils.concat(path, name + "_" + (pageNr) + new_ext));
            FileUtils.mkdirs(fileName);
            FileUtils.copy(out.getBytes(Smartly.getCharset()), new File(fileName));
        }
    }
}
