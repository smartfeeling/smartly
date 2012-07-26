package org.smartly.packages.cms.pages;

import com.mongodb.DBObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.cms.impl.cms.page.mongodb.entities.CMSUserpage;
import org.smartly.packages.cms.impl.cms.page.mongodb.services.CMSUserpageService;
import org.smartly.packages.cms.ImportAll;
import org.smartly.commons.util.FormatUtils;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import static org.junit.Assert.assertTrue;


public class ImportPages {

    private static final String DATA_PATH = "org/smartly/packages/cms/pages/pages.json";

    @BeforeClass
    public static void init() {
        // Main.main(new String[]{"-w", "z:/_smartly_qrboost"});
        ImportAll.init();
    }

    public static void run() throws Exception {
        final ImportPages service = new ImportPages();
        service.importAll();
    }

    @Test
    public void importAll() throws Exception {
        this.reset();
        this.start();
    }

    public void start() throws Exception {
        System.out.println("Importing '" + CMSUserpage.COLLECTION + "'....");
        int count = this.importOnMongo();

        assertTrue(count > 0);

        System.out.println("IMPORTED '" + CMSUserpage.COLLECTION + "': " + count);
    }


    public void reset() throws StandardCodedException {
        System.out.println("Removig '" + CMSUserpage.COLLECTION + "'....");

        final CMSUserpageService srvc = new CMSUserpageService();
        int items = srvc.removeAll();
        int indexes = srvc.dropIndexes();
        System.out.println(FormatUtils.format("\tRemoved {0} items and {1} indexes", items, indexes));
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private int importOnMongo() throws Exception {
        int counter = 0;
        final JSONArray list = ImportAll.readJSONArray(DATA_PATH);
        for (int i=0;i<list.length();i++) {
            this.importItem(list.getJSONObject(i));
            counter++;
        }
        return counter;
    }

    private void importItem(final JSONObject jobject) throws StandardCodedException {
        final DBObject item = MongoUtils.parseObject(jobject.toString());
        (new CMSUserpageService()).upsert(item);
    }

}
