/*
 * 
 */
package org.smartly.packages.mongo.impl.db.service;

import com.mongodb.DB;
import com.mongodb.DBObject;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.api.yahoo.placefinder.YahooPlaceFinder;
import org.smartly.commons.network.iplocation.IPLocation;
import org.smartly.commons.network.iplocation.IPLocator;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.AbstractMongoService;
import org.smartly.packages.mongo.impl.MongoDBConnectionFactory;
import org.smartly.packages.mongo.impl.StandardCodedException;
import org.smartly.packages.mongo.impl.db.entity.MongoAnalytics;
import org.smartly.packages.mongo.impl.db.entity.MongoCountry;
import org.smartly.packages.mongo.impl.db.service.tasks.analytics.AnalyticsData;


/**
 * @author angelo.geminiani
 */
public class MongoAnalyticsService extends AbstractMongoService {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------

    public static String YAHOO_APP_ID = (String)Smartly.getConfiguration().get("api.yahoo.appId");
    public static final String DB_NAME = "analytics";
    public static String COLLECTION_NAME = null;


    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public MongoAnalyticsService() throws StandardCodedException {
        super(MongoDBConnectionFactory.getDB(DB_NAME),
                collection(),
                MongoDBConnectionFactory.getLanguages());
    }

    public MongoAnalyticsService(final DB db, final String[] langCodes) {
        super(db, MongoCountry.COLLECTION, langCodes);
    }

    // ------------------------------------------------------------------------
    //                      public
    // ------------------------------------------------------------------------

    @Override
    public int upsert(final DBObject item) throws StandardCodedException {
        return super.upsert(item);
    }

    public void insert(final AnalyticsData data) {
        final String targetid = data.getTargetId();
        final DBObject item = new MongoAnalytics();
        MongoAnalytics.setId(item, targetid);
        MongoAnalytics.setUserId(item, data.getUserId());
        MongoAnalytics.setLang(item, data.getUserLang());
        MongoAnalytics.setIP(item, data.getUserIp());
        MongoAnalytics.setUserAgent(item, data.getUserAgent());
        MongoAnalytics.setScreen(item, data.getScreen());
        MongoAnalytics.setLatitude(item, data.getLatitude());
        MongoAnalytics.setLongitude(item, data.getLongitude());
        MongoAnalytics.setAccuracy(item, data.getAccuracy());
        MongoAnalytics.setAltitude(item, data.getAltitude());

        // retrieve iplocation if needed, and street address
        this.validateCoords(item);

        super.insert(item);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void validateCoords(final DBObject item) {
        if (null != item) {
            // try to geolocate ip
            if (MongoAnalytics.getLatitude(item) == 0) {
                this.ipLocate(item);
            }

            // get Address
            if (MongoAnalytics.getLatitude(item) > 0) {
                if(StringUtils.hasText(YAHOO_APP_ID) && !YAHOO_APP_ID.equalsIgnoreCase("APP-ID")){
                    final YahooPlaceFinder ypf = new YahooPlaceFinder(YAHOO_APP_ID);
                    final JSONObject location = ypf.reverseGeocoding(MongoAnalytics.getLatitude(item),
                            MongoAnalytics.getLongitude(item));
                    if (null != location) {
                        this.addLocation(item, location);
                    }
                }
            }
        }
    }

    private void ipLocate(final DBObject item) {
        try {
            if (StringUtils.hasText(MongoAnalytics.getIP(item))) {
                final IPLocation location = IPLocator.getInstance().locate(MongoAnalytics.getIP(item));
                if (null != location) {
                    MongoAnalytics.setLatitude(item, ConversionUtils.toDouble(location.getLatitude()));
                    MongoAnalytics.setLongitude(item, ConversionUtils.toDouble(location.getLongitude()));
                }
            }
        } catch (Throwable t) {
        }
    }

    /**
     * Add location data to item
     *
     * @param item
     * @param location {"quality":99,"latitude":"44.005531","longitude":"12.639030","offsetlat":"44.005531",
     *                 "offsetlon":"12.639030","radius":500,"name":"44.00553135 12.63902965",
     *                 "line1":"via Ortles, 5","line2":"47838 Riccione RN","line3":"","line4":"Italy","house":"5",
     *                 "street":"via Ortles","xstreet":"","unittype":"","unit":"","postal":"47838","neighborhood":"",
     *                 "city":"Riccione","county":"Rimini","state":"Emilia Romagna","country":"Italy","countrycode":"IT",
     *                 "statecode":"","countycode":"RN","hash":"","woeid":12846205,"woetype":11,"uzip":"47838"}
     */
    private void addLocation(final DBObject item, final JSONObject location) {
        final JsonWrapper json = JsonWrapper.wrap(location);
        final String street = json.optString("street"); // via Ortles
        final String house = json.optString("house"); // 5
        final String postal = json.optString("postal"); // 47838
        final String city = json.optString("city");  // Riccione
        final String county = json.optString("county"); // Rimini
        final String state = json.optString("state");  // Emilia Romagna
        final String country = json.optString("country"); // Italy
        final String countrycode = json.optString("countrycode"); // IT
        MongoAnalytics.setStreet(item, street);
        MongoAnalytics.setHouse(item, house);
        MongoAnalytics.setZip(item, postal);
        MongoAnalytics.setCity(item, city);
        MongoAnalytics.setCounty(item, county);
        MongoAnalytics.setState(item, state);
        MongoAnalytics.setCountryId(item, country);
        MongoAnalytics.setCountryName(item, countrycode);
    }

    // ------------------------------------------------------------------------
    //                     S T A T I C
    // ------------------------------------------------------------------------

    private static String collection() {
        if (StringUtils.hasText(COLLECTION_NAME) && MongoDBConnectionFactory.hasDBConnection(DB_NAME)) {
            return COLLECTION_NAME;
        }
        return MongoAnalytics.COLLECTION;
    }

    public static void insertNew(final AnalyticsData data) {
        try {
            final MongoAnalyticsService srvc = new MongoAnalyticsService();
            srvc.insert(data);
        } catch (Throwable t) {
            LoggingUtils.getLogger(MongoAnalytics.class).log(Level.SEVERE, null, t);
        }
    }
}
