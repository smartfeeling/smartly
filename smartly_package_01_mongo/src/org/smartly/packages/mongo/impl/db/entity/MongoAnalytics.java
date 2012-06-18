/*
 * 
 */
package org.smartly.packages.mongo.impl.db.entity;

import com.mongodb.DBObject;
import org.smartly.commons.util.RandomUtils;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author angelo.geminiani@gmail.com
 */
public class MongoAnalytics extends MongoObject {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String COLLECTION = "analytics";
    //
    //fields
    //public static final String ID = IPeristenceConstants.ID;
    public static final String DATETIME = IMongoEntityConstants.DATETIME;
    public static final String YEAR = IMongoEntityConstants.YEAR;
    public static final String MONTH = IMongoEntityConstants.MONTH;
    public static final String DAY = IMongoEntityConstants.DAY;
    public static final String HOUR = IMongoEntityConstants.HOUR;
    public static final String MINUTE = IMongoEntityConstants.MINUTE;
    public static final String USERID = IMongoEntityConstants.USER_ID;
    public static final String LANG = IMongoEntityConstants.LANG;
    public static final String SCREEN = IMongoEntityConstants.SCREEN;
    public static final String IP = IMongoEntityConstants.IP;
    public static final String USERAGENT = IMongoEntityConstants.USERAGENT;
    public static final String LATITUDE = IMongoEntityConstants.LATITUDE;
    public static final String LONGITUDE = IMongoEntityConstants.LONGITUDE;
    public static final String ALTITUDE = IMongoEntityConstants.ALTITUDE;
    public static final String ACCURACY = IMongoEntityConstants.ACCURACY;
    public static final String STREET = IMongoEntityConstants.STREET;
    public static final String HOUSE = IMongoEntityConstants.HOUSE;
    public static final String ZIP = IMongoEntityConstants.ZIP;
    public static final String CITY = IMongoEntityConstants.CITY;
    public static final String COUNTY = IMongoEntityConstants.COUNTY;
    public static final String STATE = IMongoEntityConstants.STATE;
    public static final String COUNTRY_NAME = IMongoEntityConstants.COUNTRY_NAME;
    public static final String COUNTRY_ID = IMongoEntityConstants.COUNTRY_ID;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public MongoAnalytics() {
        this.init();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init() {
        final Calendar calendar = Calendar.getInstance();
        final Date now = calendar.getTime();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        MongoAnalytics.setDateTime(this, now);
        MongoAnalytics.setYear(this, year);
        MongoAnalytics.setMonth(this, month);
        MongoAnalytics.setDay(this, day);
        MongoAnalytics.setHour(this, hour);
        MongoAnalytics.setMinute(this, minute);
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    public static String getId(final DBObject item) {
        return MongoUtils.getString(item, ID);
    }

    public static void setId(final DBObject item, final String value) {
        final String id = value.concat("_").concat(RandomUtils.random(6, RandomUtils.CHARS_NUMBERS));
        MongoUtils.put(item, ID, id);
    }

    public static Date getDateTime(final DBObject item) {
        final long dt = MongoUtils.getLong(item, DATETIME);
        return new Date(dt);
    }

    public static void setDateTime(final DBObject item, final Date value) {
        MongoUtils.put(item, DATETIME, value.getTime());
    }

    public static int getYear(final DBObject item) {
        return MongoUtils.getInt(item, YEAR);
    }

    public static void setYear(final DBObject item, final int value) {
        MongoUtils.put(item, YEAR, value);
    }

    public static int getMonth(final DBObject item) {
        return MongoUtils.getInt(item, MONTH);
    }

    public static void setMonth(final DBObject item, final int value) {
        MongoUtils.put(item, MONTH, value);
    }

    public static int getDay(final DBObject item) {
        return MongoUtils.getInt(item, DAY);
    }

    public static void setDay(final DBObject item, final int value) {
        MongoUtils.put(item, DAY, value);
    }

    public static int getHour(final DBObject item) {
        return MongoUtils.getInt(item, HOUR);
    }

    public static void setHour(final DBObject item, final int value) {
        MongoUtils.put(item, HOUR, value);
    }

    public static int getMinute(final DBObject item) {
        return MongoUtils.getInt(item, MINUTE);
    }

    public static void setMinute(final DBObject item, final int value) {
        MongoUtils.put(item, MINUTE, value);
    }

    public static String getUserId(final DBObject item) {
        return MongoUtils.getString(item, USERID);
    }

    public static void setUserId(final DBObject item, final String value) {
        MongoUtils.put(item, USERID, value);
    }

    public static String getLang(final DBObject item) {
        return MongoUtils.getString(item, LANG);
    }

    public static void setLang(final DBObject item, final String value) {
        MongoUtils.put(item, LANG, value);
    }

    public static String getIP(final DBObject item) {
        return MongoUtils.getString(item, IP);
    }

    public static void setIP(final DBObject item, final String value) {
        MongoUtils.put(item, IP, value);
    }

    public static String getUserAgent(final DBObject item) {
        return MongoUtils.getString(item, USERAGENT);
    }

    public static void setUserAgent(final DBObject item, final String value) {
        MongoUtils.put(item, USERAGENT, value);
    }

    public static String getScreen(final DBObject item) {
        return MongoUtils.getString(item, SCREEN);
    }

    public static void setScreen(final DBObject item, final String value) {
        MongoUtils.put(item, SCREEN, value);
    }

    public static double getLatitude(final DBObject item) {
        return MongoUtils.getDouble(item, LATITUDE);
    }

    public static void setLatitude(final DBObject item, final double value) {
        MongoUtils.put(item, LATITUDE, value);
    }

    public static double getLongitude(final DBObject item) {
        return MongoUtils.getDouble(item, LONGITUDE);
    }

    public static void setLongitude(final DBObject item, final double value) {
        MongoUtils.put(item, LONGITUDE, value);
    }

    public static double getAltitude(final DBObject item) {
        return MongoUtils.getDouble(item, ALTITUDE);
    }

    public static void setAltitude(final DBObject item, final double value) {
        MongoUtils.put(item, ALTITUDE, value);
    }

    public static int getAccuracy(final DBObject item) {
        return MongoUtils.getInt(item, ACCURACY);
    }

    public static void setAccuracy(final DBObject item, final int value) {
        MongoUtils.put(item, ACCURACY, value);
    }

    public static String getStreet(final DBObject item) {
        return MongoUtils.getString(item, STREET);
    }

    public static void setStreet(final DBObject item, final String value) {
        MongoUtils.put(item, STREET, value);
    }

    public static String getHouse(final DBObject item) {
        return MongoUtils.getString(item, HOUSE);
    }

    public static void setHouse(final DBObject item, final String value) {
        MongoUtils.put(item, HOUSE, value);
    }

    public static String getZip(final DBObject item) {
        return MongoUtils.getString(item, ZIP);
    }

    public static void setZip(final DBObject item, final String value) {
        MongoUtils.put(item, ZIP, value);
    }

    public static String getCity(final DBObject item) {
        return MongoUtils.getString(item, CITY);
    }

    public static void setCity(final DBObject item, final String value) {
        MongoUtils.put(item, CITY, value);
    }

    public static String getCounty(final DBObject item) {
        return MongoUtils.getString(item, COUNTY);
    }

    public static void setCounty(final DBObject item, final String value) {
        MongoUtils.put(item, COUNTY, value);
    }

    public static String getState(final DBObject item) {
        return MongoUtils.getString(item, STATE);
    }

    public static void setState(final DBObject item, final String value) {
        MongoUtils.put(item, STATE, value);
    }

    public static String getCountryId(final DBObject item) {
        return MongoUtils.getString(item, COUNTRY_ID);
    }

    public static void setCountryId(final DBObject item, final String value) {
        MongoUtils.put(item, COUNTRY_ID, value);
    }

    public static String getCountryName(final DBObject item) {
        return MongoUtils.getString(item, COUNTRY_NAME);
    }

    public static void setCountryName(final DBObject item, final String value) {
        MongoUtils.put(item, COUNTRY_NAME, value);
    }
}
