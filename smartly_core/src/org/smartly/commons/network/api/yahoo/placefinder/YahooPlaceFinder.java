package org.smartly.commons.network.api.yahoo.placefinder;

import org.json.JSONArray;
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
    private static final String REVERSE_GEOCODING = "http://where.yahooapis.com/geocode?location={latitude}+{longitude}&gflags=R&flags=J&appid={appid}";

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
        try {
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("appid", _appId);
            params.put("latitude", latitude);
            params.put("longitude", longitude);
            final String url = FormatUtils.format(REVERSE_GEOCODING, params);
            final String jsonData = URLUtils.getUrlContent(url);
            if (StringUtils.isJSON(jsonData)) {
                final JSONObject json = new JSONObject(jsonData);
                if (null != json) {
                    final int err = this.getErrorCode(json);
                    if (err == 0) {
                        final JSONArray results = this.getResults(json);
                        if (null != results && results.length() > 0) {
                            return results.optJSONObject(0);
                        }
                    } else {
                        throw new Exception(this.getErrorMessage(json));
                    }
                }
                return json;
            }
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

    private int getErrorCode(final JSONObject json) {
        return JsonWrapper.getInt(json, FLD_ERROR);
    }

    private String getErrorMessage(final JSONObject json) {
        return JsonWrapper.getString(json, FLD_ERRORMSG);
    }

    private boolean hasError(final JSONObject json) {
        return this.getErrorCode(json) > 0;
    }

    private JSONArray getResults(final JSONObject json) {
        return JsonWrapper.getArray(json, FLD_RESULTS);
    }
}
