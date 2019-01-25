package com.aswishes.im.commons.server;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;

public class NettyConnector {
	protected SSLConfig sslConfig;
	
	protected void addSslHandler(ChannelPipeline pipeline) {
		if (sslConfig == null) {
			return;
		}
		SslHandler handler = new SslHandler(sslConfig.getSSLEngine());
		if (sslConfig.getHandshakeTimeout() != -1) {
			handler.setHandshakeTimeout(sslConfig.getHandshakeTimeout(), sslConfig.getHandshakeTimeoutUnit());
		}
		pipeline.addLast("ssl", handler);
	}
	
	public void setSslConfig(SSLConfig sslConfig) {
		this.sslConfig = sslConfig;
	}
}
