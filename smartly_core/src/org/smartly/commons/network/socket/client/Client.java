package org.smartly.commons.network.socket.client;

import org.smartly.commons.network.NetworkUtils;
import org.smartly.commons.network.socket.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Socket Client
 */
public class Client {

    private Socket _socket;

    public Client() {

    }

    public void connect(final String host, final int port) throws IOException {
        if (null != _socket) {
            try {
                _socket.close();
            } catch (Throwable ignored) {
            }
        }
        final SocketAddress address = new InetSocketAddress(host, port);
        final Proxy proxy = Proxy.NO_PROXY; //NetworkUtils.getProxy();
        _socket = new Socket(proxy);
        _socket.connect(address, 3000);
    }

    public Object send(final Object request) throws Exception {
        final ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
        final ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());

        out.writeObject(request);
        out.flush();

        final Object response = in.readObject();

        out.close();
        in.close();

        return response;
    }

    public void close() {
        try {
            if (null != _socket) {
                _socket.close();
            }
        } catch (Throwable ignored) {
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


    public static String sendString(final String request) throws Exception {
        return sendString("localhost", Server.DEFAULT_PORT, request);
    }

    public static String sendString(final String server, final int port, final String request) throws Exception {
        return (String) send(server, port, (Object) request);
    }

    public static Object send(final String server, final int port, final Object request) throws Exception {
        Object response;

        final Client cli = new Client();
        cli.connect(server, port);
        response = cli.send(request);
        cli.close();

        return response;
    }

    public static boolean ping(final String server, final int port) {
        try {
            final Client cli = new Client();
            cli.connect(server, port);
            cli.close();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
