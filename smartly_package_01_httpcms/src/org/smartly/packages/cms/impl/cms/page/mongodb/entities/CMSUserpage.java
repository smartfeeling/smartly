package org.smartly.packages.cms.impl.cms.page.mongodb.entities;

import com.mongodb.DBObject;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.List;

/**
 * Userpage.
 * Users can create pages and every page is positioned into this collection.
 */
public class CMSUserpage extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String COLLECTION = "userpages";

    public static final String TEMPLATE = "template";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String DESCRIPTION = "description";
    public static final String KEYWORDS = "keywords";
    public static final String LOGO = "logo";
    public static final String DATA = "data";
    public static final String EXCERPT = "excerpt";
    public static final String CONTENT = "content";

    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public CMSUserpage() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        CMSUserpage.setId(this, MongoUtils.createUUID());
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getId(final DBObject item) {
        return MongoUtils.getString(item, ID);
    }

    public static void setId(final DBObject item, final String value) {
        MongoUtils.put(item, ID, value);
    }

    public static String getTemplate(final DBObject item, final String lang) {
        final String field = TEMPLATE;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }

    public static void setTemplate(final DBObject item, final String lang, final String value) {
        final String field = TEMPLATE;
        final String key = StringUtils.hasText(lang) ? field.concat("_").concat(lang) : field;
        MongoUtils.put(item, key, value);
    }

    public static String getTitle(final DBObject item, final String lang) {
        final String field = TITLE;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }

    public static void setTitle(final DBObject item, final String lang, final String value) {
        final String field = TITLE;
        final String key = StringUtils.hasText(lang) ? field.concat("_").concat(lang) : field;
        MongoUtils.put(item, key, value);
    }

    public static void setLogo(final DBObject item, final String lang, final String value) {
        final String field = SUBTITLE;
        final String key = StringUtils.hasText(lang) ? field.concat("_").concat(lang) : field;
        MongoUtils.put(item, key, value);
    }

    public static String getSubtitle(final DBObject item, final String lang) {
        final String field = SUBTITLE;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }

    public static String getDescription(final DBObject item, final String lang) {
        final String field = DESCRIPTION;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }

    public static void setDescription(final DBObject item, final String lang, final String value) {
        final String field = DESCRIPTION;
        final String key = StringUtils.hasText(lang) ? field.concat("_").concat(lang) : field;
        MongoUtils.put(item, key, value);
    }

    public static String getLogo(final DBObject item, final String lang) {
        final String field = LOGO;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }


    public static void setSubtitle(final DBObject item, final String lang, final String value) {
        final String field = LOGO;
        final String key = StringUtils.hasText(lang) ? field.concat("_").concat(lang) : field;
        MongoUtils.put(item, key, value);
    }


}
