package com.wizarpos.im.core.util;

import java.util.Random;

public class CommonUtils {

	public static String randomString(int length) {
		char[] cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123467890+-*/".toCharArray();
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(cs[random.nextInt(cs.length)]);
		}
		return sb.toString();
	}
	
	public static String toHex(byte[] src) {
		StringBuilder sb = new StringBuilder();
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
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
