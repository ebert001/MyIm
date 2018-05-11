package com.im.commons.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;

public class NettyClientCreator {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientCreator.class);
	private String host;
	private int port;
	/** connection count */
	private int connections = 1;

	private String name = "NettyClient";
	private Set<NettyClient> clients = new HashSet<NettyClient>();
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
	public void create(ChannelHandler...handlers) {
		for (int i = 0; i < connections; i++) {
			NettyClient client = new NettyClient(this.name + "-" + i);
			for (int retryNum = 1; i < 4; i++) {
				try {
					client.connect(host, port, handlers);
				} catch (Exception e) {
					logger.error("Connect to netty server failed. Domain: " + host+ ":" + port + ". Retry num: " + retryNum, e);
					continue;
				}
				clients.add(client);
			}
		}
	}

	/**
	 * Choose a netty client connection to server
	 * @return
	 */
	public NettyClient chooseConnection() {
		return clients.iterator().next();
	}

	/**
	 * Close a netty client connection. Maybe need other condition or random closing.
	 */
	public void closeConnection() {

	}

	public void closeAllConnection() {
		clients.forEach(new Consumer<NettyClient>() {
			@Override
			public void accept(NettyClient t) {
				t.shutdown();
			}
		});
		clients.clear();
	}

	public void readData() {

	}

	public void readLargeData() {

	}

	public void writeData(byte[] data) {
		chooseConnection().write(data);
	}

	public void writeData(InputStream input, int length) throws IOException {
		chooseConnection().write(input, length);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
