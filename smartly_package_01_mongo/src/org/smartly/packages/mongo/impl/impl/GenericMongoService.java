/*
 * 
 */
package org.smartly.packages.mongo.impl.impl;

import com.mongodb.DB;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class GenericMongoService extends AbstractMongoService {

    public GenericMongoService(
            final DB db,
            final String collName,
            final String[] langCodes) throws StandardCodedException {
        super(db, collName, langCodes);
    }
}
