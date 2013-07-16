package org.smartly.commons.network.api.yahoo.placefinder;

import org.json.JSONObject;
import org.junit.Test;


public class YahooPlaceFinderTest {

    private static final String APP_ID = "D9qFIW3e";
    private static final String LANG = "it";
    private static final JSONObject ADDRESS = new JSONObject("{\"zip\":\"47900\",\"state\":\"rn\",\"clima_zone\":\"E\",\"country\":\"IT\",\"city\":\"rimini\"}");

    public YahooPlaceFinderTest() {

    }

    @Test
    public void testReverseGeocoding() throws Exception {

    }

    @Test
    public void testGeocoding() throws Exception {
        YahooPlaceFinder yahoo = new YahooPlaceFinder(APP_ID);
        JSONObject result = yahoo.geocoding(LANG, ADDRESS);
        System.out.println(result);
    }

    @Test
    public void testCoordinates() throws Exception {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
