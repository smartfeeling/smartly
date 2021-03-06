package org.smartly.packages.velocity.impl.vtools;

import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.vtools.lang.VLCString;

/**
 * String utilities
 */
public class StringTool {

    // ------------------------------------------------------------------------
    //                      Constants
    // ------------------------------------------------------------------------
    public static final String NAME = "string";


    public StringTool() {
    }

    public VLCString newString(){
        return new VLCString();
    }

    public String concat(final Object...args){
        return StringUtils.concatArgs(args);
    }

    public String concatEx(final String sep, final Object...args){
        return StringUtils.concatArgsEx(sep, args);
    }

    public String concatDot(final Object...args){
        return StringUtils.concatDot(args);
    }

    public String concatComma(final Object...args){
        return StringUtils.concatArgsEx(",", args);
    }

    public String toUpperCase(final Object arg){
        if(null!=arg){
           return arg.toString().toUpperCase();
        }
        return "";
    }

    public String toLowerCase(final Object arg){
        if(null!=arg){
            return arg.toString().toLowerCase();
        }
        return "";
    }

    public String replaceCR(final Object text, final Object with){
        return this.replace(text, "\n", with);
    }

    public String replace(final Object text, final Object what, final Object with){
        if(text instanceof String){
            if(null!=what){
               return StringUtils.replace(text.toString(),
                       what.toString(),
                       null!=with?with.toString():"");
            }
            return text.toString();
        }
        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
