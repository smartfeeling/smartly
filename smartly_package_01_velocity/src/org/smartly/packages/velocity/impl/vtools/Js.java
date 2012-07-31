package org.smartly.packages.velocity.impl.vtools;

import java.util.Collection;

/**
 *
 */
public class Js {

    public static final String NAME = "js";

    public Js() {

    }

    public String toArray(final Object obj) {
        if (null != obj) {
            final StringBuilder sb = new StringBuilder();
            if (obj.getClass().isArray()) {
                for (final Object item : (Object[]) obj) {
                    if(sb.length()>0){
                        sb.append(",");
                    }
                    sb.append("'").append(null!=item?item.toString():"").append("'");
                }
            } else if (obj instanceof Collection){
                for (final Object item : (Collection) obj) {
                    if(sb.length()>0){
                        sb.append(",");
                    }
                    sb.append("'").append(null!=item?item.toString():"").append("'");
                }
            }
            return "[".concat(sb.toString()).concat("]");
        }
        return "[]";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
