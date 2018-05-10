package com.im.commons.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.im.commons.util.ImConstants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;

public class NettyServer implements ImConstants {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private String name = "";
	private Class<? extends ServerChannel> channelType = NioServerSocketChannel.class;
	private Map<ChannelOption<?>, ?> channelOptions;
	private SocketAddress address;
	private Channel channel;
	private SSLConfig sslConfig;

	public NettyServer(int port) {
		this.channelOptions = new HashMap<>();
		this.address = new InetSocketAddress(port);
	}

	public void init() throws IOException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(channelType);
		if (NioServerSocketChannel.class.isAssignableFrom(channelType)) {
			bootstrap.option(ChannelOption.SO_BACKLOG, 128);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		}
		if (channelOptions != null) {
			for (Map.Entry<ChannelOption<?>, ?> entry : channelOptions.entrySet()) {
				@SuppressWarnings("unchecked")
				ChannelOption<Object> key = (ChannelOption<Object>) entry.getKey();
				bootstrap.childOption(key, entry.getValue());
			}
		}
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				ChannelPipeline pipeline = socketChannel.pipeline();

				addSslHandler(pipeline);

				pipeline.addLast(new CommandHandler());
			}
		});
		ChannelFuture future = bootstrap.bind(address);
		try {
			future.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted waiting for bind");
		}
		if (!future.isSuccess()) {
			throw new IOException("Failed to bind", future.cause());
		}
		channel = future.channel();
		logger.info("[{}]Start successfully.", this.name);
	}

	private void addSslHandler(ChannelPipeline pipeline) {
		if (sslConfig == null) {
			return;
		}
		pipeline.addLast("ssl", new SslHandler(sslConfig.getSSLEngine()));
	}

	public void shutdown() {
	    if (channel == null || !channel.isOpen()) {
	      // Already closed.
	      return;
	    }
	    channel.close().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
	    			logger.warn("Error shutting down server", future.cause());
	    		}
			}
		});
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
