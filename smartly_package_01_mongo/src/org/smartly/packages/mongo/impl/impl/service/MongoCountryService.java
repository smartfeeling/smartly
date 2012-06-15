/*
 * 
 */
package org.smartly.packages.mongo.impl.impl.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.impl.entity.MongoCountry;

import java.util.List;

/**
 * @author angelo.geminiani
 */
public class MongoCountryService extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    private static String[] LOCALFIELDS = new String[]{
            MongoCountry.NAME, MongoCountry.CURRENCY_NAME};

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoCountryService(final DB db, final String[] langCodes) {
        super(db, MongoCountry.COLLECTION, langCodes);
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------
    public void localize(final List<DBObject> list, final String lang) {
        super.localize(list, lang, LOCALFIELDS);
    }

    public void localize(final DBObject item, final String lang) {
        super.localize(item, lang, LOCALFIELDS);
    }

    public List<DBObject> getEnabled() {
        final DBObject filter = new BasicDBObject();
        filter.put(MongoCountry.ENABLED, true);
        return super.find(filter, null, new String[]{MongoCountry.ID}, null);
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
}
