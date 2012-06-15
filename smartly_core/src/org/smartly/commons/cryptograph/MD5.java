/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.smartly.commons.cryptograph;

/**
 * @author angelo.geminiani
 */
public class MD5 {

    private MD5() {
    }

    public static String encode(final String text,
                                final String opvalue) {
        try {
            final SecurityMessageDigester instance = new SecurityMessageDigester(
                    ICryptographConstants.AlgorithmMessageDigest.MD5);
            return instance.getEncodedText(text);
        } catch (Throwable t) {
            return opvalue;
        }
    }

    public static String encode(final String text) {
        return encode(text, "");
    }

}
