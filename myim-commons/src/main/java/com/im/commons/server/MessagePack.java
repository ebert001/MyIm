package com.im.commons.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.im.commons.util.ByteConvert;
import com.im.commons.util.CommonUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class MessagePack implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MessagePack.class);
	
	private ChannelHandlerContext ctx;
	private byte[] inData;

	public MessagePack(byte[] inData, ChannelHandlerContext ctx) {
		this.inData = inData;
		this.ctx = ctx;
	}

	public void run() {
		try {
			logger.debug("send remote server buffer:\n" + CommonUtils.toHex(inData));
			logger.debug("Read data: " + new String(inData));
			
			byte[] data = new byte[2];
			write(data);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	private void write(byte[] data) throws InterruptedException {
		logger.debug("Return data length: " + data.length);
		logger.debug("Return data: " + CommonUtils.toHex(data));

		ByteBuf byteBuf = ctx.alloc().buffer();
		byteBuf.writeBytes(ByteConvert.int2byte2(data.length));
		byteBuf.writeBytes(data);
		ctx.writeAndFlush(byteBuf).sync();
	}
}
