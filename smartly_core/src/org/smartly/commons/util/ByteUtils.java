/*
 * 
 */
package org.smartly.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 *
 * @author angelo.geminiani
 */
public class ByteUtils {

    public static boolean isByteArray(final Object data){
        if (data.getClass().isArray()) {
            final Object val = Array.get(data, 0);
            if (val instanceof Byte) {
                return true;
            }
        }
        return false;
    }

    public static byte[] getBytes(final InputStream is) throws IOException {
        return getBytes(is, 1024);
    }

    public static byte[] getBytes(final InputStream is, final int bufferSize) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
        try {
            byte[] buffer = new byte[bufferSize];
            int len;

            while ((len = is.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            out.close();
        }
        return out.toByteArray();
    }

    public static byte[] optBytes(final InputStream is) {
        return optBytes(is, 1024);
    }

    public static byte[] optBytes(final InputStream is, final int bufferSize) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
            try {
                byte[] buffer = new byte[bufferSize];
                int len;

                while ((len = is.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }
            } finally {
                out.close();
            }
            return out.toByteArray();
        } catch (Throwable t) {
        }
        return new byte[0];
    }

    public static byte[] getDataNotAvailable(){
        try{
            return getBytes(ClassLoaderUtils.getResourceAsStream(null, ByteUtils.class, "nodata.png"));
        }catch(Throwable ignored){
        }
        return new byte[0];
    }

}
