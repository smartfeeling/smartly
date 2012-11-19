package org.smartly.commons.network.socket.server.handler.impl;

import org.smartly.commons.network.socket.server.handler.ISocketHandler;

public class EchoNullHandler implements ISocketHandler {

	public Object handle(Object message) {
		return null;
	}
}
