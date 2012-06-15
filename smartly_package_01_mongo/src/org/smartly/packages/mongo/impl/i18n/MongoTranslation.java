/*
 *
 */
package org.smartly.packages.mongo.impl.i18n;

import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.List;

/**
 * Translations have a compount key:
 * collection + entityid + field + lang, i.e. "users-12234-name-it_IT"
 *
 * @author angelo.geminiani
 */
public class MongoTranslation extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String FIELD_NAME = "fieldname";
    public static final String FIELD_ID = "entityid";
    // VALUE
    // public static final String KEYWORDS = "keywords";

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------

    public MongoTranslation(final Object entityId, final String field) {
        super.setId(getCompoundId(entityId, field));
        this.setEntityId(entityId.toString());
        this.setFieldName(field);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MongoTranslation other = (MongoTranslation) obj;
        if ((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public final Object getValue() {
        return MongoUtils.get(this, VALUE);
    }

    public final String getValueAsString() {
        return MongoUtils.getString(this, VALUE);
    }

    public final List getValueAsList() {
        return MongoUtils.getList(this, VALUE);
    }

    public final void setValue(final Object value) {
        if (null != value) {
            super.put(VALUE, value);
        }
    }

    public final String getKeywords() {
        return MongoUtils.getString(this, KEYWORDS);
    }

    public final void setKeywords(final String value) {
        if (null != value) {
            super.put(KEYWORDS, value);
        }
    }

    public final String getEntityId() {
        return MongoUtils.getString(this, FIELD_ID);
    }

    public final void setEntityId(final String value) {
        if (null != value) {
            super.put(FIELD_ID, value);
        }
    }

    public final String getFieldName() {
        return MongoUtils.getString(this, FIELD_NAME);
    }

    public final void setFieldName(final String value) {
        if (null != value) {
            super.put(FIELD_NAME, value);
        }
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    public static String getCompoundId(final Object entityId,
                                       final String field) {
        return MongoUtils.concatId(entityId, field);
    }


}
