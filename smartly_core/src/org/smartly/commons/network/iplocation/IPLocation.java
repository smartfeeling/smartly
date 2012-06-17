/*
 * 
 */
package org.smartly.commons.network.iplocation;

import org.json.JSONObject;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.StringUtils;

/**
 * @author angelo.geminiani
 */
public final class IPLocation {

    private static final String COUNTRY = "country";
    private static final String IP = "ip";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String COUNTRYNAME = "countryname";
    private static final String COUNTRYCODE = "countrycode";
    private static final String CITY = "city";
    private String _ip;
    private String _latitude;
    private String _longitude;
    private String _countryName;
    private String _countryCode;
    private String _city;

    public IPLocation(final String content) {
        this.parse(content);
    }

    public String getIp() {
        return _ip;
    }

    public void setIp(String ip) {
        this._ip = ip;
    }

    public String getLatitude() {
        return _latitude;
    }

    public void setLatitude(String latitude) {
        this._latitude = latitude;
    }

    public String getLongitude() {
        return _longitude;
    }

    public void setLongitude(String longitude) {
        this._longitude = longitude;
    }

    public String getCountryName() {
        return _countryName;
    }

    public void setCountryName(String countryName) {
        this._countryName = countryName;
    }

    public String getCountryCode() {
        return _countryCode;
    }

    public void setCountryCode(String countryCode) {
        this._countryCode = countryCode;
    }

    public String getCity() {
        return _city;
    }

    public void setCity(String city) {
        this._city = city;
    }

    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        try {
            result.putOpt(IP, this.getIp());
            result.putOpt(CITY, this.getCity());
            result.putOpt(COUNTRYCODE, this.getCountryCode());
            result.putOpt(COUNTRYNAME, this.getCountryName());
            result.putOpt(LATITUDE, this.getLatitude());
            result.putOpt(LONGITUDE, this.getLongitude());
        } catch (Throwable t) {
        }

        return result;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    /**
     * Parse text like:
     * Country: UNITED STATES (US)
     * City: Sugar Grove, IL
     * Latitude: 41.7696
     * Longitude: -88.4588
     * IP: 12.215.42.19
     *
     * @param response
     * @return
     */
    private void parse(final String response) {
        if (StringUtils.hasText(response)) {
            final String[] tokens = StringUtils.split(
                    response, "\n", true, false, 1);
            for (final String token : tokens) {
                final String[] keypair = StringUtils.split(
                        token, ":");
                if (keypair.length > 1) {
                    final String name = keypair[0].toLowerCase();
                    final String value = keypair[1];
                    try {
                        if (COUNTRY.equalsIgnoreCase(name)) {
                            final String[] temp = StringUtils.split(
                                    value.replace("(", "|").replace(")", ""),
                                    "|", true, false, 1);
                            if (temp.length == 2) {
                                this.setCountryName(temp[0]);
                                this.setCountryCode(temp[1]);
                            } else {
                                this.setCountryName(value);
                                this.setCountryCode("");
                            }
                        } else {
                            BeanUtils.setValue(this, name, value);
                        }
                    } catch (Throwable t) {
                        this.getLogger().log(Level.SEVERE, null, t);
                    }
                }
            }
        } else {
            this.setCity("");
            this.setCountryCode("");
            this.setCountryName("");
            this.setIp("0:0:0:0:0:0:0:1");
            this.setLatitude("0.0");
            this.setLongitude("0.0");
        }
    }
}
