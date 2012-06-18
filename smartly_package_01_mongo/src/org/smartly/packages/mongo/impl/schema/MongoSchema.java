/*
 * 
 */
package org.smartly.packages.mongo.impl.schema;

import com.mongodb.DB;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.packages.mongo.impl.db.entity.MongoUser;
import org.smartly.packages.mongo.impl.db.service.MongoUserService;

/**
 * Sample Mongo Collections initializer.
 * Ensure Indexes for collections.
 *
 * @author angelo.geminiani
 */
public class MongoSchema {

    private final DB _mongoDb;

    public MongoSchema(final DB mongoDb) {
        _mongoDb = mongoDb;
    }

    public DB getDB() {
        return _mongoDb;
    }

    public void initialize() {
        try {

            // users
            this.initUsersSchema();

        } catch (Throwable t) {
            LoggingUtils.getLogger(MongoSchema.class).log(Level.SEVERE,
                    FormatUtils.format("Error initilizing Schema: {0}", t), t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void checkCollectionExists(final String collName) {
        if (null == _mongoDb.getCollection(collName)) {
            _mongoDb.createCollection(collName, null);
        }
    }

    // Users
    private void initUsersSchema() throws Exception {
        try {
            final MongoUserService srvc = new MongoUserService(_mongoDb,
                    new String[0]);

            this.checkCollectionExists(srvc.getCollectionName());

            // email
            srvc.ensureIndex(MongoUser.EMAIL, true);
            // username
            srvc.ensureIndex(MongoUser.USERNAME, true);

            // password + email
            srvc.ensureIndex(new String[]{MongoUser.PASSWORD, MongoUser.EMAIL}, true, false);
            // password + username
            srvc.ensureIndex(new String[]{MongoUser.PASSWORD, MongoUser.USERNAME}, true, false);


        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE,
                    FormatUtils.format("Schema error on '{0}': {1}",
                            MongoUser.COLLECTION, t), t);
        }
    }


}
