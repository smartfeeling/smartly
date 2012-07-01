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
import org.smartly.commons.repository.FileRepository;
import org.smartly.commons.repository.Resource;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SiteBuilderEngine {

    private final String _absoluteTemplatesRoot;
    private final String _absoluteOutputRoot;
    private final String _templateName;
    private final String _absoluteTemplatePath;
    private final String _absoluteTemplateBase; // path of templates file folder
    private final String _absoluteOutputPath;
    private JsonWrapper _metadata;
    private String _domain;
    private String[] _languages;
    private JSONObject[] _files;
    private boolean _initialized;
    private VelocityEngine _velocityEngine;
    private VelocityContext _velocityContext;

    public SiteBuilderEngine(final String templateName) {
        _absoluteTemplatesRoot = SmartlySiteBuilder.getAbsolutePathTemplates();
        _absoluteOutputRoot = SmartlySiteBuilder.getAbsolutePathOutput();
        _templateName = templateName;
        _absoluteTemplatePath = PathUtils.concat(_absoluteTemplatesRoot, _templateName);
        _absoluteTemplateBase = PathUtils.concat(_absoluteTemplatePath, ISiteBuilderConstants.PATH_TEMPLATE);
        _absoluteOutputPath = PathUtils.concat(_absoluteOutputRoot, _templateName);
        _initialized = false;

    }

    public void build() throws Exception {
        // init engine if not initialized yet
        this.init();

        //-- resolve and build pages --//
        final Set<String> pageDeployed = this.buildPages();

        //-- deploy resources, but pages --//
        this.deployFiles(pageDeployed);
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
        engine.setFileResourceLoaderPath(_absoluteTemplatePath);
        // engine
        _velocityEngine = engine.getNativeEngine();
        // context
        final VelocityContext innerContext = new VelocityContext();
        // context smartly
        innerContext.put(System.NAME, new System());
        innerContext.put(Formatter.NAME, new Formatter());
        innerContext.put(Math.NAME, new Math());
        // context builder
        innerContext.put(Dic.NAME, new Dic(PathUtils.join(_absoluteTemplatePath, ISiteBuilderConstants.PATH_DIC)));
        innerContext.put(Db.NAME, new Db(PathUtils.join(_absoluteTemplatePath, ISiteBuilderConstants.PATH_DB)));
        _velocityContext = new VelocityContext(innerContext);

        _initialized = true;
    }

    private void initMetadata() throws Exception {
        // metadata
        _metadata = getMetadata(_absoluteTemplatePath);
        _languages = JsonWrapper.toArrayOfString(_metadata.getJSONArray("languages"));
        _domain = _metadata.getString("domain");
        _files = JsonWrapper.toArrayOfJSONObject(_metadata.getJSONArray("files"));
    }


    private Set<String> buildPages() throws Exception {
        final Set<String> pageDeployed = new HashSet<String>();
        final Map<String, Object> context = new HashMap<String, Object>();
        for (final String lang : _languages) {
            // add context variable lang
            context.put(ISiteBuilderConstants.CTX_LANG, lang);
            for (final JSONObject page : _files) {
                pageDeployed.add(this.build(page, lang, new VelocityContext(context, _velocityContext)));
            }
        }
        return pageDeployed;
    }

    private String build(final JSONObject file, final String lang, final VelocityContext context) throws Exception {
        final String page_uri = JsonWrapper.getString(file, "uri");
        final String page_data = JsonWrapper.getString(file, "data");
        final int page_size = JsonWrapper.getInt(file, "size");
        final String file_name = JsonWrapper.getString(file, "file_name");
        final String content = FileUtils.copyToString(new FileReader(PathUtils.concat(_absoluteTemplateBase, page_uri)));
        // page name and ext
        final String path = PathUtils.concat(lang, PathUtils.getParent(page_uri));
        final String name = PathUtils.getFilename(page_uri, false);
        final String ext = PathUtils.getFilenameExtension(page_uri, true);
        // resolve data
        final Db db = (Db) context.get(Db.NAME);
        final Object data = db.get(page_data);
        final int countPages = countPages(page_size, data);
        // add context variable: data, pagecount
        context.put(ISiteBuilderConstants.CTX_DATA, data);
        context.put(ISiteBuilderConstants.CTX_PAGECOUNT, countPages);

        final PageHistory history = new PageHistory();

        //-- loop on pages to produce --//
        for (int i = 0; i < countPages; i++) {
            final List<Object> pageData = getPageData(i, page_size, data);
            final int pageNr = i + 1;
            final String httpRoot = PathUtils.concat(_domain, path);
            // current, prev, next page
            this.updatePageHistory(history, i, pageNr, countPages, httpRoot, file_name, path, name, ext, toList(data));
            // add context variable: pagenr
            context.put(ISiteBuilderConstants.CTX_PAGENR, pageNr);
            context.put(ISiteBuilderConstants.CTX_LINKNEXT, history.next);
            context.put(ISiteBuilderConstants.CTX_LINKPREV, history.prev);
            context.put(ISiteBuilderConstants.CTX_LINKCURRENT, history.current);
            context.put(ISiteBuilderConstants.CTX_PAGEDATA, pageData);

            //-- merge velocity template --//
            String out = VLCManager.getInstance().evaluateText(_velocityEngine, page_uri, content, context);

            //-- parse html and replace links with domain links --//
            out = updateLinks(_domain, path, out);

            // save output --//
            final String fileName = history.current;
            FileUtils.mkdirs(fileName);
            FileUtils.copy(out.getBytes(Smartly.getCharset()), new File(fileName));
        }
        return page_uri;
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
            current = PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, attrValue + ext));
            prev = pageNr == 1 ? "" : history.current;
            next = PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, attrNextValue + ext));
        } else if (pattern.startsWith("prefix:") && !CollectionUtils.isEmpty(data)) {
            final String prefix = pattern.substring(7);
            current = i == 0
                    ? PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, prefix + ext))
                    : PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, prefix + "_" + (pageNr) + ext));
            next = pageNr == countPages ? "" : PathUtils.concat(httpRoot, prefix + "_" + (pageNr + 1) + ext);
            prev = pageNr == 1 ? "" : PathUtils.concat(httpRoot, prefix + "_" + (pageNr - 1) + ext);
        } else {
            // standard progressive baseName based names
            current = i == 0
                    ? PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, baseName + ext))
                    : PathUtils.concat(_absoluteOutputPath, PathUtils.concat(path, baseName + "_" + (pageNr) + ext));
            next = pageNr == countPages ? "" : PathUtils.concat(httpRoot, baseName + "_" + (pageNr + 1) + ext);
            prev = pageNr == 1 ? "" : PathUtils.concat(httpRoot, baseName + "_" + (pageNr - 1) + ext);
        }
        history.current = current;
        history.next = next;
        history.prev = prev;
    }

    private void deployFiles(final Set<String> pageDeployed) throws IOException {
        final FileRepository templateRepo = new FileRepository(_absoluteTemplatePath);
        final Resource[] resources = templateRepo.getResources(true);
        for (final Resource resource : resources) {
            final String path = resource.getPath();
            final String relativePath = isDeployable(path, pageDeployed);
            if (StringUtils.hasText(relativePath)) {
                // can deploy file
                final String outPath = PathUtils.concat(_absoluteOutputPath, relativePath);
                FileUtils.mkdirs(outPath);
                FileUtils.copy(resource.getInputStream(), new FileOutputStream(outPath));
            }
        }
    }

    private String isDeployable(final String path, final Set<String> pageDeployed) {
        final String unixPath = PathUtils.toUnixPath(path);
        final String fileName = PathUtils.getFilename(unixPath, true);
        // not matadata
        if (ISiteBuilderConstants.FILE_METADATA.equalsIgnoreCase(fileName)) {
            return "";
        }
        final String relativePath = PathUtils.subtract(_absoluteTemplatePath, unixPath);
        final String root = PathUtils.getPathRoot(relativePath);

        // not db and dic folders
        if (ISiteBuilderConstants.PATH_DB.equalsIgnoreCase(root) ||
                ISiteBuilderConstants.PATH_DIC.equalsIgnoreCase(root)) {
            return "";
        } else if (ISiteBuilderConstants.PATH_TEMPLATE.equalsIgnoreCase(root)) {
            // /templates is deployable
            final String no_root = PathUtils.splitPathRoot(relativePath);
            if (pageDeployed.contains(no_root)) {
                return "";
            } else {
                return no_root;
            }
        }

        return "";
    }


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static JsonWrapper getMetadata(final String templatePath) throws IOException {
        final String path = PathUtils.concat(templatePath, ISiteBuilderConstants.FILE_METADATA);
        final byte[] bytes = FileUtils.copyToByteArray(new File(path));
        return JsonWrapper.wrap(new String(bytes, Smartly.getCharset()));
    }

    private static int countPages(final int pageSize, final Object pageContent) {
        if (pageSize > 0 && pageContent instanceof JSONArray) {
            return MathUtils.paging(pageSize, ((JSONArray) pageContent).length());
        }
        return 1;
    }

    private static List<Object> toList(final Object data) {
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

    private static List<Object> getPageData(final int index, final int size, final Object data) {
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

    private static String toValidFileName(final String name) {
        if (StringUtils.hasText(name)) {
            //final String noSpace = name.replaceAll(" ", "_");
            return RegExUtils.replaceNoAlphanumericChar(name);
        }
        return GUID.create(false, true);
    }

    private static String updateLinks(final String domain, final String langPath, final String html) {
        final String httpRoot = PathUtils.concat(domain, langPath);
        if (StringUtils.hasText(html)) {
            final HtmlParser parser = new HtmlParser(html);
            boolean changed = false;
            final Elements a = parser.select("a");
            if (updateLinks(httpRoot, a)) {
                changed = true;
            }
            final Elements link = parser.select("link");
            if (updateLinks(domain, link)) {
                changed = true;
            }
            final Elements script = parser.select("script");
            if (updateLinks(domain, script)) {
                changed = true;
            }
            final Elements img = parser.select("img");
            if (updateLinks(httpRoot, img)) {
                changed = true;
            }
            if (changed) {
                return parser.getDocument().toString();
            }
        }
        return html;
    }

    private static boolean updateLinks(final String httpRoot, final Elements links) {
        boolean changed = false;
        if (null != links && !links.isEmpty()) {
            for (final Element link : links) {
                final String href = link.attr("href");
                if (StringUtils.hasText(href)) {
                    link.attr("href", mergeLink(httpRoot, href));
                    changed = true;
                }
                final String src = link.attr("src");
                if (StringUtils.hasText(src)) {
                    link.attr("src", mergeLink(httpRoot, src));
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static String mergeLink(final String httpRoot, final String href) {
        final String protocol = PathUtils.getProtocol(href);
        if (StringUtils.hasText(protocol)) {
            return href;
        }
        final String path = PathUtils.concat(httpRoot, href);
        final int count = StringUtils.countOccurrencesOf(href, "../");
        return count > 0 ? PathUtils.resolve(path) : path;
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
