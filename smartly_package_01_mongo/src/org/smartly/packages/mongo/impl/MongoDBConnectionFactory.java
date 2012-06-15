package org.smartly.packages.mongo.impl;

import com.mongodb.DB;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.util.FormatUtils;

/**
 * User: angelo.geminiani
 */
public class MongoDBConnectionFactory {

    private MongoDBConnectionFactory() { }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static boolean hasDBConnection(final String name){
        try{
           return null!=getConnection(name);
        }catch(Throwable ignored){}
        return false;
    }

    public static DB getConnection( final String dbName ) throws StandardCodedException {
        final Object config = Smartly.getConfiguration().get("databases." + dbName);
        if(config instanceof JSONObject){
             return new MongoDBConnection((JSONObject)config).getDB();
        } else {
            throw new StandardCodedException(
                    FormatUtils.format(
                            "DATABASE NOT FOUND IN CONFIGURATION FOLDER: '{0}'",
                            dbName));
        }
    }


    public static String[] getLanguages(){
        return Smartly.getLanguages();
    }
}
