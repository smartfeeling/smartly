/*
 * 
 */
package org.smartly.packages.mongo.impl.impl.entity;

import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

/**
 * User Role.
 * <p/>
 * FIELDS:
 * - ID {_id}: numeric
 * - NAME {name}: role name
 * - DESCRIPTION {description}: optional role description
 * - DATA {data}: Custom role attributes
 *
 * @author angelo.geminiani
 */
public class MongoRole extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static String COLLECTION = "roles";
    //
    public static final String NAME = IMongoEntityConstants.NAME;
    public static final String DESCRIPTION = IMongoEntityConstants.DESCRIPTION;
    //-- objects --//
    public static final String DATA = IMongoEntityConstants.DATA;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoRole() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private void init() {
        final String id = MongoUtils.createUUID();
        this.append(ID, id);
    }

    // ------------------------------------------------------------------------
    //                      STATIC
    // ------------------------------------------------------------------------
    public static String getId(final DBObject item) {
        return MongoUtils.getString(item, ID);
    }

    public static void setId(final DBObject item, final String value) {
        MongoUtils.put(item, ID, value);
    }

    public static String getName(final DBObject item) {
        return MongoUtils.getString(item, NAME);
    }

    public static void setName(final DBObject item, final String value) {
        MongoUtils.put(item, NAME, value);
    }

    public static String getDescription(final DBObject item) {
        return MongoUtils.getString(item, DESCRIPTION);
    }

    public static void setDescription(final DBObject item, final String value) {
        MongoUtils.put(item, DESCRIPTION, value);
    }

    public static DBObject getData(final DBObject item) {
        return MongoUtils.getDBObject(item, DATA);
    }

    public static void setData(final DBObject item, final DBObject value) {
        MongoUtils.put(item, DATA, value);
    }
}
