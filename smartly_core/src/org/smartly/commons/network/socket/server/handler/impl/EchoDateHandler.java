package org.smartly.commons.network.socket.server.handler.impl;

import org.smartly.commons.network.socket.server.handler.ISocketHandler;

import java.util.Date;

public class EchoDateHandler implements ISocketHandler {

	public Object handle(Object message) {
		return new Date();
	}
}
