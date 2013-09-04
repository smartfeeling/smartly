package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.network.socket.messages.UserToken;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;
import org.smartly.commons.network.socket.server.tools.MultipartMessageUtils;

/**
 *
 */
public class HandlerMultipartMessage extends AbstractSocketHandler {

    public static final String TYPE = MultipartMessagePart.class.getName();

    @Override
    public void handle(final SocketRequest request, final SocketResponse response) {
        // add part to server pool
        if (request.isTypeOf(MultipartMessagePart.class)) {
            final MultipartMessagePart part = (MultipartMessagePart) request.read();
            // multipart messages are used to upload or download
            if (part.getInfo().getType() == MultipartInfo.MultipartInfoType.File) {
                if (part.getInfo().getDirection() == MultipartInfo.MultipartInfoDirection.Upload) {
                    // UPLOAD
                    MultipartMessageUtils.saveOnDisk(part);
                    request.getServer().addMultipartMessagePart(part);
                } else {
                    // DOWNLOAD
                    MultipartMessageUtils.setPartBytes(part); // read chunk bytes
                    // send back data with bytes
                    response.write(part);
                }
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


}
