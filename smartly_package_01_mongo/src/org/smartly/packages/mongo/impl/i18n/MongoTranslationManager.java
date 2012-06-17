/*
 * 
 */
package org.smartly.packages.mongo.impl.i18n;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.IMongoConstants;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.impl.GenericMongoService;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author angelo.geminiani
 */
public class MongoTranslationManager {

    private final DB _db;
    private final String _collName;
    private final String[] _langCodes;
    private final Map<String, AbstractMongoService> _services;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoTranslationManager(final DB db,
                                   final String collection, final String[] langCodes) {
        _db = db;
        _collName = collection;
        _langCodes = langCodes;
        _services = Collections.synchronizedMap(new HashMap<String, AbstractMongoService>());
    }
    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public final Object get(final Object entityid,
                            final String fieldName, final String lang) {
        Object result = null;
        try {
            final String id = MongoTranslation.getCompoundId(entityid,
                    fieldName);
            result = this.get(lang, id);
        } catch (Throwable t) {
        }
        return result;
    }

    public final Object getAsString(final Object entityid,
                                    final String fieldName, final String lang) {
        final Object result = this.get(entityid, fieldName, lang);
        return StringUtils.toString(result, null);
    }

    public final List getAsList(final Object entityid,
                                final String fieldName, final String lang) {
        final Object result = this.get(entityid, fieldName, lang);
        if (result instanceof List) {
            return (List) result;
        }
        return null;
    }

    public final List<String> getTranslatedFieldIds(final String lang,
                                                    final String filterText) {
        try {
            final AbstractMongoService srvc = this.getService(lang);
            final DBObject fTranslations = new BasicDBObject();
            final Pattern pattern = Pattern.compile("^.*".concat(filterText).concat(".*$"), Pattern.CASE_INSENSITIVE);
            fTranslations.put(MongoTranslation.KEYWORDS, pattern);
            return srvc.distinct(MongoTranslation.FIELD_ID, fTranslations);
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, ex.getMessage());
        }
        return new ArrayList<String>();
    }

    public final boolean add(final Object entityid,
                             final String fieldName, final String lang, final Object value) {
        if (null != entityid && StringUtils.hasText(lang)
                && StringUtils.hasText(fieldName)
                && null != value) {
            try {
                final MongoTranslation item = new MongoTranslation(entityid,
                        fieldName);
                item.setValue(value);
                //-- add keywords --//
                if (value instanceof String && value.toString().length() < 255) {
                    final String[] keywords = StringUtils.split((String) value,
                            new String[]{" ", "&nbsp;", "&#32;"},
                            true, true, 3);
                    item.setKeywords(StringUtils.toString(keywords, " ", "", 100));
                }

                this.upsert(lang, item);
                return true;
            } catch (Throwable t) {
            }
        }
        return false;
    }

    public final boolean remove(final Object entityid,
                                final String fieldName, final String lang) {
        try {
            final String id = MongoTranslation.getCompoundId(entityid, fieldName);
            this.removeOne(lang, id);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public final int removeAll() {
        int counter = 0;
        try {
            for (final String lang : _langCodes) {
                counter += this.remove(lang);
            }
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, "Error removing translations: "
                    + ex, ex);
        }
        return counter;
    }

    public final int removeAll(final Object entityid) {
        int counter = 0;
        try {
            for (final String lang : _langCodes) {
                final DBObject filter = new BasicDBObject();
                filter.put(MongoTranslation.FIELD_ID, entityid);
                counter += this.remove(lang, filter);
            }
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, "Error removing translations: "
                    + ex, ex);
        }
        return counter;
    }

    public Map<String, List<DBObject>> find(final DBObject filter) throws StandardCodedException {
        final Map<String, List<DBObject>> result = new HashMap<String, List<DBObject>>();
        for (final String lang : _langCodes) {
            final List<DBObject> values = this.find(lang, filter);
            if (!CollectionUtils.isEmpty(values)) {
                result.put(lang, values);
            }
        }
        return result;
    }

    public List<DBObject> find(final String lang, final DBObject filter) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            return srvc.find(filter);
        }
        return null;
    }

    public int remove(final String lang) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            return srvc.removeAll();
        }
        return 0;
    }

    public int remove(final String lang, final DBObject filter) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            return srvc.remove(filter);
        }
        return 0;
    }

    public DBObject findOne(final String lang, final DBObject query) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            return srvc.findOne(query);
        }
        return null;
    }

    public void removeOne(final String lang, final String id) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            srvc.removeOne(id);
        }
    }

    public int upsert(final String lang, final DBObject item) throws StandardCodedException {
        final AbstractMongoService srvc = this.getService(lang);
        if (null != srvc) {
            return srvc.upsert(item);
        }
        return 0;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private Logger getLogger() {
        return LoggingUtils.getLogger();
    }

    private String getCollectionName(final String lang) {
        final String language = this.getLanguage(lang);
        return _collName.concat("_").concat(language);
    }

    private String getLanguage(final String langCode) {
        return LocaleUtils.getLanguage(langCode);
    }

    private Object get(final String lang, final String id) throws StandardCodedException {
        final DBObject query = MongoUtils.queryEquals("_id", id);
        return this.get(lang, query);
    }

    private Object get(final String lang, final DBObject query) throws StandardCodedException {
        final DBObject item = this.findOne(lang, query);
        if (null != item) {
            return MongoUtils.get(item, IMongoConstants.VALUE);
        }
        return null;
    }

    private AbstractMongoService getService(final String lang) throws StandardCodedException {
        synchronized (_services) {
            if (_services.containsKey(lang)) {
                return _services.get(lang);
            } else {
                final String collName = this.getCollectionName(lang);
                final GenericMongoService srvc = new GenericMongoService(_db, collName, new String[0]);
                _services.put(lang, srvc);
                return srvc;
            }
        }
    }
}
