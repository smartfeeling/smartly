package org.smartly.packages.http.impl.handlers.websocket;

import org.eclipse.jetty.websocket.common.extensions.compress.FrameCompressionExtension;
import org.eclipse.jetty.websocket.common.extensions.compress.MessageCompressionExtension;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 */
public class WSocketServlet extends WebSocketHandler{

    private final Set users = new CopyOnWriteArraySet();

    @Override
    public void configure(final WebSocketServletFactory factory) {
        // extensions
        factory.getExtensionFactory().register("x-webkit-deflate-frame",FrameCompressionExtension.class);
        factory.getExtensionFactory().register("permessage-compress",MessageCompressionExtension.class);

        factory.setCreator(new WSocketCreator());
    }

}
