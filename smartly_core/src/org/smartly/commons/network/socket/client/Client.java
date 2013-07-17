package org.smartly.commons.network.socket.client;

import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.io.filetokenizer.FileTokenizer;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.messages.multipart.MultipartInfo;
import org.smartly.commons.network.socket.messages.multipart.MultipartMessagePart;
import org.smartly.commons.network.socket.server.Server;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Socket Client
 */
public class Client {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 10000;

    private Socket _socket;

    private Proxy _proxy;
    private SocketAddress _address;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public Client() {
        _proxy = Proxy.NO_PROXY; //NetworkUtils.getProxy();
        _address = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
    }

    public Client(final Proxy proxy) {
        _proxy = proxy;
        _address = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public SocketAddress getAddress() {
        return _address;
    }

    public void setAddress(final SocketAddress value) {
        _address = value;
    }

    public boolean isConnected() {
        return _socket.isConnected();
    }

    public void connect() throws IOException {
        this.connect(_address);
    }

    public void connect(final String host, final int port) throws IOException {
        final SocketAddress address = new InetSocketAddress(host, port);
        this.connect(address);
    }

    public void connect(final SocketAddress address) throws IOException {
        if (null != _socket) {
            try {
                _socket.close();
                _socket = null;
            } catch (Throwable ignored) {
            }
        }

        _address = address;
        _socket = new Socket(_proxy);
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
        Object response = null;
        final ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
        final ObjectInputStream in = new ObjectInputStream(_socket.getInputStream());
        try {
            out.writeObject(request);
            out.flush();

            try {
                response = in.readObject();
            } catch (EOFException ignored) {
                // no response
            }
        } finally {
            out.close();
            in.close();
        }
        return response;

    }

    public Thread[] sendFile(final String fileName,
                             final String userToken,
                             final boolean useMultipleConnections,
                             final Delegates.ProgressCallback progressCallback,
                             final Delegates.ExceptionCallback errorHandler) throws Exception {
        Thread[] result = new Thread[0];
        if (this.isConnected() && FileUtils.exists(fileName)) {
            final String uid = GUID.create();
            final String[] chunks = FileTokenizer.splitFromChunkSize(fileName, uid, 1 * 1000 * 1024, null);
            try {
                result = this.sendFileChunks(PathUtils.getFilename(fileName, true),
                        userToken,
                        chunks, useMultipleConnections,
                        progressCallback, errorHandler);
            } finally {
                this.clearFolder(chunks);
            }
        }
        return result;
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

    private Thread[] sendFileChunks(final String fileName,
                                    final String userToken,
                                    final String[] chunks,
                                    final boolean useMultipleConnections,
                                    final Delegates.ProgressCallback progressCallback,
                                    final Delegates.ExceptionCallback errorHandler) {
        final int len = chunks.length;
        final String transactionId = GUID.create();
        return Async.maxConcurrent(len, 3, new Delegates.CreateRunnableCallback() {
            @Override
            public Runnable handle(final int index, final int length) {
                return new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String chunk = chunks[index];
                            final MultipartInfo info = new MultipartInfo(fileName,
                                    MultipartInfo.MultipartInfoType.File, chunk, index, len);
                            info.setUserToken(userToken);
                            final MultipartMessagePart part = new MultipartMessagePart();
                            part.setInfo(info);
                            part.setUid(transactionId);
                            part.setData(FileUtils.copyToByteArray(new File(chunk)));
                            //-- send part --//
                            if (useMultipleConnections) {
                                send(getAddress(), part);
                            } else {
                                send(part);
                            }
                        } catch (Throwable t) {
                            if (null != errorHandler) {
                                errorHandler.handle(null, t);
                            } else {
                                LoggingUtils.getLogger(Client.class).log(Level.SEVERE, null, t);
                            }
                        }
                    }
                };
            }
        });
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

    public static Object send(final String host, final int port, final Object request) throws Exception {
        final SocketAddress address = new InetSocketAddress(host, port);
        return send(address, request);
    }

    public static Object send(final SocketAddress address, final Object request) throws Exception {
        Object response;

        final Client cli = new Client();
        cli.connect(address);
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
