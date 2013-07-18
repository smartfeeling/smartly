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

import java.io.*;
import java.net.Socket;


public class ServerWorker extends Thread {

    private final Server _server;
    private final Socket _client;
    private final SocketHandlerPool _pool;

    public ServerWorker(final Server server, final Socket client) {
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
            if (null != input) {
                final SocketRequest request = new SocketRequest(new SocketRequestServer(_server), input);
                final SocketResponse response = new SocketResponse();

                //-- handle request and write response --//
                this.handle(request, response);

                if (null != response.read()) {

                    final Object output = response.read();
                    // write
                    out.writeObject(output);
                    out.flush();
                }
            }
            out.close();
            in.close();
        } catch (EOFException ignored) {
        } catch (StreamCorruptedException ignored) {
        } catch (Throwable t) {
            this.onError(null, t);
        } finally {
            try {
                _client.close();
            } catch (Throwable ignored) {
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void onError(final String message, final Throwable t) {
        try {
            _server.onError(message, t);
        } catch (Throwable ignored) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
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

    private Object read(final InputStream is) {
        Object result = null;
        try {
            final ObjectInputStream in = new ObjectInputStream(is);
            result = in.readObject();
        } catch (Throwable ignored) {
            // unable to read stream
        }
        return result;
    }
}
