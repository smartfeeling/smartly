package org.smartly.commons.network;

import org.smartly.commons.lang.Base64;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.RandomUtils;
import org.smartly.commons.util.RegExUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for Avatar Image.
 *
 * Gravatar is not supported for base64 and bytes, because require a redirect to gravatar site
 */
public class AvatarUtils {

    //-- robohash avatars --//
    public static final String ROBO_HASH = "http://robohash.org/{0}";
    public static final String ROBO_HASH_GRAVATAR = ROBO_HASH.concat("?gravatar=yes");

    private static final String OPT_GRAVATAR = "gravatar=yes";
    private static final String OPT_IMG_SET = "set=set{0}";
    private static final String OPT_BG_SET = "bgset=bg{0}";
    private static final String OPT_IMG_SET2 = "set=set2";
    private static final String OPT_IMG_SET3 = "set=set3";
    private static final String OPT_SIZE = "size={h}x{w}";

    public static String getAvatarUrl(final String email,
                                      final boolean useGravatar,
                                      final int imgSet,
                                      final int bgSet,
                                      final int height,
                                      final int width) {
        final String avatar_email = RegExUtils.isValidEmail(email)
                ? email
                : RandomUtils.random(6, RandomUtils.CHARS_LOW_NUMBERS) + "@email.com";
        final String base_url = FormatUtils.format(ROBO_HASH, avatar_email);

        //-- options --//
        final StringBuilder options = new StringBuilder();
        if (useGravatar) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(OPT_GRAVATAR);
        }

        if (imgSet>1) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_IMG_SET, imgSet));
        }

        if (bgSet>0) {
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_BG_SET, bgSet));
        }

        if(height>0 && width>0){
            if (options.length() == 0) {
                options.append("?");
            } else {
                options.append("&");
            }
            options.append(FormatUtils.format(OPT_SIZE, getSize(height, width)));
        }

        return base_url.concat(options.toString());
    }

    public static byte[] getAvatar(final String email,
                                   final int imgSet,
                                   final int bgSet,
                                   final int height,
                                   final int width) {
        try {
            final String url = getAvatarUrl(email, false, imgSet, bgSet, height, width);

            final InputStream is = URLUtils.getInputStream(url, 3000, URLUtils.TYPE_ALL);
            try {
                return ByteUtils.getBytes(is);
            } finally {
                is.close();
            }
        } catch (final Throwable t) {
            LoggingUtils.getLogger(AvatarUtils.class).log(Level.SEVERE, null, t);
        }
        return new byte[0];
    }

    public static String getAvatarBase64(final String email,
                                         final int imgSet,
                                         final int bgSet,
                                         final int height,
                                         final int width) {
        final byte[] bytes = getAvatar(email, imgSet, bgSet, height, width);
        return Base64.encodeBytes(bytes);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static Map<String, Object> getSize(final int h, final int w) {
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("h", h);
        result.put("w", w);
        return result;
    }

}
