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
    private static final String PAGE_COUNT = "page_count";
    private static final String PAGE_NR = "page_nr";

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

    public void setPageNr(final int value) {
        super.put(PAGE_NR, value);
    }

    public int getPageNr() {
        return MongoUtils.getInt(this, PAGE_NR);
    }


    public void setPageCount(final int value) {
        super.put(PAGE_COUNT, value);
    }

    public int getPageCount() {
        return MongoUtils.getInt(this, PAGE_COUNT);
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

    public static long getPageNr(final DBObject entity) {
        return MongoUtils.getLong(entity, PAGE_NR);
    }

    public static long getPageCount(final DBObject entity) {
        return MongoUtils.getLong(entity, PAGE_COUNT);
    }
}
