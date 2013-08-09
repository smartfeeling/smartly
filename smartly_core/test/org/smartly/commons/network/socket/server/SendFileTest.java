package org.smartly.commons.network.socket.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.network.socket.client.UploadRunnable;
import org.smartly.commons.network.socket.messages.multipart.Multipart;
import org.smartly.commons.network.socket.server.tools.MultipartMessageUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.util.ResourceBundle;

/**
 *
 */
public class SendFileTest {

    static private int port;
    static private String host;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("org.smartly.commons.network.socket.server.TestServer");
        port = Integer.parseInt(resources.getString("server.port"));
        host = resources.getString("server.host");
    }

    private Server _simpleSocketServer;

    @Before
    public void setUp() throws Exception {
        _simpleSocketServer = new Server(port);
        _simpleSocketServer.onStart(new Server.OnStart() {
            @Override
            public void handle(Server sender) {
                System.out.println("STARTED!!!!!");
            }
        });

        _simpleSocketServer.onMultipartTimeOut(new Multipart.OnTimeOutListener() {
            @Override
            public void handle(Multipart sender) {
                System.out.println("TIME-OUT: " + sender.toString());
                try {
                    MultipartMessageUtils.remove(sender);
                } catch (Throwable ignored) {
                }
            }
        });
        _simpleSocketServer.onMultipartFull(new Multipart.OnFullListener() {
            @Override
            public void handle(Multipart sender) {
                System.out.println("FULL: " + sender.toString());
                parseMultipart(sender);
            }
        });

        _simpleSocketServer.start();
    }

    @After
    public void tearDown() throws Exception {
        _simpleSocketServer.stopServer();
    }

    @Test
    public void testSendFile() throws Exception {
        final String filename = PathUtils.concat(PathUtils.getTemporaryDirectory(), "ARCHIVIO.zip");

        final Client client = new Client();
        client.connect(host, port);

        final Thread[] tasks = client.sendFile(filename,
                "{\"_id\":\"test_01\"}",
                true,
                new Delegates.ProgressCallback() {
                    @Override
                    public void handle(int index, int length, double progress) {
                        System.out.println(FormatUtils.format("{0}/{1} {2}%", index + 1, length, (int) (progress * 100)));
                    }
                },
                new Delegates.ExceptionCallback() {
                    @Override
                    public void handle(String message, Throwable exception) {
                        System.out.println("Test Error: " + exception.toString());
                    }
                }
        );

        Async.joinAll(tasks);

        System.out.println("finishing....");

        for (final Thread thread : tasks) {
            final UploadRunnable task = (UploadRunnable) thread;
            System.out.println(task.toString());
        }

        Thread.sleep(5000);
    }

    private static void parseMultipart(final Multipart item) {
        try {
            final String out_root = PathUtils.concat(PathUtils.getTemporaryDirectory(), "out");
            MultipartMessageUtils.saveOnDisk(item, out_root);
        } catch (Throwable t) {
            System.out.println(t);
        }
    }
}
