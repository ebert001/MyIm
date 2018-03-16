package com.wizarpos.im.core.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wizarpos.im.core.util.ImConstants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;

public class NettyServer implements ImConstants {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private boolean useSSL = false;
	private SSLContext sslContext = null;
	
	private int port = 16060;
	
	public NettyServer(boolean useSSL) {
		this.useSSL = useSSL;
		
		
	}

	public void init() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 5000);
			serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					ChannelPipeline pipeline = socketChannel.pipeline();
					
					SSLEngine sslEngine = sslContext.createSSLEngine();
					sslEngine.setUseClientMode(false);
					sslEngine.setNeedClientAuth(true);
					
					SslHandler sslHandler = new SslHandler(sslEngine);
					sslHandler.setHandshakeTimeoutMillis(0);
					pipeline.addLast("ssl", sslHandler);
					
					pipeline.addLast(new CommandHandler());
				}
			});
			logger.info("begin bind port:" + port);
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			logger.info("begin close Future syc");
			channelFuture.channel().closeFuture().sync();
			logger.info("end");
		} catch (InterruptedException e) {
			logger.error("", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	private void initSSLContext() throws Exception {
		sslContext = SSLContext.getInstance(SSL_PRTOCOL, SSL_PROVIDER_NAME);
		sslContext.init(null, geTrustManagers(), null);
	}
	
	private TrustManager[] geTrustManagers() throws Exception {
		char[] password = "".toCharArray();

		KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);
		File keystore = new File("");
		if (!keystore.exists()) {
			throw new NullPointerException("" + " do not exists");
		}
		trustStore.load(new FileInputStream(keystore), password);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(KEYSTORE_ALG);
		tmf.init(trustStore);
		return tmf.getTrustManagers();
	}
}
