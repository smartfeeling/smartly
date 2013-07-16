/*
 * 
 */
package org.smartly.commons.cryptograph;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Simple AES encoder/decoder
 *
 * @author angelo.geminiani
 */
public final class AESEncrypter {


    private Cipher _ecipher;
    private Cipher _dcipher;

    public AESEncrypter(final String textkey) {
        try {
            final SecretKey key = this.createKeyFromCleartext(textkey);
            this.init(key);
        } catch (Throwable t) {
        }
    }

    public AESEncrypter(final SecretKey key) {
        this.init(key);
    }

    public byte[] encrypt(final byte[] data) {
        try {
            return _ecipher.doFinal(data);
        } catch (Throwable ignored) {
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
            _ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            _ecipher.init(Cipher.ENCRYPT_MODE, key);
            _dcipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
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
        final SecretKeySpec sk = new SecretKeySpec(bytes, "AES");
        return sk;
    }
}
