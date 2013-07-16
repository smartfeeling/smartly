package org.smartly.commons.network.socket.server.tools;

import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.util.PathUtils;

/**
 * Utility methods for multipart messages.
 */
public class MultipartMessageUtils {

    public static final String STORE = PathUtils.getTemporaryDirectory("chunks");

    /**
     * Save part on temp folder
     *
     * @param part
     */
    public static void save(final MultipartMessagePart part) {


    }

}
