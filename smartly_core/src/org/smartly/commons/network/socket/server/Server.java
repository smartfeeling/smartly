package org.smartly.commons.network.socket.server;

import org.smartly.commons.Delegates;
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
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnStart {
        void handle(final Server sender);
    }

    private static final Class EVENT_ON_START = OnStart.class;
    private static final Class EVENT_ON_FULL = Multipart.OnFullListener.class;
    private static final Class EVENT_ON_TIME_OUT = Multipart.OnTimeOutListener.class;
    private static final Class EVENT_ON_ERROR = Delegates.ExceptionCallback.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final MultipartPool _multipartPool;
    private final Delegates.Handlers _eventHandlers;
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
        _eventHandlers = new Delegates.Handlers();

        this.init();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _multipartPool.clear();
            _handlers.clear();
            _eventHandlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }



    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onStart(final OnStart handler){
       _eventHandlers.add(handler);
    }

    public void onError(final Delegates.ExceptionCallback listener) {
         _eventHandlers.add(listener);
    }

    public void onMultipartFull(final Multipart.OnFullListener listener) {
        _eventHandlers.add(listener);
    }

    public void onMultipartTimeOut(final Multipart.OnTimeOutListener listener) {
        _eventHandlers.add(listener);
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
            this.onError(null, t);
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

    public void addMultipartMessagePart(final MultipartMessagePart part) {
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
        _multipartPool.onFull(new Multipart.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                onMultipartFull(sender);
            }
        });
        _multipartPool.onTimeOut(new Multipart.OnTimeOutListener() {
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
            this.onStart();
            while (true) {
                // accept client
                final Socket client = _socket.accept();
                // handle request in thread
                final ServerWorker st = new ServerWorker(this, client);
                st.start();
            }
        } catch (Throwable t) {
            if (_socket != null && _socket.isClosed()) {
                //Ignore if closed by stopServer() call
            } else {
                this.onError(null, t);
            }
        } finally {
            _socket = null;
            _running = false;
        }
        this.getLogger().info("Stopped");
    }

    void onStart() {
        _eventHandlers.trigger(EVENT_ON_START, this);
    }

    void onError(final String message, final Throwable error) {
        if (_eventHandlers.contains(EVENT_ON_ERROR)) {
            _eventHandlers.trigger(EVENT_ON_ERROR, message, error);
        } else {
            this.getLogger().log(Level.SEVERE, message, error);
        }
    }

    private void onMultipartFull(final Multipart multipart) {
        try {
            if (_eventHandlers.contains(EVENT_ON_FULL)) {
                _eventHandlers.triggerAsync(EVENT_ON_FULL, multipart);
            } else {
                // no external handlers.
                // handle internally

            }
        } catch (Throwable ignored) {

        }
    }

    private void onMultipartTimeout(final Multipart multipart) {
        // timeout
        try {
            if (_eventHandlers.contains(EVENT_ON_TIME_OUT)) {
                _eventHandlers.triggerAsync(EVENT_ON_TIME_OUT, multipart);
            } else {
                // no external handlers.
                // handle internally
                try {
                    MultipartMessageHandler.remove(multipart);
                } catch (Throwable t) {
                    this.onError(null, t);
                }
            }
        } catch (Throwable ignored) {
        }
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
