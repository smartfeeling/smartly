/*
 * 
 */
package org.smartly.commons.cryptograph;

import org.smartly.commons.lang.Base64;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Simple DES encoder/decoder
 *
 * @author angelo.geminiani
 */
public final class _DESEncrypter {

    private Cipher _ecipher;
    private Cipher _dcipher;

    public _DESEncrypter(final String textkey) {
        try {
            final SecretKey key = this.createKeyFromCleartext(textkey);
            this.init(key);
        } catch (Throwable t) {
        }
    }

    public _DESEncrypter() {
        try {
            final SecretKey key = KeyGenerator.getInstance("DES").generateKey();
            this.init(key);
        } catch (Throwable t) {
        }
    }

    public _DESEncrypter(final SecretKey key) {
        this.init(key);
    }

    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            return this.encrypt(utf8);
        } catch (Throwable t) {
        }
        return null;
    }

    public String encrypt(byte[] data) {
        try {
            // Encrypt
            byte[] enc = _ecipher.doFinal(data);

            // Encode bytes to base64 to get a string
            return Base64.encodeBytes(enc);
        } catch (Throwable t) {
        }
        return null;
    }

    public byte[] decrypt(final String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = Base64.decode(str);

            return this.decrypt(dec);
        } catch (Throwable t) {
        }
        return null;
    }

    public byte[] decrypt(final byte[] data) {
        try {
            // Decrypt
            byte[] utf8 = _dcipher.doFinal(data);

            // Decode using utf-8
            return utf8;
        } catch (Throwable t) {
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init(final SecretKey key) {
        try {
            _ecipher = Cipher.getInstance("DES");
            _dcipher = Cipher.getInstance("DES");
            _ecipher.init(Cipher.ENCRYPT_MODE, key);
            _dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    /**
     * Creates 8 byte secret key.
     *
     * @param cleartext 8 byte string
     * @return
     */
    private SecretKey createKeyFromCleartext(final String cleartext) {
        final byte[] bytes = cleartext.getBytes();
        final SecretKeySpec sk = new SecretKeySpec(bytes, "DES");
        return sk;
    }
}
