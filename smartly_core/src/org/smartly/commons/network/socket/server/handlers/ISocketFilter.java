package org.smartly.commons.network.socket.server.handlers;

public interface ISocketFilter {

    /**
     * Handler event.
     * If false is returned, the handler chain will call next handler.
     *
     * @param request  The request
     * @param response The response
     * @return Return true to notify handled request
     */
    public boolean handle(final SocketRequest request, final SocketResponse response);

}
