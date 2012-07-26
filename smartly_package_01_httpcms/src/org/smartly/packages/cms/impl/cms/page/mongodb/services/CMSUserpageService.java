/*
 * 
 */
package org.smartly.packages.cms.impl.cms.page.mongodb.services;

import com.mongodb.DBObject;
import org.smartly.Smartly;
import org.smartly.packages.cms.impl.cms.page.mongodb.CMSDBFactory;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSUserpage;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.StandardCodedException;

/**
 * @author angelo.geminiani
 */
public class CMSUserpageService extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    private static String[] LOCALFIELDS = new String[]{};

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public CMSUserpageService() throws StandardCodedException {
        super(CMSDBFactory.getInstance().getDBMain(),
                CMSUserpage.COLLECTION,
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
            final CMSUserpageService srvc = new CMSUserpageService();
            return srvc.getById(id);
        } catch (Throwable ignored) {
        }
        return null;
    }
}
