/*
 * 
 */
package org.smartly.packages.mongo.impl.db;

import com.mongodb.DB;
import org.smartly.packages.mongo.impl.MongoDB;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class SampleMongoInitializer {

    private final MongoDB _mongo;
    private String _dbname;
    private String _username;
    private String _password;

    private SampleMongoInitializer() {
        _mongo = new MongoDB();
        _dbname = "test";
        _username = null;
        _password = null;
    }

    public void initialize(final String host,
                           final int port,
                           final String dbname,
                           final String username,
                           final String password) {
        _dbname = dbname;
        _username = username;
        _password = password;
        _mongo.setHost(host);
        _mongo.setPort(port);
    }

    public final DB getDB()
            throws StandardCodedException {
        return _mongo.getDB(_dbname, _username, _password);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private static SampleMongoInitializer __instance;

    public static SampleMongoInitializer getInstance() {
        if (null == __instance) {
            __instance = new SampleMongoInitializer();
        }
        return __instance;
    }


}
