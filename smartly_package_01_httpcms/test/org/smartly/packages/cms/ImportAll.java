package org.smartly.packages.cms;

import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.commons.csv.CSVReader;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.packages.cms.launcher.Main;
import org.smartly.packages.cms.pages.ImportPages;

import java.io.StringReader;
import java.util.List;
import java.util.Map;


public class ImportAll {

    @BeforeClass
    public static void init() {
        // Main.main(new String[]{"-w", "z:/_smartly_qrboost"});
        Main.main(new String[]{"-w", "z:/_smartly_cms/", "-t"});
    }

    @Test
    public void importAll() throws Exception {
        // userpages
        ImportPages.run();

    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


    // --------------------------------------------------------------------
    //              STATIC
    // --------------------------------------------------------------------

    public static List<Map<String, String>> readCSV(final String filename) throws Exception {
        final String text = ClassLoaderUtils.getResourceAsString(filename);
        final CSVReader reader = new CSVReader(new StringReader(text), ';');
        return reader.readAllAsMap(true);
    }

    public static JSONArray readJSONArray(final String filename) throws Exception {
        final String text = ClassLoaderUtils.getResourceAsString(filename);
        final JSONArray array = JsonWrapper.wrap(text).getJSONArray();
        return array;
    }

}
