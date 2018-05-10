package com.im.commons.server;

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
import io.netty.handler.ssl.SslHandler;

public class NettyClient {
	private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	private String name;
	private EventLoopGroup workerGroup;
	private Channel channel;
	private SSLConfig sslConfig;

	public NettyClient(String name) {
		this.name = name;
	}

	public void setSSL() {
		this.sslConfig = new SSLConfig();
//		this.sslConfig.setKeyStoreParam(ksFile, pwd);
//		this.sslConfig.setTrustStoreParam(ksFile, pwd)
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

	private void addSslHandler(ChannelPipeline pipeline) {
		if (sslConfig == null) {
			return;
		}
		pipeline.addLast("ssl", new SslHandler(sslConfig.getSSLEngine()));
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

	public static class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
	    private static Logger logger = LoggerFactory.getLogger(HelloClientIntHandler.class);

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
	        ByteBuf encoded = ctx.alloc().buffer(4 * msg.length());
	        encoded.writeBytes(msg.getBytes());
	        ctx.write(encoded);
	        ctx.flush();
	    }
	}
}
