package org.smartly.packages.http.impl.handlers;


/* ------------------------------------------------------------ */

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A handler that shuts the server down on a valid request. Used to do "soft" restarts from Java. If _exitJvm ist set to true a hard System.exit() call is being
 * made.
 * <p/>
 * This handler is a contribution from Johannes Brodwall: https://bugs.eclipse.org/bugs/show_bug.cgi?id=357687
 * <p/>
 * Usage:
 * <p/>
 * <pre>
 * Server server = new Server(8080);
 * HandlerList handlers = new HandlerList();
 * handlers.setHandlers(new Handler[]
 * { someOtherHandler, new ShutdownHandler(server,&quot;secret password&quot;) });
 * server.setHandler(handlers);
 * server.start();
 * </pre>
 * <p/>
 * <pre>
 * public static void attemptShutdown(int port, String shutdownCookie) {
 * try {
 * URL url = new URL("http://localhost:" + port + "/shutdown?cookie=" + shutdownCookie);
 * HttpURLConnection connection = (HttpURLConnection)url.openConnection();
 * connection.setRequestMethod("POST");
 * connection.getResponseCode();
 * logger.info("Shutting down " + url + ": " + connection.getResponseMessage());
 * } catch (SocketException e) {
 * logger.debug("Not running");
 * // Okay - the server is not running
 * } catch (IOException e) {
 * throw new RuntimeException(e);
 * }
 * }
 * </pre>
 */
public class SmartlyShutdownHandler extends ContextHandler {

    private static final Logger logger = LoggingUtils.getLogger(SmartlyShutdownHandler.class);

    private final String _shutdownToken;
    private final Server _server;
    private boolean _exitJvm = false;


    /**
     * Creates a listener that lets the server be shut down remotely (but only from localhost).
     *
     * @param server        the Jetty instance that should be shut down
     * @param shutdownToken a secret password to avoid unauthorized shutdown attempts
     */
    public SmartlyShutdownHandler(final Server server,
                                  final String shutdownToken) {
        _server = server;
        _shutdownToken = shutdownToken;
        super.setContextPath("/shutdown");
    }

    public void setExitJvm(boolean exitJvm) {
        this._exitJvm = exitJvm;
    }

    public void doHandle(final String target,
                         final Request baseRequest,
                         final HttpServletRequest request,
                         final HttpServletResponse response)
            throws IOException, ServletException{
        // accept only clean commands
        if (!target.equals("/")) {
            return;
        }

        // ok, command path is valid
        baseRequest.setHandled(true);

        if (!request.getMethod().equals("POST") && !request.getMethod().equals("GET")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!hasCorrectSecurityToken(request)) {
            logger.warning("Unauthorized shutdown attempt from " + getRemoteAddr(request));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!requestFromLocalhost(request)) {
            logger.warning("Unauthorized shutdown attempt from " + getRemoteAddr(request));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        logger.info("Shutting down by request from " + getRemoteAddr(request));

        new Thread() {
            public void run() {
                try {
                    shutdownServer();
                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    throw new RuntimeException("Shutting down server", e);
                }
            }
        }.start();
    }
    


    private boolean requestFromLocalhost(final HttpServletRequest request) {
        return "127.0.0.1".equals(getRemoteAddr(request));
    }

    protected String getRemoteAddr(final HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private boolean hasCorrectSecurityToken(final HttpServletRequest request) {
        return _shutdownToken.equals(request.getParameter("token"));
    }

    private void shutdownServer() throws Exception {
        _server.stop();

        if (_exitJvm) {
            System.exit(0);
        }
    }


}
