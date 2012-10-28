/*
 * 
 */
package org.smartly.packages.mongo.impl.io;

import com.mongodb.*;
import org.bson.BSONObject;
import org.bson.io.OutputBuffer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.csv.CSVFileReader;
import org.smartly.commons.csv.CSVReader;
import org.smartly.commons.util.*;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.db.GenericMongoService;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

/**
 * @author angelo.geminiani
 */
public class MongoImporter {

    /**
     * Use this field to store localizations into single json file to import.
     * 'localizations' field will be removed and localizations will be added to each collection field.<br>
     * i.e. : 'localizations':[
     * {'lang':'it', 'name':'un nome', 'description':'una descrizione'},
     * {'lang':'en', 'name':'a name', 'description':'a description'}
     * ]
     */
    public static final String FIELD_LOCALIZATIONS = "localizations"; //

    public static final String FIELD_LANG = "lang";

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
        return this.importFromFile(filepath);
    }
    public List<DBObject> importFromFile(final String filepath,
                                         final ImporterIterator iterator) throws Exception {
        List<DBObject> result = null;
        if (isCSVFile(filepath)) {
            final CSVFileReader reader = new CSVFileReader(filepath);
            try {
                final List<Map<String, String>> data = reader.readAllAsMap(true);
                result = this.doImport(data, iterator);
            } catch (Throwable t) {
            }
            reader.close();
        } else if (isJSONFile(filepath)){
            final String json = FileUtils.copyToString(new FileReader(filepath));
            final JsonWrapper jsw = JsonWrapper.wrap(json);
            if(jsw.isJSONArray()){
                result = this.doImport(jsw.getJSONArray(), iterator);
            }
        }
        return null != result ? result : new ArrayList<DBObject>();
    }

    public List<DBObject> importFromString(final String text) throws Exception {
       return this.importFromString(text, null);
    }

    public List<DBObject> importFromString(final String text,
                                           final ImporterIterator iterator) throws Exception {
        List<DBObject> result = null;
        if(StringUtils.isJSONArray(text)){
            final JsonWrapper jsw = JsonWrapper.wrap(text);
            if(jsw.isJSONArray()){
                result = this.doImport(jsw.getJSONArray(), iterator);
            }
        } else {
            final CSVReader reader = new CSVReader();
            reader.setReader(new StringReader(text));
            try {
                final List<Map<String, String>> data = reader.readAllAsMap(true);
                result = this.doImport(data, iterator);
            } catch (Throwable t) {
            }
            reader.close();
        }

        return null != result ? result : new ArrayList<DBObject>();
    }

    public List<DBObject> importFromResource(final String resourceName) throws Exception {
          return this.importFromResource(resourceName, null);
    }

    public List<DBObject> importFromResource(final String resourceName,
                                             final ImporterIterator iterator) throws Exception {
        List<DBObject> result = null;

        if (isCSVFile(resourceName)) {
            final List<Map<String, String>> data = readCSV(resourceName);
            result = this.doImport(data, iterator);
        } else if (isJSONFile(resourceName)) {
            final JSONArray data = readJSONArray(resourceName);
            result = this.doImport(data, iterator);
        }

        return null != result ? result : new ArrayList<DBObject>();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private List<DBObject> doImport(final List<Map<String, String>> data,
                                    final ImporterIterator iterator) throws StandardCodedException {
        final List<DBObject> result = new LinkedList<DBObject>();
        for (final Map<String, String> item : data) {
            final DBObject dbo = new BasicDBObject(item);
            if(null!=iterator){
                iterator.importing(dbo);
            }
            _srvc.upsert(dbo);
            result.add(dbo);
        }
        return result;
    }

    private List<DBObject> doImport(final JSONArray list,
                                    final ImporterIterator iterator) throws Exception {
        final List<DBObject> result = new LinkedList<DBObject>();
        for (int i = 0; i < list.length(); i++) {
            final JSONObject jso = list.getJSONObject(i);
            final DBObject dbo = MongoUtils.parseObject(jso);
            List localizations = null;
            if (dbo.containsField(FIELD_LOCALIZATIONS)) {
                final Object obj = MongoUtils.remove(dbo, FIELD_LOCALIZATIONS);
                if (obj instanceof List) {
                    localizations = (List) obj;
                }
            }
            if(null!=iterator){
                iterator.importing(dbo);
            }
            _srvc.upsert(dbo);

            this.doImportLocalizations(MongoUtils.getId(dbo), localizations);

            result.add(dbo);
        }
        return result;
    }

    private void doImportLocalizations(final Object id, final List localizations) {
        if (null != localizations) {
            for (final Object item : localizations) {
                if (item instanceof DBObject) {
                    final DBObject dbo = (DBObject) item;
                    final String lang = MongoUtils.getString(dbo, FIELD_LANG);
                    if (StringUtils.hasText(lang)) {
                        final Set<String> keys = dbo.keySet();
                        for (final String key : keys) {
                            if (!FIELD_LANG.equalsIgnoreCase(key)) {
                                _srvc.addLocalization(id, lang, key, dbo.get(key));
                            }
                        }
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //                          S T A T I C
    // --------------------------------------------------------------------

    private static JSONArray readJSONArray(final String resourceName) throws Exception {
        final String text = ClassLoaderUtils.getResourceAsString(resourceName);
        final JSONArray array = JsonWrapper.wrap(text).getJSONArray();
        return array;
    }

    private static List<Map<String, String>> readCSV(final String resource) throws Exception {
        String text = ClassLoaderUtils.getResourceAsString(resource);
        final CSVReader reader = new CSVReader(new StringReader(text), ';');
        return reader.readAllAsMap(true);
    }

    private static boolean isJSONFile(final String name){
        return PathUtils.getFilenameExtension(name, true).equalsIgnoreCase(".json");
    }

    private static boolean isCSVFile(final String name){
        return PathUtils.getFilenameExtension(name, true).equalsIgnoreCase(".csv");
    }

    // --------------------------------------------------------------------
    //                          EMBEDDED
    // --------------------------------------------------------------------

    public interface ImporterIterator {
        void importing(DBObject item);
    }
}
