/*
 * 
 */
package org.smartly.packages.mongo.impl.impl.entity;

import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

/**
 * Session
 * <p/>
 * FIELDS:
 * - ID {_id}: string
 * - DATA {data}: Custom data
 *
 * @author angelo.geminiani
 */
public class MongoSession extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static String COLLECTION = "sessions";
    //
    //-- objects --//
    public static final String DATA = IMongoEntityConstants.DATA;

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoSession() {
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

    public static DBObject getData(final DBObject item) {
        return MongoUtils.getDBObject(item, DATA);
    }

    public static void setData(final DBObject item, final DBObject value) {
        MongoUtils.put(item, DATA, value);
    }
}
