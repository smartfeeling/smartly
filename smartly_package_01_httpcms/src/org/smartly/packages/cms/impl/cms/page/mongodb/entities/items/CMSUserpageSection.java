package org.smartly.packages.cms.impl.cms.page.mongodb.entities.items;

import com.mongodb.DBObject;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.List;

/**
 * Userpage.
 * Users can create pages and every page is positioned into this collection.
 */
public class CMSUserpageSection extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    private static final String TEMPLATE = "template";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";


    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public CMSUserpageSection() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        CMSUserpageSection.setId(this, MongoUtils.createUUID(6));
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

    public static String getTitle(final DBObject item, final String lang) {
        final String field = TITLE;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }

    public static String getDescription(final DBObject item, final String lang) {
        final String field = DESCRIPTION;
        final String result = MongoUtils.getString(item, field.concat("_").concat(lang));
        return StringUtils.hasText(result) ? result : MongoUtils.getString(item, field);
    }


}
