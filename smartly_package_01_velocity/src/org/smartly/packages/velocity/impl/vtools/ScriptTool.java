package org.smartly.packages.velocity.impl.vtools;

import org.smartly.Smartly;
import org.smartly.commons.io.repository.deploy.FileDeployer;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.util.Collection;

/**
 * Script utilities.
 * This methods are useful in javascript or html files.
 */
public class ScriptTool {

    public static final String NAME = "script";


    public ScriptTool() {

    }

    public String resource(final String filename){
        final boolean debug = isDebug();
        return geRealFileName(debug, filename);
    }

    public String require(final String filename) {
        return require(isDebug(), filename);
    }

    public String require(final boolean debug, final String filename) {
        try {
            final String ext = PathUtils.getFilenameExtension(filename, true);
            if (".js".equalsIgnoreCase(ext)) {
                return requireJs(debug, filename);
            } else if (".css".equalsIgnoreCase(ext)) {
                return requireCss(debug, filename);
            }
        } catch (Throwable ignored) {
        }
        return "";
    }

    public String requireJs(final String filename) {
        return requireJs(isDebug(), filename);
    }

    public String requireJs(final boolean debug, final String filename) {
        try {
            final String name = geRealFileName(debug, filename);
            return FormatUtils.format("<script type=\"text/javascript\" src=\"{0}\"></script>", name);
        } catch (Throwable ignored) {
        }
        return "";
    }

    public String requireJs(final String filenameDebug, final String filenameMini) {
        try {
            final String name = isDebug()?filenameDebug:filenameMini;
            return FormatUtils.format("<script type=\"text/javascript\" src=\"{0}\"></script>", name);
        } catch (Throwable ignored) {
        }
        return "";
    }

    /**
     * Returns a script that load a javascript library checking for modern browsers.<br/>
     * Useful for IE support.<br>
     *     i.e. <code>$script.requireJsCond("/js/lib/zepto/zepto.min.js", "/js/lib/jquery/jquery-1.7.2.min.js")</code>
     * @param modern i.e. "zepto.js"
     * @param legacy i.e. "jquery.js"
     * @return script
     */
    public String requireJsCond (final String modern, final String legacy) {
        return requireJsCond(isDebug(), modern, legacy);
    }

    public String requireJsCond (final boolean debug, final String modern, final String legacy) {
        try {
            final String name1 = geRealFileName(debug, modern);
            final String name2 = geRealFileName(debug, legacy);
            return FormatUtils.format("<script>\n" +
                    "document.write('<script src=' +\n" +
                    "('__proto__' in {} ? '{0}' : '{1}') +\n" +
                    "'><\\/script>')\n" +
                    "</script>", name1, name2);
        } catch (Throwable ignored) {
        }
        return "";
    }

    public String requireCss(final String filename) {
        return requireCss(isDebug(), filename);
    }

    public String requireCss(final boolean debug, final String filename) {
        try {
            final String name = geRealFileName(debug, filename);
            return FormatUtils.format("<link rel=\"stylesheet\" href=\"{0}\"/>", name);
        } catch (Throwable ignored) {
        }
        return "";
    }

    /**
     * Returns javascript array from java array or collection
     *
     * @param obj Array or Collection
     * @return javascript array
     */
    public String toArray(final Object obj) {
        if (null != obj) {
            final StringBuilder sb = new StringBuilder();
            if (obj.getClass().isArray()) {
                for (final Object item : (Object[]) obj) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append("'").append(null != item ? item.toString() : "").append("'");
                }
            } else if (obj instanceof Collection) {
                for (final Object item : (Collection) obj) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append("'").append(null != item ? item.toString() : "").append("'");
                }
            }
            return "[".concat(sb.toString()).concat("]");
        }
        return "[]";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isDebug() {
        return Smartly.isDebugMode();
    }

    private static String geRealFileName(final boolean debug, final String filename){
        final String min_name = FileDeployer.getMiniFilename(filename);
        return !debug ? null!=min_name?min_name:filename : filename;
    }
}
