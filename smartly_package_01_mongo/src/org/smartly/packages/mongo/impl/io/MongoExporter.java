/*
 * 
 */
package org.smartly.packages.mongo.impl.io;

import com.mongodb.DB;
import com.mongodb.DBObject;
import org.smartly.commons.csv.CSVFileWriter;
import org.smartly.commons.csv.CSVWriter;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.impl.GenericMongoService;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author angelo.geminiani
 */
public class MongoExporter {

    // ------------------------------------------------------------------------
    //                      variables
    // ------------------------------------------------------------------------
    private final String _collName;
    private final GenericMongoService _srvc;

    // ------------------------------------------------------------------------
    //                      constructor
    // ------------------------------------------------------------------------
    public MongoExporter(final DB db, final String collName) throws StandardCodedException {
        _collName = collName;
        _srvc = new GenericMongoService(db, collName, new String[0]);

    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------
    public String exportAsFile(final String filepath) throws IOException {
        final String destination = this.getFilePath(filepath);
        FileUtils.mkdirs(filepath);
        final CSVWriter writer = new CSVFileWriter(destination);
        try {
            final List<DBObject> data = _srvc.find();
            this.export(writer, data);
        } catch (Throwable t) {
        }
        writer.close();
        return destination;
    }

    public String exportAsString() throws IOException {
        final StringWriter output = new StringWriter();
        final CSVWriter writer = new CSVWriter(output);
        try {
            final List<DBObject> data = _srvc.find();
            this.export(writer, data);
        } catch (Throwable t) {
        }
        writer.close();
        return output.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    private String getFilePath(final String filepath) {
        final String absolute = PathUtils.getAbsolutePath(filepath);
        final String name = PathUtils.getFilename(filepath, true);
        final String path = PathUtils.getParent(absolute);
        if (StringUtils.hasText(name)) {
            if (!StringUtils.hasText(PathUtils.getFilenameExtension(filepath, true))) {
                return PathUtils.changeFileExtension(filepath, ".csv");
            } else {
                return filepath;
            }
        } else {
            return PathUtils.concat(path, _collName.concat(".csv"));
        }
    }

    private int export(final CSVWriter writer, final List<DBObject> data) {
        final List<String> keys = new ArrayList<String>();
        final List<Map> mapdata = new LinkedList<Map>();

        // create writeable data
        for (final DBObject item : data) {
            final Map itemmap = item.toMap();
            mapdata.add(itemmap);
            // add keys to list
            CollectionUtils.addAllNoDuplicates(keys, itemmap.keySet());
        }

        // normalize writeable data fields
        for (final Map itemmap : mapdata) {
            for (final String key : keys) {
                if (!itemmap.containsKey(key)) {
                    itemmap.put(key, "");
                }
            }
        }

        return writer.writeAll(mapdata, true);
    }
}
