package org.smartly.commons.network.socket.server;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;

import java.io.IOException;
import java.net.BindException;
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

    private final int _port;
    private final ISocketHandler _handler;
    private ServerSocket _socket;
    private boolean _running;

    public Server(final int port,
                  final ISocketHandler handler) throws IOException {
        super("Smartly-SocketServer");
        _running = false;
        _port = port;
        _handler = handler;
        _socket = new ServerSocket(_port);
        if (handler == null) {
            throw new RuntimeException("Null handler detected.");
        }
    }

    public void run(){
        this.startServer();
    }

    public void stopServer() {
        this.getLogger().info("Server: Stopping server...");
        try {
            if (_socket != null) {
                _socket.close();
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    public boolean isServerRunning() {
        return _running;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void startServer() {
        this.getLogger().info("Starting server on port [" + _port + "] with handler [" + _handler.toString() + "]");
        try {
            _running = true;
            while (true) {
                // accept client
                final Socket s = _socket.accept();
                // handle request in thread
                final ServerThread st = new ServerThread(s, _handler);
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

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static Server startServer(final ISocketHandler handler) throws Exception {
        return startServer(Server.DEFAULT_PORT, handler);
    }

    public static synchronized Server startServer(final int port,
                                                  final ISocketHandler handler) throws Exception {
        final Server server = new Server(port, handler);
        server.start();
        while (!server.isServerRunning()) {
            Thread.sleep(100);
        }
        return server;
    }

}
