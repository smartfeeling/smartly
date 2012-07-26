package org.smartly.packages.cms.impl.cms.page;

import com.mongodb.DBObject;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSUserpage;
import org.smartly.packages.mongo.impl.util.MongoUtils;

/**
 * Wrapper of CMSUserpage.
 * This wrapper is also added at velocity context as "$page".
 */
public class CMSPage {

    private final DBObject _page;
    private final String _reqLang;

    public CMSPage(final String lang, final DBObject page) {
        _reqLang = lang;
        _page = page;
    }

    public Object get(final DBObject item, final String property) {
        return MongoUtils.get(item, property);
    }

    public Object get(final DBObject item, final String lang, final String property) {
        Object result = MongoUtils.get(item, property.concat("_").concat(StringUtils.hasText(lang) ? lang : _reqLang));
        if (null == result) {
            result = MongoUtils.get(item, property);
        }
        return null != result ? result : "";
    }

    public Object getTitle(final String lang) {
        return this.get(_page, lang, CMSUserpage.TITLE);
    }

    public Object getDescription(final String lang) {
        return this.get(_page, lang, CMSUserpage.DESCRIPTION);
    }

    public Object getSections() {
        return get(_page, CMSUserpage.SECTIONS);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
