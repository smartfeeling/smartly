/*
 * 
 */
package org.smartly.packages.mongo.impl.impl.entity.item;

import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.impl.entity.IMongoEntityConstants;
import org.smartly.packages.mongo.impl.util.MongoUtils;

/**
 * Country data.
 * <p/>
 * FIELDS:
 * - UID {uid}: id of region or province. i.e. "RN"
 * - NAME {name}: name of region or province. i.e. "Rimini"
 *
 * @author angelo.geminiani
 */
public class MongoCountryRegion extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    //-- fields --//
    public static final String UID = IMongoEntityConstants.UID;
    public static final String NAME = IMongoEntityConstants.NAME;


    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoCountryRegion() {
        this.append(UID, "");
        this.append(NAME, "");
    }

    // ------------------------------------------------------------------------
    //                      STATIC
    // ------------------------------------------------------------------------
    public static String getUid(final DBObject item) {
        return MongoUtils.getString(item, UID);
    }

    public static void setUid(final DBObject item, final String value) {
        MongoUtils.put(item, UID, value);
    }

    public static String getName(final DBObject item) {
        return MongoUtils.getString(item, NAME);
    }

    public static void setName(final DBObject item, final String value) {
        MongoUtils.put(item, NAME, value);
    }
}
