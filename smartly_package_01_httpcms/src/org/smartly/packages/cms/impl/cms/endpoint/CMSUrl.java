package org.smartly.packages.cms.impl.cms.endpoint;

import org.smartly.commons.cryptograph.MD5;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Url wrapper
 */
public final class CMSUrl {

    private final String _path;
    private String _id;
    private String _path_id;
    private String[] _path_params;
    private int _countParams;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public CMSUrl(final String path) {
        _path = path;
        _countParams = 0;
        this.parse(path);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CMSUrl) {
            final CMSUrl url = (CMSUrl) obj;
            return super.equals(obj) ||
                    (this.getId().equalsIgnoreCase(url.getId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 9 + this.getId().hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("{");
        sb.append("id=").append(_id);
        sb.append(", ");
        sb.append("pathId=").append(_path_id);
        sb.append(", ");
        sb.append("path=").append(_path);
        sb.append(", ");
        sb.append("params=").append(_countParams);
        sb.append("}");

        return sb.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getPath() {
        return _path;
    }

    public String getId() {
        return _id;
    }

    public boolean hasParams() {
        return _countParams > 0;
    }

    public int countParams() {
        return _countParams;
    }

    public Map<String, String> getParams(final String path) {
        if (this.hasParams()) {
            return pluckParams(path, _path_params);
        }
        return new HashMap<String, String>();
    }

    public boolean match(final String url) {
        final String[] check_tokens = StringUtils.split(url, "/");
        final String[] path_tokens = StringUtils.split(_path, "/");
        if (check_tokens.length != path_tokens.length) {
            return false;
        }
        for (int i = 0; i < check_tokens.length; i++) {
            if (!check_tokens[i].equalsIgnoreCase(path_tokens[i]) && !isParam(path_tokens[i])) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void parse(final String url) {
        // set path id
        _path_id = getPathId(_path);
        // set method id
        _id = MD5.encode(null != _path_id ? _path_id : "");

        _path_params = getPathParams(_path);
        _countParams = 0;
        for (final String val : _path_params) {
            if (StringUtils.hasText(val)) {
                _countParams++;
            }
        }
    }

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    public static boolean hasParams(final String url) {
        return getPathParams(url).length > 0;
    }

    /**
     * From "/folder1/{id}/folder2" to "folder1.*.folder2"
     *
     * @param path Original path. i.e. "/folder1/{id}/folder2"
     * @return "folder1.*.folder2"
     */
    private static String getPathId(final String path) {
        final StringBuilder sb = new StringBuilder();
        final String[] tokens = StringUtils.split(path, "/");
        for (final String t : tokens) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            if (isParam(t)) {
                sb.append("*");
            } else {
                sb.append(t);
            }
        }
        return sb.toString();
    }

    private static boolean isParam(final String path_token) {
        return (path_token.trim().startsWith("{"));
    }

    private static String[] getPathParams(final String path) {
        final List<String> result = new LinkedList<String>();
        final String[] tokens = StringUtils.split(path, "/");
        for (final String t : tokens) {
            if (isParam(t)) {
                result.add(t.substring(t.indexOf("{") + 1, t.indexOf("}")));
            } else {
                result.add(""); // empty space
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private static Map<String, String> pluckParams(final String path, final String[] path_params) {
        final String[] tokens = StringUtils.split(path, "/");
        final Map<String, String> result = new HashMap<String, String>();
        if (tokens.length > 0 && tokens.length == path_params.length) {
            for (int i = 0; i < path_params.length; i++) {
                if (StringUtils.hasText(path_params[i])) {
                    result.put(path_params[i], tokens[i]);
                }
            }
        }
        return result;
    }
}
