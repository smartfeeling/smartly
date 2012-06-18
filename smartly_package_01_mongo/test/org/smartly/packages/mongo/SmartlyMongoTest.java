package org.smartly.packages.mongo;

import com.mongodb.DB;
import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.mongo.impl.MongoDBConnectionFactory;
import org.smartly.packages.mongo.impl.db.entity.MongoUser;
import org.smartly.packages.mongo.impl.db.service.MongoUserService;
import org.smartly.packages.mongo.impl.schema.MongoSchema;
import org.smartly.packages.mongo.launcher.Main;


public class SmartlyMongoTest {

    public SmartlyMongoTest() {

    }

    @BeforeClass
    public static void open(){
        Main.main(new String[]{"-w", "z:/_smartly_mongo/"});
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testMain() throws Exception {

        DB db = MongoDBConnectionFactory.getDB("MONGO_sample");
        MongoSchema schema = new MongoSchema(db);

        schema.initialize();

        // try insert and remove user
        MongoUserService srvc = new MongoUserService(db, MongoDBConnectionFactory.getLanguages());
        final MongoUser user = new MongoUser();
        MongoUser.setId(user, "TEST___USER");
        MongoUser.setUserName(user, "test-user");

        srvc.upsert(user);

        DBObject dbuser = srvc.getById(MongoUser.getId(user), false);

        Assert.assertTrue(null!=dbuser);

        srvc.remove(user);

        dbuser = srvc.getById(MongoUser.getId(user), false);

        Assert.assertNull(dbuser);
    }

}
