/*
 * 
 */
package org.smartly.packages.cms.impl.cms.page.mongodb.services;

import com.mongodb.DBObject;
import org.smartly.Smartly;
import org.smartly.packages.cms.impl.cms.page.mongodb.CMSDBFactory;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSPageEntity;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSPageEntity;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSPageEntity;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class CMSPageEntityService extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    private static String[] LOCALFIELDS = new String[]{};

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public CMSPageEntityService() throws StandardCodedException {
        super(CMSDBFactory.getInstance().getDBMain(),
                CMSPageEntity.COLLECTION,
                Smartly.getLanguages());
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------

    public DBObject getById(final String id) {
        final DBObject object = super.findById(id);
        return object;
    }

    @Override
    public int upsert(final DBObject object) throws StandardCodedException {
        return super.upsert(object);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static DBObject getPage(final String url) {
        try {
            final String id = url.replaceAll("/", "");
            final CMSPageEntityService srvc = new CMSPageEntityService();
            return srvc.getById(id);
        } catch (Throwable ignored) {
        }
        return null;
    }
}
