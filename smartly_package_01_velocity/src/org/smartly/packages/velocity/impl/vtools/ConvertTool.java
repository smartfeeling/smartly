package org.smartly.packages.velocity.impl.vtools;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.util.URLEncodeUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Conversion utility
 */
public class ConvertTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "convert";

    public ConvertTool() {

    }

    // --------------------------------------------------------------------
    //               encode / decode
    // --------------------------------------------------------------------

    public void encodeAll(final Object data) {
        if(null!=data){
            if (data instanceof Map) {
                final Map map = (Map)data;
                final Set keys = map.keySet();
                for (final Object key : keys) {
                    map.put(key, this.encode(map.get(key)));
                }
            } else if (data instanceof JSONObject) {
                final JsonWrapper json = new JsonWrapper((JSONObject)data);
                this.encodeAll(json);
            } else if (data instanceof JsonWrapper) {
                final JsonWrapper json = (JsonWrapper)data;
                final Set<String> keys = json.keys();
                for (final String key : keys) {
                    json.putSilent(key, this.encode(json.opt(key)));
                }
            } else if (data instanceof JSONArray) {
                final Object[] array = JsonWrapper.toArray((JSONArray) data);
                for (final Object item : array) {
                    this.encodeAll(item);
                }
            } else if(data.getClass().isArray()){
                final Object[] array = (Object[]) data;
                for (final Object item : array) {
                    this.encodeAll(item);
                }
            } else if(data instanceof Collection){
                final Collection array = (Collection) data;
                for (final Object item : array) {
                    this.encodeAll(item);
                }
            }
        }
    }

    public void decodeAll(final Object data) {
        if(null!=data){
            if (data instanceof Map) {
                final Map map = (Map)data;
                final Set keys = map.keySet();
                for (final Object key : keys) {
                    map.put(key, this.decode(map.get(key)));
                }
            } else if (data instanceof JSONObject) {
                final JsonWrapper json = new JsonWrapper((JSONObject)data);
                this.decodeAll(json);
            } else if (data instanceof JsonWrapper) {
                final JsonWrapper json = (JsonWrapper)data;
                final Set<String> keys = json.keys();
                for (final String key : keys) {
                    json.putSilent(key, this.decode(json.opt(key)));
                }
            } else if (data instanceof JSONArray) {
                final Object[] array = JsonWrapper.toArray((JSONArray) data);
                for (final Object item : array) {
                    this.decodeAll(item);
                }
            } else if(data.getClass().isArray()){
                final Object[] array = (Object[]) data;
                for (final Object item : array) {
                    this.decodeAll(item);
                }
            } else if(data instanceof Collection){
                final Collection array = (Collection) data;
                for (final Object item : array) {
                    this.decodeAll(item);
                }
            }
        }
    }

    public String encode(final Object data) {
        return this.encodeURIComponent(data);
    }

    public String encodeURIComponent(final Object data) {
        if (null != data) {
            return URLEncodeUtils.encodeURI(data.toString());
        }
        return "";
    }

    public String decode(final Object data) {
        return this.decodeURIComponent(data);
    }

    public String decodeURIComponent(final Object data) {
        if (null != data) {
            return URLEncodeUtils.decodeURI(data.toString());
        }
        return "";
    }

    public String encodeHTML(final Object data) {
        if (null != data) {
            return URLEncodeUtils.encodeHTML(data.toString(), null);
        }
        return "";
    }

    public String decodeHTML(final Object data) {
        if (null != data) {
            return URLEncodeUtils.decodeHTML(data.toString());
        }
        return "";
    }

    public String toHTML(final String text) {
        final StringBuilder result = new StringBuilder();
        if (StringUtils.hasText(text)) {
            final String[] lines = StringUtils.split(text, "\n");
            for (final String line : lines) {
                if (result.length() > 0) {
                    result.append("<br/>");
                    result.append("\n");
                }
                // split for a title
                final String[] tokens = StringUtils.splitFirst(line, ":");
                if (tokens.length > 1) {
                    result.append("<span style='font-weight:bold;'>");
                    result.append(tokens[0]).append(":&nbsp;");
                    result.append("</span>");
                    result.append(tokens[1]);
                } else {
                    result.append(line);
                }
            }
        }
        return result.toString();
    }

    // --------------------------------------------------------------------
    //               type conversion
    // --------------------------------------------------------------------

    public String toString(final Object item) {
        return null != item ? item.toString() : "";
    }

    public boolean toBoolean(final Object item) {
        return ConversionUtils.toBoolean(item);
    }

    public int toInt() {
        return this.toInt(-1);
    }

    public int toInt(final Object value) {
        return this.toInt(value, -1);
    }

    public int toInt(final Object value, final int defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.intValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    public double toDouble() {
        return this.toDouble(-1.0);
    }

    public double toDouble(final Object value) {
        return this.toDouble(value, -1.0);
    }

    public double toDouble(final Object value, final double defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.doubleValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    public long toLong() {
        return this.toLong(-1);
    }

    public long toLong(final Object value) {
        return this.toLong(value, -1);
    }

    public long toLong(final Object value, final int defaultValue) {
        try {
            final Number n = ConversionUtils.toNumber(value);
            return n.longValue();
        } catch (Throwable ignored) {
            return defaultValue;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
