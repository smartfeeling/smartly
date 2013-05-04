/*
 * 
 */
package org.smartly.packages.mongo.impl;

import com.mongodb.*;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.mongo.impl.i18n.MongoTranslationManager;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author angelo.geminiani
 */
public abstract class AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    private static final String _ID = IMongoConstants.ID;
    private static final String MODIFIER_INC = "$inc";
    private static final int EARTH_RADIUS_mt = 6378160; // earth radius in mt.
    private static final String LOCALE_BASE_FIELD = IMongoConstants.LANG_BASE; // used in embedded localizations. i.e. "description":{"base":"hello", "it":"ciao"}
    private static final String WILDCHAR = IMongoConstants.WILDCHAR;
    // ------------------------------------------------------------------------
    //                      variables
    // ------------------------------------------------------------------------
    private final DB _db;
    private final String _collName;
    private final DBCollection _coll;
    private final String[] _langCodes; // used from translation manager
    private final MongoTranslationManager _tm;
    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------

    public AbstractMongoService(final DB db, final String collName,
                                final String[] langCodes) {
        _db = db;
        _collName = collName;
        _coll = _db.getCollection(_collName);
        _langCodes = null != langCodes ? langCodes : new String[0];
        _tm = new MongoTranslationManager(db, collName, langCodes);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    // <editor-fold defaultstate="collapsed" desc=" Languages, Collection and DB">
    public final String getName() {
        return null != _coll ? _coll.getName() : "";
    }

    public final String getFullName() {
        return null != _coll ? _coll.getFullName() : "";
    }

    public final DBCollection getCollection() {
        return _coll;
    }

    public final String getCollectionName() {
        return _collName;
    }

    /**
     * Returns supported languages
     *
     * @return
     */
    public final String[] getLanguages() {
        return _langCodes;
    }

    protected final DB getDb() {
        return _db;
    }

    public void drop() {
        if (null != _coll) {
            _coll.drop();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" INDEXES">
    public List<DBObject> getIndexes() {
        if (null != _coll) {
            return _coll.getIndexInfo();
        }
        return new ArrayList<DBObject>();
    }

    public int countIndexes() {
        if (null != _coll) {
            return this.getIndexes().size();
        }
        return 0;
    }

    public int dropIndexes() {
        if (null != _coll) {
            int count = 0;
            final List<DBObject> indexes = this.getIndexes();
            for (final DBObject index : indexes) {
                try {
                    final DBObject key = (DBObject) index.get("key");
                    if (null != key) {
                        if (key.containsField(_ID)) {
                            continue;
                        }
                    }
                    _coll.dropIndex(index.get("name").toString());
                    count++;
                } catch (Throwable t) {
                    this.getLogger().severe(FormatUtils.format("Unable to drop index: {0}", t));
                }
            }
            return count;
        }
        return 0;
    }

    public void ensureIndex(final DBObject index) {
        if (null != _coll) {
            _coll.ensureIndex(index);
        }
    }

    public void ensureIndex(final String fieldName, final boolean ascending) {
        if (null != _coll) {
            final DBObject index = new BasicDBObject(fieldName,
                    ascending ? 1 : -1);
            _coll.ensureIndex(index);
        }
    }

    public void ensureIndex(final String[] fieldNames,
                            final boolean ascending, final boolean unique) {
        if (null != _coll) {
            final DBObject index = new BasicDBObject();
            for (final String fieldName : fieldNames) {
                index.put(fieldName, ascending ? 1 : -1);
            }
            _coll.ensureIndex(index, null, unique);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" UPSERT (update/insert)">

    /**
     * Saves document(s) to the database.
     * if doc doesn't have an _id, one will be added
     * you can get the _id that was added from doc after the insert
     *
     * @param item document to save
     */
    public void insert(final DBObject item) throws StandardCodedException {
        if (null != _coll) {
            final WriteResult result = _coll.insert(item);
            if (StringUtils.hasText(result.getError())) {
                throw this.getError418(result.getError());
            }
        }
    }

    /**
     * Update or Insert an object and return 1 if object was updated or 0
     * if was insert.
     *
     * @param object Object to save.
     * @return 0=insert, 1=update/overwrite
     * @throws StandardCodedException
     */
    public int upsert(final DBObject object) throws StandardCodedException {
        try {
            final Object id = this.getOrCreateId(object);
            if (null != id) {
                final boolean overwritten = this.overwrite(object, id);
                return overwritten ? 1 : 0;
            } else {
                throw this.getError500("Expected a valid ID!");
            }
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }


    /**
     * Update existing document also with partial data.
     *
     * @param object
     * @return
     * @throws StandardCodedException
     */
    public DBObject merge(final DBObject object,
                          final String[] excludeProperties) throws StandardCodedException {
        try {
            final Object id = this.getId(object);
            if (null != id) {
                return this.merge(object, id, excludeProperties);
            } else {
                //-- insert new object --//
                this.upsert(object);
                return object;
            }
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    /**
     * Update existing Object. Only consistent (not null and not empty) fields are updated.
     *
     * @param object
     * @return
     * @throws StandardCodedException
     */
    public int updateFields(final DBObject object) throws StandardCodedException {
        try {
            final Object id = this.getOrCreateId(object);
            if (null != id) {
                final boolean overwritten = this.update(object, id);
                return overwritten ? 1 : 0;
            } else {
                throw this.getError500("Expected a valid ID!");
            }
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    /**
     * Update existing Object
     *
     * @param query  The filter
     * @param object Object or Operation
     * @return True if existing data has been updated
     * @throws StandardCodedException
     */
    public boolean update(final DBObject query, final DBObject object)
            throws StandardCodedException {
        return this.nativeUpdate(query, object, false, false);
    }

    /**
     * Update existing Object or insert new one if does not exists and
     * "upsert" parameter is true.
     *
     * @param query  The filter
     * @param object Object or Operation
     * @param upsert Boolean
     * @return True if existing data has been updated
     * @throws StandardCodedException
     */
    public boolean update(final DBObject query, final DBObject object,
                          final boolean upsert)
            throws StandardCodedException {
        return this.nativeUpdate(query, object, upsert, false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" REMOVE">
    public int removeAll() throws StandardCodedException {
        final List<DBObject> data = this.find();
        int counter = 0;
        for (final DBObject item : data) {
            this.removeOne(item);
            counter++;
        }

        // remove localizations
        this.removeLocalizations();

        return counter;
    }

    public int removeOne(final DBObject object) throws StandardCodedException {
        final Object id = this.getOrCreateId(object);
        return this.removeOne(id);
    }

    public int removeOne(final Object id) throws StandardCodedException {
        if (null != id) {
            final DBObject filter;
            if (id instanceof String) {
                final Pattern pattern = MongoUtils.patternEquals((String) id);
                filter = new BasicDBObject(_ID, pattern);
            } else {
                filter = new BasicDBObject(_ID, id);
            }
            return this.remove(filter);
        } else {
            throw this.getError500("Expected a valid ID!");
        }
    }

    public int remove(final DBObject filter) throws StandardCodedException {
        try {
            final WriteResult result;
            result = _coll.remove(filter);

            if (StringUtils.hasText(result.getError())) {
                throw this.getError418(result.getError());
            }
            return result.getN();
        } catch (Throwable t) {
            throw this.getError500(t);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" FIND, DISTINCT, GET">
    public boolean exists(final Object id) {
        if (null != _coll && null != id) {
            final DBObject filter = new BasicDBObject(_ID, id);
            return _coll.count(filter) > 0;
        }
        return false;
    }

    public DBObject findById(final Object id) {
        return this.findById(id, null);
    }

    public DBObject findById(final Object id, final String[] fieldNames) {
        if (null != _coll && null != id) {
            final DBObject filter;
            if (id instanceof String) {
                if (StringUtils.hasText((String) id)) {
                    final Pattern pattern = MongoUtils.patternEquals((String) id);
                    filter = new BasicDBObject(_ID, pattern);
                } else {
                    return null;
                }
            } else {
                filter = new BasicDBObject(_ID, id);
            }
            final DBObject fields = this.getFields(fieldNames, true);
            return null != fields
                    ? _coll.findOne(filter, fields)
                    : _coll.findOne(filter);
        }
        return null;
    }

    public DBObject findOne() {
        return null != _coll ? _coll.findOne() : null;
    }

    public DBObject findOne(final DBObject filter) {
        return this.findOne(filter, null);
    }

    public DBObject findOne(final DBObject filter, final String[] fieldNames) {
        final DBObject fields = this.getFields(fieldNames, true);
        return null != fields
                ? _coll.findOne(fields, fields)
                : _coll.findOne(filter);
    }

    public List<DBObject> findByIds(final List ids) {
        final DBObject query = MongoUtils.queryEquals(_ID, ids);
        return this.find(query, null, null, null);
    }

    public List<DBObject> findByIds(final List ids, final String[] fieldNames) {
        final DBObject query = MongoUtils.queryEquals(_ID, ids);
        return this.find(query, fieldNames, null, null);
    }

    public List<DBObject> find(final DBObject filter) {
        return this.find(filter, null, null, null);
    }

    public List<DBObject> find() {
        if (null != _coll) {
            final DBCursor cursor = _coll.find();
            return null != cursor ? cursor.toArray() : new ArrayList<DBObject>();
        }
        return null;
    }

    public List<DBObject> find(final String[] fieldNames) {
        return this.find(null, fieldNames, null, null);
    }

    public List<DBObject> find(final DBObject filter, final String[] fieldNames,
                               final String[] sortAscBy, final String[] sortDescBy) {
        if (null != _coll) {
            final DBCursor cursor = this.cursor(
                    filter, fieldNames, 0, 0, sortAscBy, sortDescBy);
            return null != cursor
                    ? cursor.toArray()
                    : new ArrayList<DBObject>();
        }
        return null;
    }

    /**
     * Return list of items.
     *
     * @param filter     (Optional) filter
     * @param fieldNames (Optional)
     * @param skip       Number of results to skip (0 = no skip)
     * @param limit      Number of results to return (0 = all)
     * @param sortAscBy  (Optional) Array of field names to sort ASC
     * @param sortDescBy (Optional) Array of field names to sort DESC
     * @return
     */
    public List<DBObject> find(final DBObject filter, final String[] fieldNames,
                               final int skip, final int limit,
                               final String[] sortAscBy, final String[] sortDescBy) {
        if (null != _coll) {
            final DBCursor cursor = this.cursor(
                    filter, fieldNames, skip, limit, sortAscBy, sortDescBy);
            return null != cursor
                    ? cursor.toArray()
                    : new ArrayList<DBObject>();
        }
        return null;
    }

    public List distinct(final String key, final DBObject query) {
        if (null != _coll) {
            return _coll.distinct(key, null != query ? query : new BasicDBObject());
        }
        return null;
    }

    public MongoPage paged(final DBObject filter,
                           final String[] fieldNames,
                           final int skip, final int limit,
                           final String[] sortAscBy, final String[] sortDescBy) {
        final MongoPage result = new MongoPage();
        // result cursor
        final DBCursor cursor = this.cursor(filter, fieldNames, skip, limit,
                sortAscBy, sortDescBy);
        try {
            final int count = MongoUtils.queryIsOR(filter)
                    ? this.count(filter, false)
                    : this.count(cursor, true);
            final int pageCount = MathUtils.paging(limit, count);
            final int pageNr = skip > 0 ? skip / limit + 1 : 1;

            //final int count = null != cursor ? cursor.count() : 0;
            final List<DBObject> list = null != cursor
                    ? cursor.toArray()
                    : new ArrayList<DBObject>();

            result.setItems(list);
            result.setCount(count);
            result.setPageCount(pageCount);
            result.setPageNr(pageNr);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return result;
    }

    public List<DBObject> geoNear(final Double[] coord,
                                  final int maxDistance) {
        final double[] dc = new double[]{coord[0], coord[1]};
        return geoNear(dc, maxDistance);
    }

    /**
     * geoNear is a db command and is used as this.
     *
     * @param coord Lon and Lat
     * @return List
     */
    public List<DBObject> geoNear(final double[] coord,
                                  final int maxDistance) {
        if (null != _db && null != _coll) {
            final BasicDBObject cmd = new BasicDBObject();
            cmd.put("geoNear", _coll.getName());

            // maxDistance
            if (maxDistance > 0) {
                cmd.put("maxDistance", getMaxDistance(maxDistance));
                cmd.put("distanceMultiplier", EARTH_RADIUS_mt); // result data are in mt.
            }

            //int coord[] = {50, 50};
            cmd.put("near", coord);

            cmd.put("num", 10);

            final CommandResult result = _db.command(cmd);
            // retrieve response from result
            if (result.ok()) {
                final Object list = result.get("results");
                if (list instanceof List) {
                    return (List) list;
                }
            } else {
                // error
                final String err = result.getErrorMessage();
                this.getLogger().log(Level.SEVERE, err);
            }
        }
        return new ArrayList<DBObject>();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" LOCALIZATION ">
    public final void addLocalization(final Object id,
                                      final String lang, final String field,
                                      final Object value) {
        try {
            final MongoTranslationManager srvc = this.getTranslationManager();
            srvc.add(id, field, lang, value);
        } catch (Throwable ex) {
        }
    }

    public final int removeLocalizations() {
        final MongoTranslationManager srvc = this.getTranslationManager();
        return srvc.removeAll();
    }

    public final int removeLocalizations(final DBObject item) {
        final String id = MongoUtils.getString(item, IMongoConstants.ID);
        return this.removeLocalizations(id);
    }

    public final int removeLocalizations(final Object id) {
        try {
            final MongoTranslationManager srvc = this.getTranslationManager();
            return srvc.removeAll(id);
        } catch (Throwable ex) {
        }
        return -1;
    }

    public final void removeLocalization(
            final Object id, final String lang, final String[] fields) {
        for (final String field : fields) {
            this.removeLocalization(id, lang, field);
        }
    }

    public final void removeLocalization(final Object id,
                                         final String lang, final String field) {
        try {
            final MongoTranslationManager srvc = this.getTranslationManager();
            srvc.remove(id, field, lang);
        } catch (Throwable ex) {
        }
    }

    public final void localize(final MongoPage page,
                               final String lang, final String[] fields) {
        this.localize(MongoPage.getItems(page), lang, fields, false);
    }

    public final void localize(final MongoPage page,
                               final String lang, final String[] fields,
                               final boolean onlyExistingFields) {
        this.localize(MongoPage.getItems(page), lang, fields, onlyExistingFields);
    }

    /**
     * Localize a list of objects.
     *
     * @param list   Name of collection. i.e. "documents"
     * @param list   List of objects to localize
     * @param lang   The language. i.e. "it"
     * @param fields Array of field names to localize. i.e. {"name", "description"}
     */
    public final void localize(final List<DBObject> list,
                               final String lang, final String[] fields) {
        this.localize(list, lang, fields, false);
    }

    public final void localize(final List<DBObject> list,
                               final String lang, final String[] fields,
                               final boolean onlyExistingFields) {
        if (!CollectionUtils.isEmpty(list) && StringUtils.hasText(lang)) {
            for (final DBObject item : list) {
                this.localize(item, lang, fields, onlyExistingFields);
            }
        }
    }

    /**
     * Localize an object
     *
     * @param item   The Object to localize
     * @param lang   The language. i.e. "it"
     * @param fields Array of field names to localize. i.e. {"name", "description"}
     */
    public final void localize(final DBObject item,
                               final String lang,
                               final String[] fields) {
        this.localize(item, lang, fields, false);
    }

    public final void localize(final DBObject item,
                               final String lang,
                               final String[] fields,
                               final boolean onlyExistingFields) {
        if (null != fields && fields.length > 0 && null != item && StringUtils.hasText(lang)) {
            //-- define fields to localize --//
            final String[] fields_to_localize;
            if (fields.length == 1 && WILDCHAR.equalsIgnoreCase(fields[0])) {
                // all fields
                final Set<String> keys = item.keySet();
                fields_to_localize = keys.toArray(new String[keys.size()]);
            } else {
                fields_to_localize = fields;
            }
            //-- loop on fields to localize --//
            try {
                final MongoTranslationManager srvc = this.getTranslationManager();
                for (final String field : fields_to_localize) {
                    if (onlyExistingFields && !item.containsField(field)) {
                        continue;
                    }
                    localize(srvc, item, lang, field);
                }
            } catch (Throwable ex) {
            }
        }
    }

    /**
     * Retrieve e a list of "Field Id" values from translation collection.
     *
     * @param lang       Language
     * @param filterText Text to search for
     * @return List of fieldIds.
     */
    public final List<String> getTranslatedFieldIds(final String lang,
                                                    final String filterText) {
        return this.getTranslationManager().getTranslatedFieldIds(lang, filterText);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" COUNT ">
    public int count(final DBObject filter) {
        return this.count(filter, true);
    }

    public int count(final DBObject filter,
                     final boolean usecursorCount) {
        final DBCursor cursor = this.cursor(filter, new String[]{"_id"}, 0, 0, null, null);
        return this.count(cursor, usecursorCount);
    }

    public int count(final DBCursor cursor,
                     final boolean usecursorCount) {
        if (usecursorCount) {
            return null != cursor ? cursor.count() : 0;
        } else {
            return null != cursor ? cursor.toArray().size() : 0;
        }
    }
    //</editor-fold>

    public void inc(final DBObject item, final String field, final int value) {
        //  db.analytic.update({"url" : "www.example.com"}, {"$inc" : {"pageviews" : 1}})
        if (null != _coll && null != item && StringUtils.hasText(field) && value > 0) {
            final Object id = this.getId(item);
            final DBObject query = new BasicDBObject();
            query.put(_ID, id);
            final DBObject action = new BasicDBObject();
            action.put(MODIFIER_INC, new BasicDBObject(field, value));

            _coll.update(query, action);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    protected MongoTranslationManager getTranslationManager() {
        return _tm;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private StandardCodedException getError500(final Throwable t) {
        final String message = ExceptionUtils.getRealMessage(t);
        return this.getError500(message);
    }

    private StandardCodedException getError500(final String message) {
        return new StandardCodedException(
                StandardCodedException.ERROR_500_SERVERERROR,
                message);
    }

    private StandardCodedException getError418(final String message) {
        return new StandardCodedException(
                StandardCodedException.ERROR_418_IAMTEAPOT,
                message);
    }

    private String getUUID() {
        return MongoUtils.createUUID();
    }

    private boolean isUpdatedExisting(final WriteResult wr) {
        if (null != wr) {
            return this.isUpdatedExisting(wr.getLastError());
        }
        return true;
    }

    private boolean isUpdatedExisting(final CommandResult cmdRes) {
        if (null != cmdRes) {
            return cmdRes.getBoolean("updatedExisting", true);
        }
        return true;
    }

    private Object getId(final DBObject object) {
        return MongoUtils.getId(object);
    }

    private Object getOrCreateId(final DBObject object) {
        if (null != object) {
            if (object.containsField(_ID)) {
                return object.get(_ID);
            } else {
                return this.getUUID();
            }
        }
        return null;
    }

    private boolean overwrite(final DBObject object, final Object id)
            throws StandardCodedException {
        final DBObject filter = new BasicDBObject(_ID, id);
        // removeOne empty arrays
        final DBObject item = this.removeEmptyArrays(object);
        // save
        return this.nativeUpdate(filter, item, true, false);
        // object.put(_ID, id);    // add _id
    }

    private boolean update(final DBObject object, final Object id)
            throws StandardCodedException {
        final DBObject filter = new BasicDBObject(_ID, id);
        // removeOne empty arrays
        final DBObject item = this.removeEmptyArrays(object);
        // removeOne _id for update of other fields
        item.removeField(_ID);
        final DBObject operation = new BasicDBObject("$set", item);
        // save
        return this.nativeUpdate(filter, operation, true, false);
        // object.put(_ID, id);    // add _id
    }

    private boolean nativeUpdate(final DBObject query, final DBObject object,
                                 final boolean upsert, final boolean multi)
            throws StandardCodedException {
        final WriteResult result = _coll.update(query, object, upsert, multi);
        if (StringUtils.hasText(result.getError())) {
            throw this.getError418(result.getError());
        }
        return this.isUpdatedExisting(result);
    }

    private DBObject merge(final DBObject object, final Object id,
                           final String[] excludeProperties)
            throws StandardCodedException {
        WriteResult result;
        final DBObject existing = this.findById(id);
        MongoUtils.merge(object, existing, excludeProperties);
        // removeOne empty arrays before save
        final DBObject item = this.removeEmptyArrays(existing);
        // save
        result = _coll.save(item);

        if (StringUtils.hasText(result.getError())) {
            throw this.getError418(result.getError());
        }

        return item;
    }

    private DBObject removeEmptyArrays(final DBObject object) {
        return MongoUtils.removeEmptyArrays(object);
    }

    /**
     * Return an object containing fields to include or fields to exclude.
     * i.e. "{thumbnail:0}" exclude 'thumbnail' field.
     *
     * @param fields
     * @param include
     * @return
     */
    private DBObject getFields(final String[] fields, final boolean include) {
        return MongoUtils.getFields(fields, include);
    }

    private DBCursor cursor(final DBObject filter, final String[] fieldNames,
                            final int skip, final int limit,
                            final String[] sortAscBy, final String[] sortDescBy) {
        if (null != _coll) {
            DBCursor cursor;
            final DBObject fields = this.getFields(fieldNames, true);
            final DBObject sort = MongoUtils.getSortFields(sortAscBy, sortDescBy);
            if (null != filter) {
                cursor = null != fields
                        ? _coll.find(filter, fields)
                        : _coll.find(filter);
            } else {
                cursor = null != fields
                        ? _coll.find(new BasicDBObject(), fields)
                        : _coll.find();
            }

            // limit data
            if (skip > 0 || limit > 0) {
                if (skip > 0) {
                    cursor = cursor.skip(skip);
                }
                if (limit > 0) {
                    cursor = cursor.limit(limit);
                }
            }

            // sort
            if (null != cursor && null != sort) {
                cursor = cursor.sort(sort);
            }

            return cursor;
        }
        return null;
    }

    // --------------------------------------------------------------------
    //                  S T A T I C
    // --------------------------------------------------------------------

    /**
     * Returns maxDistance value relative to earth radius
     *
     * @param maxDistanceInMeters max distance in meters
     * @return maxDistance value relative to earth radius
     */
    private static double getMaxDistance(final int maxDistanceInMeters) {
        return maxDistanceInMeters / EARTH_RADIUS_mt;
    }

    private static void localize(final MongoTranslationManager translator,
                                 final DBObject item,
                                 final String lang,
                                 final String field) {
        final Object id = MongoUtils.getId(item);
        if (field.indexOf(".") == -1) {
            // standard field name
            final DBObject value = MongoUtils.getDBObject(item, field, null);
            if (null != value) {
                // EMBEDDED TRANSLATIONS
                localize(item, lang, field, value);
            } else {
                final Object translated = translator.get(id, field, lang);
                if (!StringUtils.isNULL(translated)) {
                    item.put(field, translated);
                } else {
                    // value is not replaced
                    // this.getLogger().info(StringUtils.format( "value not replaced for '{0}'. Keep original '{1}'", field, item.get(field)));
                }
            }
        } else {
            // path (i.e. 'types.description') [ONLY FOR EMBEDDED]
            localizePath(item, lang, field);
        }
    }

    private static void localizePath(final DBObject item,
                                     final String lang,
                                     final String path) {
        final String[] tokens = StringUtils.split(path, ".");
        if (tokens.length > 1) {
            final String[] a = CollectionUtils.removeTokenFromArray(tokens, tokens.length - 1);
            final String new_path = CollectionUtils.toDelimitedString(a, ".");
            final Object propertyBean = MongoUtils.getByPath(item, new_path);
            final String fieldName = CollectionUtils.getLast(tokens);
            if (propertyBean instanceof List) {
                final List list = (List) propertyBean;
                for (final Object obj : list) {
                    if (obj instanceof DBObject) {
                        final DBObject dbo = (DBObject) obj;
                        final DBObject translation = MongoUtils.getDBObject(dbo, fieldName);
                        localize(dbo, lang, fieldName, translation);
                    }
                }
            } else if (propertyBean instanceof DBObject) {
                final DBObject dbo = (DBObject) propertyBean;
                final DBObject translation = MongoUtils.getDBObject(dbo, fieldName);
                localize(dbo, lang, fieldName, translation);
            }
        }
    }

    private static void localize(final DBObject item,
                                 final String lang,
                                 final String field,
                                 final DBObject translation) {
        if (translation.containsField(lang)) {
            item.put(field, translation.get(lang));
        } else if (translation.containsField(LOCALE_BASE_FIELD)) {
            item.put(field, translation.get(LOCALE_BASE_FIELD));
        }
    }
}
