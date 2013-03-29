package org.smartly.packages.http.impl.util.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;

/**
 * Simple Jetty client
 * <p/>
 * http://wiki.eclipse.org/Jetty/Tutorial/HttpClient
 */
public class Client {

    public Client() {
        HttpClient client = new HttpClient();
    }

    public String doGET(final String url) throws Exception {
        final HttpClient client = this.createClient();
        client.start();

        final ContentResponse response = client.GET(url);
        return response.getContentAsString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private HttpClient createClient() {
        HttpClient client = new HttpClient();
        //client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        client.setMaxConnectionsPerDestination(200); // max 200 concurrent connections to every address
        //client.setThreadPool(new QueuedThreadPool(250)); // max 250 threads
        client.setConnectTimeout(30000); // 30 seconds timeout; if no server reply, the request expires
        return client;
    }

}
