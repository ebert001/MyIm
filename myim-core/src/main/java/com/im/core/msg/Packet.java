package com.im.core.msg;

/**
 *
 * @author lizhou
 */
public abstract class Packet {

	public static final byte CMD_PING = 0x01;

	/** one or two bytes length. if first bit is 1, it's two bytes; or it's one bytes */
	protected byte[] len;
	/** 1 byte length */
	protected byte cmd;

	protected byte[] packet;

	public Packet() {}

	public Packet(byte[] packet) {
		this.packet = packet;
	}

	public Packet parse(byte[] packet) {

		return this;
	}

	public byte[] build() {
		return null;
	}
}
