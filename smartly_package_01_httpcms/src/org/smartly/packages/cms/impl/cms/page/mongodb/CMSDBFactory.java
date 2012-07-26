/*
 * 
 */
package org.smartly.packages.cms.impl.cms.page.mongodb;

import com.mongodb.DB;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.MongoDBConnectionFactory;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class CMSDBFactory {

    public static String DB_MAIN = "MONGO_cms";       // main db

    public final DB getDBMongo(final String dbname)
            throws StandardCodedException {
        if (StringUtils.hasText(dbname)) {
            return MongoDBConnectionFactory.getDB(dbname);
        } else {
            return this.getDBMain();
        }
    }

    public final DB getDBMain()
            throws StandardCodedException {
        return MongoDBConnectionFactory.getDB(DB_MAIN);
    }


    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static CMSDBFactory __instance;

    public static CMSDBFactory getInstance() {
        if (null == __instance) {
            __instance = new CMSDBFactory();
        }
        return __instance;
    }


}
