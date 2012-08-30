package org.smartly.commons.network.api.yahoo.placefinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.URLUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Yahoo PlaceFinder implementation.
 * http://developer.yahoo.com/geo/placefinder/
 * http://developer.yahoo.com/geo/placefinder/guide/
 * http://developer.yahoo.com/geo/placefinder/guide/examples.html
 * <p/>
 * flags=J&appid=yourappid
 * <p/>
 * User: angelo.geminiani
 */
public class YahooPlaceFinder {

    private final String _appId;
    private static final String FLD_RESULTSET = "ResultSet";
    private static final String FLD_ERROR = FLD_RESULTSET + ".Error";
    private static final String FLD_ERRORMSG = FLD_RESULTSET + ".ErrorMessage";
    private static final String FLD_RESULTS = FLD_RESULTSET + ".Results";
    //-- patterns --//
    private static final String REVERSE_GEOCODING = "http://where.yahooapis.com/geocode?locale={locale}&location={latitude}+{longitude}&gflags=R&flags=J&appid={appid}";
    private static final String GEOCODING = "http://where.yahooapis.com/geocode?locale={locale}&location={address}&flags=J&appid={appid}";
    private static final String GEOCODING_COORD = "http://where.yahooapis.com/geocode?locale={locale}&location={address}&flags=JC&appid={appid}";

    public YahooPlaceFinder(final String appId) {
        _appId = appId;
    }

    /**
     * Returns Address object.
     * i.e. {"quality":99,"latitude":"44.005531","longitude":"12.639030","offsetlat":"44.005531",
     * "offsetlon":"12.639030","radius":500,"name":"44.00553135 12.63902965",
     * "line1":"via Ortles, 5","line2":"47838 Riccione RN","line3":"","line4":"Italy","house":"5",
     * "street":"via Ortles","xstreet":"","unittype":"","unit":"","postal":"47838","neighborhood":"",
     * "city":"Riccione","county":"Rimini","state":"Emilia Romagna","country":"Italy","countrycode":"IT",
     * "statecode":"","countycode":"RN","hash":"","woeid":12846205,"woetype":11,"uzip":"47838"}
     * <p/>
     * FULL OBJECT:
     * {
     * "ResultSet":
     * {
     * "version":"1.0",
     * "Error":0,
     * "ErrorMessage":"No error",
     * "Locale":"us_US",
     * "Quality":99,
     * "Found":1,
     * "Results":[
     * {"quality":99,"latitude":"44.005531","longitude":"12.639030","offsetlat":"44.005531",
     * "offsetlon":"12.639030","radius":500,"name":"44.00553135 12.63902965",
     * "line1":"via Ortles, 5","line2":"47838 Riccione RN","line3":"","line4":"Italy","house":"5",
     * "street":"via Ortles","xstreet":"","unittype":"","unit":"","postal":"47838","neighborhood":"",
     * "city":"Riccione","county":"Rimini","state":"Emilia Romagna","country":"Italy","countrycode":"IT",
     * "statecode":"","countycode":"RN","hash":"","woeid":12846205,"woetype":11,"uzip":"47838"}
     * ]}
     * }
     *
     * @param latitude
     * @param longitude
     * @return Address Object:
     *         {"quality":99,"latitude":"44.005531","longitude":"12.639030","offsetlat":"44.005531",
     *         "offsetlon":"12.639030","radius":500,"name":"44.00553135 12.63902965",
     *         "line1":"via Ortles, 5","line2":"47838 Riccione RN","line3":"","line4":"Italy","house":"5",
     *         "street":"via Ortles","xstreet":"","unittype":"","unit":"","postal":"47838","neighborhood":"",
     *         "city":"Riccione","county":"Rimini","state":"Emilia Romagna","country":"Italy","countrycode":"IT",
     *         "statecode":"","countycode":"RN","hash":"","woeid":12846205,"woetype":11,"uzip":"47838"}
     */
    public JSONObject reverseGeocoding(final double latitude, final double longitude) {
        return this.reverseGeocoding("en_US", latitude, longitude);
    }

    public JSONObject reverseGeocoding(final String locale, final double latitude, final double longitude) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("locale", locale);
            params.put("appid", _appId);
            params.put("latitude", latitude);
            params.put("longitude", longitude);
            final String url = FormatUtils.format(REVERSE_GEOCODING, params);
            final String jsonData = URLUtils.getUrlContent(url);
            return parseResponse(jsonData);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    public JSONObject geocoding(final String locale, final JSONObject address) {
        final String text_address = getAddress(address, false);
        return this.geocoding(locale, text_address);
    }

    /**
     * http://where.yahooapis.com/geocode?location=San+Francisco,+CA&flags=J&appid=yourappid
     *
     * @param address format: First line of address (street address), a comma, and the second line of address (city-state-zip in US).
     * @return json
     */
    public JSONObject geocoding(final String locale, final String address) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("locale", locale);
            params.put("appid", _appId);
            params.put("address", address);
            // http://where.yahooapis.com/geocode?locale=it_IT&location=riccione rn 47838 IT&flags=J&appid=APPID
            final String url = FormatUtils.format(GEOCODING, params);
            final String jsonData = URLUtils.getUrlContent(url, 5000, URLUtils.TYPE_JSON);
            return parseResponse(jsonData);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    public JSONObject coordinates(final String locale, final JSONObject address) {
        return this.coordinates(locale, getAddress(address, false));
    }

    public JSONObject coordinates (final String locale, final String address) {
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("locale", locale);
            params.put("appid", _appId);
            params.put("address", address);
            // http://where.yahooapis.com/geocode?locale=it_IT&location=riccione rn 47838 IT&flags=JC&appid=APPID
            final String url = FormatUtils.format(GEOCODING_COORD, params);
            final String jsonData = URLUtils.getUrlContent(url);
            return parseResponse(jsonData);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------



    // --------------------------------------------------------------------
    //               S T A T I C  -  p r i v a t e
    // --------------------------------------------------------------------

    private static JSONObject parseResponse(final String jsonData) throws Exception {
        if (StringUtils.isJSON(jsonData)) {
            final JSONObject json = new JSONObject(jsonData);
            final int err = getErrorCode(json);
            if (err == 0) {
                final JSONArray results = getResults(json);
                if (null != results && results.length() > 0) {
                    return results.optJSONObject(0);
                } else {
                    // system error
                    final String sys_err = JsonWrapper.getString(json, "error");
                    if(StringUtils.hasText(sys_err)){
                        final String cause = JsonWrapper.getString(json, "error_message");
                        if(StringUtils.hasText(cause)){
                            throw new Exception("[" + cause + "] - " + sys_err);
                        }
                        throw new Exception(sys_err);
                    }
                }
            } else {
                throw new Exception(getErrorMessage(json));
            }
            return json;
        }
        return null;
    }

    private static int getErrorCode(final JSONObject json) {
        return JsonWrapper.getInt(json, FLD_ERROR);
    }

    private static String getErrorMessage(final JSONObject json) {
        return JsonWrapper.getString(json, FLD_ERRORMSG);
    }

    private static boolean hasError(final JSONObject json) {
        return getErrorCode(json) > 0;
    }

    private static JSONArray getResults(final JSONObject json) {
        return JsonWrapper.getArray(json, FLD_RESULTS);
    }

    private static String getAddress(final JSONObject address, final boolean includeStreet) {
        // format: (street address), a comma, and (city-state-zip in US).
        final String street = address.optString("street").replaceAll(",", "");
        final String city = address.optString("city");
        final String state = address.optString("state");
        final String country = address.optString("country");
        final String zip = address.optString("zip");
        final StringBuilder sb = new StringBuilder();
        if(StringUtils.hasText(city) ||
                StringUtils.hasText(state) ||
                StringUtils.hasText(zip) ||
                StringUtils.hasText(country)){
            if(includeStreet && StringUtils.hasText(street)){
                sb.append(street);
                sb.append(",");
            }
            if(StringUtils.hasText(city)){
                sb.append(city).append(" ");
            }
            if(StringUtils.hasText(state)){
                sb.append(state).append(" ");
            }
            if(StringUtils.hasText(zip)){
                sb.append(zip).append(" ");
            }
            if(StringUtils.hasText(country)){
                sb.append(country);
            }
        }

        return sb.toString().replaceAll(" ", "+");
    }
}
