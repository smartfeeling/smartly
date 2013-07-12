package org.smartly.commons.network.socket.server;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.messages.multipart.Multipart;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.messages.multipart.MultipartPool;
import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.ISocketHandler;
import org.smartly.commons.network.socket.server.handlers.impl.MultipartMessageHandler;
import org.smartly.commons.network.socket.server.handlers.pool.SocketHandlerPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Very simple socket server example. That responds to a single object with
 * another object. Could be used as the basis for something more complex, but
 * this illustrates the basics of TCP/IP communication.
 * 
 * A Client will call a Server with a message. The Server will respond with a message.
 * In this simplest implementation the messages can be any serializable object.
 * 
 * To setup a server:
 *  1) Create your handler, a class that implements the simple SimpleSocketHandler 
 *     interface.
 *  2) Call the static Server.startServer() method with a port and an
 *     instance of your Handler defined above.
 *     
 * To call the server from a client:
 *  1) Call the static Client.send() method specifying the server host, port, and
 *     message. This returns your response.
 */
public class Server extends Thread {

    public static int DEFAULT_PORT = 14444;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final MultipartPool _multipartPool;
    private final int _port;
    private SocketHandlerPool _handlers;
    private ServerSocket _socket;
    private boolean _running;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Server(final int port) throws IOException {
        this(port, null);
    }

    public Server(final int port,
                  final Class<? extends ISocketFilter>[] handlers) throws IOException {
        super("Smartly-SocketServer");
        _running = false;
        _port = port;
        _handlers = new SocketHandlerPool(handlers);
        _socket = new ServerSocket(_port);
        _multipartPool = new MultipartPool();

        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _multipartPool.clear();
            _handlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void run() {
        this.startServer();
    }

    public void stopServer() {
        this.getLogger().info("Server: Stopping server...");
        try {
            if (_socket != null) {
                _socket.close();
            }
            if (null != _handlers) {
                _handlers.clear();
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    public boolean isServerRunning() {
        return _running;
    }

    public SocketHandlerPool getHandlers() {
        return _handlers;
    }

    public Server addFilter(final Class<? extends ISocketFilter> handler) {
        _handlers.addFilter(handler);
        return this;
    }

    public Server removeFilter(final Class<? extends ISocketFilter> handler) {
        _handlers.removeFilter(handler);
        return this;
    }

    public Server addHandler(final Class type, final Class<? extends ISocketHandler> handler) {
        this.addHandler(type.getName(), handler);
        return this;
    }

    public Server addHandler(final String type, final Class<? extends ISocketHandler> handler) {
        _handlers.addHandler(type, handler);
        return this;
    }

    public void addMultipartMessagePart(final MultipartMessagePart part){
        _multipartPool.add(part);
    }


    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void init() {

        //-- register default handlers --//
        this.addHandler(MultipartMessageHandler.TYPE,
                MultipartMessageHandler.class);

        //-- init multipart pool --//
        _multipartPool.onFull(new MultipartPool.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                onMultipartFull(sender);
            }
        });
        _multipartPool.onTimeOut(new MultipartPool.OnTimeOutListener() {
            @Override
            public void handle(Multipart sender) {
                onMultipartTimeout(sender);
            }
        });
    }

    private void startServer() {
        this.getLogger().info("Starting server on port [" + _port + "] with handlers [" + _handlers.toString() + "]");
        try {
            _running = true;
            while (true) {
                // accept client
                final Socket client = _socket.accept();
                // handle request in thread
                final ServerThread st = new ServerThread(this, client);
                st.start();
            }
        } catch (Throwable t) {
            if (_socket != null && _socket.isClosed()) {
                //Ignore if closed by stopServer() call
            } else {
                this.getLogger().log(Level.SEVERE, null, t);
            }
        } finally {
            _socket = null;
            _running = false;
        }
        this.getLogger().info("Stopped");
    }

    private void onMultipartFull(final Multipart multipart) {

    }

    private void onMultipartTimeout(final Multipart multipart) {

    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static Server startServer(final Class<ISocketFilter>[] handlers) throws Exception {
        return startServer(Server.DEFAULT_PORT, handlers);
    }

    public static synchronized Server startServer(final int port,
                                                  final Class<ISocketFilter>[] handlers) throws Exception {
        final Server server = new Server(port, handlers);
        server.start();
        while (!server.isServerRunning()) {
            Thread.sleep(100);
        }
        return server;
    }

}
