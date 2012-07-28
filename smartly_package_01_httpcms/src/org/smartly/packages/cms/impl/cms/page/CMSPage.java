package org.smartly.packages.cms.impl.cms.page;

import com.mongodb.DBObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSUserpage;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.Collection;

/**
 * Wrapper of CMSUserpage.
 * This wrapper is also added at velocity context as "$page".
 */
public class CMSPage {

    private final DBObject _dbpage;
    private final JSONObject _jsonpage;
    private final String _reqLang;

    public CMSPage(final String lang, final DBObject page) {
        _reqLang = lang;
        _dbpage = page;
        _jsonpage = JsonWrapper.wrap(page.toString()).getJSONObject();
    }

    public DBObject getObject(){
        return _dbpage;
    }

    public JSONObject getJson(){
        return _jsonpage;
    }

    public Object get(final DBObject item, final String property) {
        return MongoUtils.get(item, property);
    }

    public Object get(final JSONObject item, final String property) {
        return JsonWrapper.get(item, property);
    }

    public Object get(final DBObject item, final String lang, final String property) {
        Object result = MongoUtils.get(item, property.concat("_").concat(StringUtils.hasText(lang) ? lang : _reqLang));
        if (isEmpty(result)) {
            result = MongoUtils.get(item, property);
        }
        return null != result ? result : "";
    }

    public Object get(final JSONObject item, final String lang, final String property) {
        Object result = JsonWrapper.get(item, property.concat("_").concat(StringUtils.hasText(lang) ? lang : _reqLang));
        if (isEmpty(result)) {
            result = JsonWrapper.get(item, property);
        }
        return null != result ? result : "";
    }

    public Object getTitle(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.TITLE);
    }

    public Object getSubtitle(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.SUBTITLE);
    }

    public Object getDescription(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.DESCRIPTION);
    }

    public Object getKeywords(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.KEYWORDS);
    }

    public Object getLogo(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.LOGO);
    }

    public Object getData(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.DATA);
    }

    public Object getExcerpt(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.EXCERPT);
    }

    public Object getContent(final String lang) {
        return this.get(_dbpage, lang, CMSUserpage.CONTENT);
    }

    public Object getSections() {
        return get(_dbpage, CMSUserpage.SECTIONS);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static boolean isEmpty(final Object o) {
        try {
            if (null == o) {
                return true;
            }
            if (o instanceof String) {
                return !StringUtils.hasText((String) o);
            }
            if (o instanceof JSONObject) {
                return ((JSONObject) o).length() == 0;
            }
            if (o instanceof JSONArray) {
                return ((JSONArray) o).length() == 0;
            }
            if (o instanceof DBObject) {
                return ((DBObject) o).keySet().isEmpty();
            }
            if (o instanceof Collection) {
                return ((Collection) o).isEmpty();
            }
            if (o.getClass().isArray()) {
                return ((Object[]) o).length == 0;
            }
        } catch (Throwable t) {
            return true;
        }
        return false;
    }

}
