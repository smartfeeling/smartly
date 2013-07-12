package org.smartly.commons.network.socket.server;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handlers.ISocketFilter;
import org.smartly.commons.network.socket.server.handlers.ISocketHandler;
import org.smartly.commons.network.socket.server.handlers.SocketRequest;
import org.smartly.commons.network.socket.server.handlers.SocketResponse;
import org.smartly.commons.network.socket.server.handlers.pool.SocketFilterPoolIterator;
import org.smartly.commons.network.socket.server.handlers.pool.SocketHandlerPool;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerThread extends Thread {

    private final Server _server;
    private final Socket _client;
    private final SocketHandlerPool _pool;

    public ServerThread(final Server server, final Socket client) {
        _server = server;
        _client = client;
        _pool = _server.getHandlers();
    }

    @Override
    public void run() {
        try {
            final ObjectOutputStream out = new ObjectOutputStream(_client.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(_client.getInputStream());
            // read
            final Object input = in.readObject();
            final SocketRequest request = new SocketRequest(input);
            final SocketResponse response = new SocketResponse();

            //-- handle request and write response --//
            this.handle(request, response);

            if (null != response.read()) {
                final Object output = response.read();
                // write
                out.writeObject(output);
            }

            out.close();
            in.close();
            _client.close();
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void handle(final SocketRequest request, final SocketResponse response) {
        // filters
        final SocketFilterPoolIterator iterator = _pool.getFiltersIterator();
        while (iterator.hasNext()) {
            final ISocketFilter handler = iterator.next();
            if (handler.handle(request, response)) {
                break;
            }
        }

        if (response.canHandle() && _pool.hasHandler(request.getType())) {
            final ISocketHandler handler = _pool.getHandler(request.getType());
            if (null != handler) {
                handler.handle(request, response);
            }
        }
    }
}
