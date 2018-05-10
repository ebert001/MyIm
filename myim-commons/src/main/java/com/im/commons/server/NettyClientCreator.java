package com.im.commons.server;

import io.netty.channel.ChannelHandlerContext;

public class NettyClientCreator {
	private String host;
	private int port;
	/** connection count */
	private int connections = 1;

	private String name = "NettyClient";
	private NettyClient nettyClient;
	public NettyClientCreator(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public NettyClientCreator(String host, int port, int connections) {
		this.host = host;
		this.port = port;
		this.connections = connections;
	}

	/**
	 * Create connection that connect to netty server by connections parameter
	 */
	public void create() {
		for (int i = 0; i < connections; i++) {
			new NettyClient(this.name + "-" + i);
		}
	}

	/**
	 * Choose a netty client connection to server
	 * @return
	 */
	public ChannelHandlerContext chooseConnection() {
		return null;
	}

	/**
	 * Close a netty client connection. Maybe need other condition or random closing.
	 */
	public void closeConnection() {

	}

	public void closeAllConnection() {

	}

	public void readData() {

	}

	public void readLargeData() {

	}

	public void writeData() {

	}

	public void writeLargeData() {

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
