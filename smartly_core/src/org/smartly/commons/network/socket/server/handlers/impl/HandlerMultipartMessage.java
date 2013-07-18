package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.IConstants;
import org.smartly.commons.io.filetokenizer.FileTokenizer;
import org.smartly.commons.network.socket.messages.multipart.Multipart;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;
import org.smartly.commons.network.socket.server.tools.MultipartMessageUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.File;
import java.io.IOException;

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
            MultipartMessageUtils.saveOnDisk(part);
            request.getServer().addMultipartMessagePart(part);
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------



}
