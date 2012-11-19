package org.smartly.commons.network.socket.server;

import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.server.handler.ISocketHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerThread extends Thread {

    private final Socket _socket;
    private final ISocketHandler _handler;

    public ServerThread(final Socket socket, final ISocketHandler handler) {
        _socket = socket;
        _handler = handler;
    }

    public void run() {
        try {
            final ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
            final ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());
            // read
            final Object input = in.readObject();
            // handle
            final Object output = _handler.handle(input);
            // write
            out.writeObject(output);

            out.close();
            in.close();
            _socket.close();
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
}
