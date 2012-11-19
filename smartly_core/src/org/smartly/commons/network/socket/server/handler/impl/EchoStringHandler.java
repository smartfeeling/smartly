package org.smartly.commons.network.socket.server.handler.impl;

import org.smartly.commons.network.socket.server.handler.ISocketHandler;

import java.util.Date;

public class EchoStringHandler implements ISocketHandler {

	public Object handle(final Object message) {
		return this.getClass().getSimpleName() + " (" + (new Date()).toString() + "): " + message.toString();
	}
}
