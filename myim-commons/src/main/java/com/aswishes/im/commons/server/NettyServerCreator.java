package com.aswishes.im.commons.server;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;

public class NettyServerCreator {
	private int port;
	private NettyServer nettyServer;

	public NettyServerCreator(int port) {
		this.port = port;
	}

	/**
	 * Create a netty service.
	 * @throws IOException
	 */
	public void create() throws IOException {
		this.nettyServer = new NettyServer(port);
		this.nettyServer.init();

	}

	/**
	 * Choose a netty client connection
	 * @return
	 */
	public ChannelHandlerContext chooseConnection() {
		return null;
	}

	/**
	 * Close a netty client connection. Maybe need a client connection mark.
	 */
	public void closeConnection() {

	}

	/**
	 * Stop netty service and close all netty client connections.
	 */
	public void shutdown() {
		this.nettyServer.shutdown();
	}

	/**
	 * Restart netty service.
	 */
	public void restart() {

	}
}
