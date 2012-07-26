/*
 * 
 */
package org.smartly.packages.cms.impl.cms.page.mongodb;

import com.mongodb.DB;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FormatUtils;


/**
 * Ensure Indexes for collections
 *
 * @author angelo.geminiani
 */
public class CMSMongoSchema {

    public static void init() {
        try {
            final DB db = CMSDBFactory.getInstance().getDBMain();

            //initTargetSchema();

        } catch (Throwable t) {
            LoggingUtils.getLogger(CMSDBFactory.class).log(Level.SEVERE,
                    FormatUtils.format("Error initilizing Schema: {0}", t), t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    /*
    private static void initTargetSchema() {
        try {
            final QRTargetService srvc = new QRTargetService();

            // userid 
            srvc.ensureIndex(QRTarget.USER_ID, false);

            // keywords 
            srvc.ensureIndex(QRTarget.KEYWORDS, false);

            // userid + keywords
            srvc.ensureIndex(new String[]{
                    QRTarget.USER_ID,
                    QRTarget.KEYWORDS
            }, false, false);

        } catch (Throwable t) {
            LoggingUtils.getLogger(QRMongoSchema.class).log(Level.SEVERE,
                    FormatUtils.format("Schema error on '{0}': {1}",
                            QRTarget.COLLECTION, t), t);
        }
    } */


}
