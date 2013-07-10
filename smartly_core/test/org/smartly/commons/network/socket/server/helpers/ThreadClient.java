package org.smartly.commons.network.socket.server.helpers;


import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.network.socket.client.Client;
import org.smartly.commons.util.RandomUtils;

public class ThreadClient implements Runnable {

    private final int _id;
    private final String _host;
    private final int _port;
    private final Logger _logger;

    public ThreadClient(final String host, final int port, final int id) {
        _id = id;
        _host = host;
        _port = port;
        _logger = LoggingUtils.getLogger(this);
    }

    @Override
    public void run() {
        _logger.info("Started: " + _id);
        try {

            Thread.sleep(RandomUtils.getTimeBasedRandomInteger(3));

            final String response = Client.sendString(_host, _port, "Message from thread: " + _id);
            _logger.info("CLIENT: " + response);
        } catch (Throwable t) {
            _logger.error(t.toString());
        }
        _logger.info("Finished: " + _id);
    }


}
