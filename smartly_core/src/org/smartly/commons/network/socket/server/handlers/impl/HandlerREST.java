package org.smartly.commons.network.socket.server.handlers.impl;

import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.messages.rest.RESTMessage;
import org.smartly.commons.network.socket.server.handlers.AbstractSocketHandler;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;
import org.smartly.commons.remoting.rest.RESTRegistry;
import org.smartly.commons.remoting.rest.wrapper.MethodWrapper;

/**
 * REST Command Handler
 */
public class HandlerREST extends AbstractSocketHandler {

    public static final String TYPE = RESTMessage.class.getName();

    // --------------------------------------------------------------------
    //               o v e r r i d e
    // --------------------------------------------------------------------

    @Override
    public void handle(final SocketRequest request, final SocketResponse response) {
        // manage request
        if (request.isTypeOf(RESTMessage.class)) {
            final RESTMessage message = (RESTMessage) request.read();
            final Object invoke_response = invoke(message);
            if (null != invoke_response) {
                response.write(invoke_response);
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static Logger getLogger() {
        return LoggingUtils.getLogger(HandlerREST.class);
    }

    private static Object invoke(final RESTMessage message) {
        if (null != message) {
            try {
                final MethodWrapper mw = RESTRegistry.getMethod(message.getMethod(), message.getPath());
                if (null != mw) {
                    final byte[] bytes = mw.execute(message.getPath(), message.getDataAsJSON());
                    return null != bytes ? new String(bytes, CharEncoding.UTF_8) : "";
                } else {
                    // method not found
                    getLogger().error("Method not found: " + message.toString());
                }
            } catch (Throwable t) {
                return t;
            }
        }
        return null;
    }

}
