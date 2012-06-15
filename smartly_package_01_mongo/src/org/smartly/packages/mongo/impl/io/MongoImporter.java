/*
 * 
 */
package org.smartly.packages.mongo.impl.io;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.smartly.commons.csv.CSVFileReader;
import org.smartly.commons.csv.CSVReader;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.impl.GenericMongoService;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class MongoImporter {

    // ------------------------------------------------------------------------
    //                      variables
    // ------------------------------------------------------------------------
    private final GenericMongoService _srvc;

    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------
    public MongoImporter(final DB db, final String collName)
            throws StandardCodedException {
        _srvc = new GenericMongoService(db, collName, new String[0]);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public List<DBObject> importFromFile(final String filepath) throws Exception {
        List<DBObject> result = null;
        final CSVFileReader reader = new CSVFileReader(filepath);
        try {
            final List<Map<String, String>> data = reader.readAllAsMap(true);
            result = this.doImport(data);
        } catch (Throwable t) {
        }
        reader.close();
        return null != result ? result : new ArrayList<DBObject>();
    }

    public List<DBObject> importFromString(final String text) throws Exception {
        List<DBObject> result = null;
        final CSVReader reader = new CSVReader();
        reader.setReader(new StringReader(text));
        try {
            final List<Map<String, String>> data = reader.readAllAsMap(true);
            result = this.doImport(data);
        } catch (Throwable t) {
        }
        reader.close();
        return null != result ? result : new ArrayList<DBObject>();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private List<DBObject> doImport(final List<Map<String, String>> data) throws StandardCodedException {
        final List<DBObject> result = new LinkedList<DBObject>();
        for (final Map<String, String> item : data) {
            final DBObject dbo = new BasicDBObject(item);
            _srvc.upsert(dbo);
            result.add(dbo);
        }
        return result;
    }
}
