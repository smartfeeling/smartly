package org.smartly.packages.velocity.impl.vtools;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.JsonWrapper;

import java.util.Collection;
import java.util.Map;

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

    public String toString(final Object item) {
        return null!=item ? item.toString():"";
    }

    public boolean toBoolean(final Object item){
        return ConversionUtils.toBoolean(item);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
