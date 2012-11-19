package org.smartly.commons.network.socket.server.handler.impl;

import org.smartly.commons.network.socket.server.handler.ISocketHandler;

/**
 * Just echo back the message that you receive. This Handler could be used
 * for testing that a communication channel is up and running.
 *
 */
public class EchoHandler implements ISocketHandler {

	public Object handle(Object message) {
		return message;
	}
}
