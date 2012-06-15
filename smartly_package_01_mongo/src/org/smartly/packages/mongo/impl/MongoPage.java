/*
 * 
 */
package org.smartly.packages.mongo.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.List;

/**
 * @author angelo.geminiani
 */
public class MongoPage extends BasicDBObject {

    private static final String ITEMS = "items";
    private static final String COUNT = "count";

    public MongoPage() {
    }

    public void setItems(final List value) {
        super.put(ITEMS, value);
    }

    public List getItems() {
        return MongoUtils.getList(this, ITEMS);
    }

    public void setCount(final int value) {
        super.put(COUNT, value);
    }

    public int getCount() {
        return MongoUtils.getInt(this, COUNT);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    public static List<DBObject> getItems(final DBObject entity) {
        return MongoUtils.getList(entity, ITEMS);
    }

    public static long getCount(final DBObject entity) {
        return MongoUtils.getLong(entity, COUNT);
    }
}
