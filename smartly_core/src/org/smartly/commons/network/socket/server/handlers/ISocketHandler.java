package org.smartly.commons.network.socket.server.handlers;

public interface ISocketHandler {

    public String getType();

    public void setType(String type);

    /**
     * Handler event.
     *
     * @param request  The request
     * @param response The response
     */
    public void handle(final SocketRequest request, final SocketResponse response);

}
