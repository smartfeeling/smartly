/*
 * 
 */
package org.smartly.packages.mongo.impl.db.entity.item;

import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.db.entity.IMongoEntityConstants;
import org.smartly.packages.mongo.impl.util.MongoUtils;

/**
 * Address Entity.
 * <p/>
 * FIELDS:<br/>
 * - STREET {street}:
 * - PLACE {place}:
 * - ZIP {zip}:
 * - CITY {city}:
 * - STATE {state}:
 * - COUNTRY {country}:
 *
 * @author angelo.geminiani
 */
public class MongoAddress extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String STREET = IMongoEntityConstants.STREET; // Madison Avn., 7
    public static final String PLACE = IMongoEntityConstants.PLACE; // Palace B
    public static final String ZIP = IMongoEntityConstants.ZIP; // 47858
    public static final String CITY = IMongoEntityConstants.CITY; // Rochester
    public static final String STATE = IMongoEntityConstants.STATE; // NY
    public static final String COUNTRY = IMongoEntityConstants.COUNTRY; // USA

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoAddress() {
        this.append(STREET, "");
        this.append(PLACE, "");
        this.append(ZIP, "");
        this.append(CITY, "");
        this.append(STATE, "");
        this.append(COUNTRY, "");
    }

    public MongoAddress(final DBObject item) {
        super(item);
    }

    // ------------------------------------------------------------------------
    //                      STATIC
    // ------------------------------------------------------------------------
    public static String getStreet(final DBObject item) {
        return MongoUtils.getString(item, STREET);
    }

    public static void setStreet(final DBObject item, final String value) {
        MongoUtils.put(item, STREET, value);
    }

    public static String getPlace(final DBObject item) {
        return MongoUtils.getString(item, PLACE);
    }

    public static void setPlace(final DBObject item, final String value) {
        MongoUtils.put(item, PLACE, value);
    }

    public static String getZip(final DBObject item) {
        return MongoUtils.getString(item, ZIP);
    }

    public static void setZip(final DBObject item, final String value) {
        MongoUtils.put(item, ZIP, value);
    }

    public static String getCity(final DBObject item) {
        return MongoUtils.getString(item, CITY);
    }

    public static void setCity(final DBObject item, final String value) {
        MongoUtils.put(item, CITY, value);
    }

    public static String getState(final DBObject item) {
        return MongoUtils.getString(item, STATE);
    }

    public static void setState(final DBObject item, final String value) {
        MongoUtils.put(item, STATE, value);
    }

    public static String getCountry(final DBObject item) {
        return MongoUtils.getString(item, COUNTRY);
    }

    public static void setCountry(final DBObject item, final String value) {
        MongoUtils.put(item, COUNTRY, value);
    }
}
