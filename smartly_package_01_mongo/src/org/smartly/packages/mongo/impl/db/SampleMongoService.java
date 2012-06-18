/*
 * 
 */

package org.smartly.packages.mongo.impl.db;

import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class SampleMongoService extends AbstractMongoService {

    private static String NAME = "test";

    public SampleMongoService() throws StandardCodedException {
        super(SampleMongoInitializer.getInstance().getDB(), NAME, new String[0]);
    }


}
