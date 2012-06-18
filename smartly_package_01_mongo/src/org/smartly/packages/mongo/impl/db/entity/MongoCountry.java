/*
 * 
 */
package org.smartly.packages.mongo.impl.db.entity;

import com.mongodb.DBObject;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.mongo.impl.MongoObject;
import org.smartly.packages.mongo.impl.util.MongoUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Country data.
 * 
 * FIELDS:
 * - ID {_id}: country code. i.e. "IT", "GB", "US", "DE"
 * - CURRENCY_CODE {currencycode}: ISO 4217 currency code (http://en.wikipedia.org/wiki/ISO_4217). i.e. "GBP"
 * - CURRENCY_NAME {currencyname}: ISO 4217 currency name. i.e. "pound"
 * - CURRENCY_SYMBOL {currencysymbol}: ISO 4217 currency symbol. i.e. "£"
 * - STORE_TAX {storetax}: Default Tax for current store. i.e. "0.21"
 * - TAXVALUES {taxvalues}: Array ob objects {name:'IVA 21%', value:0.21}
 * - ENABLED {enabled}: True if country is enabled. Remember to disable countries you don't need. 
 * - REGION {array}
 * 
 * @author angelo.geminiani
 */
public class MongoCountry extends MongoObject {
    
    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static String COLLECTION = "countries";
    
    //public static final String ID = ISCMongoConstants.UID;
    public static final String NAME = IMongoEntityConstants.NAME;
    public static final String CURRENCY_CODE = IMongoEntityConstants.CURRENCY_CODE;  
    public static final String CURRENCY_NAME = IMongoEntityConstants.CURRENCY_NAME;  
    public static final String CURRENCY_SYMBOL = IMongoEntityConstants.CURRENCY_SYMBOL;  
    public static final String SEP_DECIMAL = IMongoEntityConstants.SEP_DECIMAL; 
    public static final String SEP_GROUPING = IMongoEntityConstants.SEP_GROUPING; 
    public static final String STORE_TAX = IMongoEntityConstants.STORE_TAX; 
    public static final String ENABLED = IMongoEntityConstants.ENABLED; 
    public static final String IMAGE = IMongoEntityConstants.IMAGE; 
    //-- collections --//
    public static final String REGIONS = IMongoEntityConstants.REGIONS; 
    public static final String TAXVALUES = IMongoEntityConstants.TAXVALUES; 
    
    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------

    public MongoCountry() {
        // no default ID.
        this.append(ENABLED, true);
        this.append(IMAGE, "");
    }
    
    // ------------------------------------------------------------------------
    //                      STATIC
    // ------------------------------------------------------------------------
    
    public static String getId(final DBObject item) {
        return MongoUtils.getString(item, ID);
    }

    public static void setId(final DBObject item, final String value) {
        MongoUtils.put(item, ID, value);
        // add default image image
        if(!StringUtils.hasText(MongoCountry.getImage(item))){
            MongoCountry.setImage(item, "/images/flags/".concat(value.toLowerCase()).concat(".png"));
        }
    }

    public static String getName(final DBObject item) {
        return MongoUtils.getString(item, NAME);
    }

    public static void setName(final DBObject item, final String value) {
        MongoUtils.put(item, NAME, value);
    }

    public static String getCurrencyCode(final DBObject item) {
        return MongoUtils.getString(item, CURRENCY_CODE);
    }

    public static void setCurrencyCode(final DBObject item, final String value) {
        MongoUtils.put(item, CURRENCY_CODE, value);
    }
    
    public static String getCurrencyName(final DBObject item) {
        return MongoUtils.getString(item, CURRENCY_NAME);
    }

    public static void setCurrencyName(final DBObject item, final String value) {
        MongoUtils.put(item, CURRENCY_NAME, value);
    }
    
    public static String getCurrencySymbol(final DBObject item) {
        return MongoUtils.getString(item, CURRENCY_SYMBOL);
    }

    public static void setCurrencySymbol(final DBObject item, final String value) {
        MongoUtils.put(item, CURRENCY_SYMBOL, value);
    }
    
    public static String getSepDecimal(final DBObject item) {
        return MongoUtils.getString(item, SEP_DECIMAL);
    }

    public static void setSepDecimal(final DBObject item, final String value) {
        MongoUtils.put(item, SEP_DECIMAL, value);
    }
    
    public static String getSepGrouping(final DBObject item) {
        return MongoUtils.getString(item, SEP_GROUPING);
    }

    public static void setSepGrouping(final DBObject item, final String value) {
        MongoUtils.put(item, SEP_GROUPING, value);
    }
    
    public static String getImage(final DBObject item) {
        return MongoUtils.getString(item, IMAGE);
    }

    public static void setImage(final DBObject item, final String value) {
        MongoUtils.put(item, IMAGE, value);
    }
    
    public static double getStoreTax(final DBObject item) {
        return MongoUtils.getDouble(item, STORE_TAX, 2, 0.21);
    }

    public static void setStoreTax(final DBObject item, final double value) {
        MongoUtils.put(item, STORE_TAX, value);
    }
    
    public static boolean getEnabled(final DBObject item) {
        return MongoUtils.getBoolean(item, ENABLED, false);
    }

    public static void setEnabled(final DBObject item, final boolean value) {
        MongoUtils.put(item, ENABLED, value);
    }
    
    public static List<DBObject> getRegions(final DBObject item) {
        return MongoUtils.getList(item, REGIONS);
    }

    public static void setRegions(final DBObject item, final List<DBObject> value) {
        MongoUtils.put(item, REGIONS, value);
    }
    
    
    public static List<DBObject> getTaxValues(final DBObject item) {
        return MongoUtils.getList(item, TAXVALUES);
    }

    public static void setTaxValues(final DBObject item, final List<DBObject> value) {
        MongoUtils.put(item, TAXVALUES, value);
    }
    
    public static void setTaxValues(final DBObject item, final String[] value) {
        final List<DBObject> values = new LinkedList<DBObject>();
        for(final String jsontxt:value){
            final DBObject val = MongoUtils.parseObject(jsontxt);
            values.add(val);
        }
        setTaxValues(item, values);
    }
}
