/*
 * 
 */
package org.smartly.packages.mongo.impl.db.service;

import com.mongodb.DB;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.db.entity.MongoRole;

/**
 * @author angelo.geminiani
 */
public class MongoRoleService extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoRoleService(final DB db, final String[] langCodes) {
        super(db, MongoRole.COLLECTION, langCodes);
    }
    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------
}
