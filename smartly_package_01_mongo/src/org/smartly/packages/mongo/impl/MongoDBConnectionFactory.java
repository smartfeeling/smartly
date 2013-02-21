package org.smartly.packages.mongo.impl;

import com.mongodb.DB;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.SmartlyPathManager;
import org.smartly.commons.util.FormatUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: angelo.geminiani
 */
public class MongoDBConnectionFactory {

    private MongoDBConnectionFactory() {
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    private static Map<String, MongoDBConnection> _connections = new HashMap<String, MongoDBConnection>();

    private static MongoDBConnection getConn(final String dbName) {
        if (!_connections.containsKey(dbName)) {
            final JSONObject config = SmartlyPathManager.getConfiguration(MongoDBConnectionFactory.class).getJSONObject("databases." + dbName);
            if (null != config) {
                _connections.put(dbName, new MongoDBConnection(config));
            }
        }
        return _connections.get(dbName);
    }

    public static boolean hasDBConnection(final String name) {
        try {
            return null != getDB(name);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static MongoDBConnection getConnection(final String dbName) throws StandardCodedException {
        final MongoDBConnection conn = getConn(dbName);
        if (null != conn) {
            return conn;
        } else {
            throw new StandardCodedException(
                    FormatUtils.format(
                            "DATABASE NOT FOUND IN CONFIGURATION FOLDER: '{0}'",
                            dbName));
        }
    }

    public static DB getDB(final String dbName) throws StandardCodedException {
        final MongoDBConnection connection = getConnection(dbName);
        return null != connection ? connection.getDB() : null;
    }


    public static String[] getLanguages() {
        return Smartly.getLanguages();
    }
}
