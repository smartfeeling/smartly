package org.smartly.commons.network.socket.client;

import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.io.filetokenizer.FileTokenizer;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.Server;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

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

    public void close() {
        try {
            if (null != _socket) {
                _socket.close();
            }
        } catch (Throwable ignored) {
        }
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

    public boolean sendFile(final String fileName,
                            final boolean useMultipleConnections) throws Exception {
        boolean result = false;
        if (FileUtils.exists(fileName)) {
            final String uid = GUID.create();
            final String[] chunks = FileTokenizer.splitFromChunkSize(fileName, uid, 1 * 1000 * 1024, null);
            try {
                this.sendFileChunks(PathUtils.getFilename(fileName, true), chunks, useMultipleConnections);
                result = true;
            } finally {
                this.clearFolder(chunks);
            }
        }
        return true;
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void clearFolder(final String[] chunks) throws IOException {
        // clean temp files
        final String file = CollectionUtils.get(chunks, 0);
        if (StringUtils.hasText(file)) {
            FileUtils.delete(PathUtils.getParent(file));
        }
    }

    private void sendFileChunks(final String fileName, final String[] chunks, final boolean useMultipleConnections) {
        final int len = chunks.length;
        for (int i = 0; i < len; i++) {
            final String chunk = chunks[i];
            final MultipartInfo info = new MultipartInfo(fileName,
                    MultipartInfo.MultipartInfoType.File, chunk, i, len);
            final MultipartMessagePart part = new MultipartMessagePart();

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
