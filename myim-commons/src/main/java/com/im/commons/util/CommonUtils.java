package com.im.commons.util;

import java.util.Random;

public class CommonUtils {
	private static final char[] CS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123467890+-*/".toCharArray();
	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String randomString(int length) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(CS[random.nextInt(CS.length)]);
		}
		return sb.toString();
	}

	public static String toHex(byte[] src) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length; i++) {
			sb.append(hexDigits[src[i] >>> 4 & 0x0F]);
			sb.append(hexDigits[src[i] & 0x0F]);
		}
		return sb.toString();
	}

	public static int byte2ToInt(byte[] src, int offset) {
		if (null == src || offset < 0 || offset > src.length) {
			throw new NullPointerException("invalid byte array ");
		}
		if ((src.length - offset) < 2) {
			throw new IndexOutOfBoundsException("invalid len: " + src.length);
		}
		return ((src[offset + 0] & 0xff) << 8 | (src[offset + 1] & 0xff));
	}

	public static byte[] subBytes(byte[] src, int srcPos, int length) {
        byte[] bs = new byte[length];
        System.arraycopy(src, srcPos, bs, 0, length);
        return bs;
    }

    public static void append(byte[] src, byte[] dest, int offset) {
        System.arraycopy(src, 0, dest, offset, src.length);
    }

    public static void append(byte src, byte[] dest, int offset) {
    	dest[offset] = src;
    }
}
