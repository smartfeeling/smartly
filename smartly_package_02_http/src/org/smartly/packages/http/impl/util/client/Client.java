package org.smartly.packages.http.impl.util.client;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 *  Simple Jetty client
 *
 *  http://wiki.eclipse.org/Jetty/Tutorial/HttpClient
 *
 */
public class Client {

    public Client() {
        HttpClient client = new HttpClient();
    }

    public String doGET(final String url) throws Exception {
        final HttpClient client = this.createClient();
        client.start();

        ContentExchange exchange = new ContentExchange(true);
        exchange.setURL(url);

        client.send(exchange);

        // Waits until the exchange is terminated
        int exchangeState = exchange.waitForDone();

        if (exchangeState == HttpExchange.STATUS_COMPLETED) {
            return exchange.getResponseContent();
        } else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
            // error
        } else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
            // timeout - slow server
        }

        return "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClient createClient() {
        HttpClient client = new HttpClient();
        client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerAddress(200); // max 200 concurrent connections to every address
        client.setThreadPool(new QueuedThreadPool(250)); // max 250 threads
        client.setTimeout(30000); // 30 seconds timeout; if no server reply, the request expires
        return client;
    }

}
