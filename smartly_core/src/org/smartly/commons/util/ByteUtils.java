/*
 * 
 */
package org.smartly.commons.util;

import java.io.*;
import java.lang.reflect.Array;

/**
 * @author angelo.geminiani
 */
public class ByteUtils {

    public static boolean isByteArray(final Object data) {
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
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    public static byte[] getBytes(final Object data) throws IOException {
        byte[] result = new byte[0];
        if (!isByteArray(data)) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(data);
                result = bos.toByteArray();
            } finally {
                if (null != out) out.close();
                bos.close();
            }
        } else {
            result = (byte[]) data;
        }
        return result;
    }

    public static byte[] optBytes(final Object data) {
        try {
            return getBytes(data);
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

    public static Object getObject(final byte[] data)
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return  in.readObject();
        } finally {
            if (null != in) in.close();
            bis.close();
        }
    }

    public static Object optObject(final byte[] data) {
        try {
            return getObject(data);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static byte[] getDataNotAvailable() {
        try {
            return getBytes(ClassLoaderUtils.getResourceAsStream(null, ByteUtils.class, "nodata.png"));
        } catch (Throwable ignored) {
        }
        return new byte[0];
    }

}
