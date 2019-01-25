package com.aswishes.im.commons.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient extends NettyConnector {
	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	private String name;
	private EventLoopGroup workerGroup;
	private Channel channel;

	public NettyClient(String name) {
		this.name = name;
	}

	public void connect(String host, int port, ChannelHandler...handlers) throws Exception {
        workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {
            	ChannelPipeline pipeline = socketChannel.pipeline();
				addSslHandler(pipeline);
            }
        });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        channel = future.channel();
        channel.pipeline().addLast(handlers);
    }

	public void shutdown() {
		// Already closed.
	    if (channel == null || !channel.isOpen()) {
	      return;
	    }
	    if (workerGroup == null || workerGroup.isTerminated()) {
	    	return;
	    }
	    channel.close().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
	    			logger.warn("[" + name + "]Error shutting down server. ", future.cause());
	    		}
			}
		});
	    workerGroup.shutdownGracefully();
	}

	public class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
	    // 接收server端的消息，并打印出来
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	        logger.info("HelloClientIntHandler.channelRead");
	        ByteBuf result = (ByteBuf) msg;
	        byte[] result1 = new byte[result.readableBytes()];
	        result.readBytes(result1);
	        System.out.println("Server said:" + new String(result1));
	        result.release();
	    }

	    // 连接成功后，向server发送消息
	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	        logger.info("HelloClientIntHandler.channelActive");
	        String msg = "Are you ok?";
	        write(msg);
	    }
	}

	public void write(byte[] data) {
		if (channel == null) {
			logger.warn("[{}] Netty client channel is null", this.name);
			return;
		}
		if (!channel.isWritable()) {
			logger.warn("[{}] Netty client channel is not writable.", this.name);
			return;
		}
		ByteBuf buffer = channel.alloc().buffer(data.length);
		buffer.writeBytes(data);
		channel.writeAndFlush(buffer);
	}

	public void write(String msg) {
		write(msg.getBytes(StandardCharsets.UTF_8));
	}

	public void write(InputStream input, int length) throws IOException {
		if (channel == null) {
			logger.warn("[{}] Netty client channel is null", this.name);
			return;
		}
		if (!channel.isWritable()) {
			logger.warn("[{}] Netty client channel is not writable.", this.name);
			return;
		}
		ByteBuf buffer = channel.alloc().ioBuffer();
		buffer.writeBytes(input, length);
		channel.writeAndFlush(buffer);
	}
}
