package com.aswishes.im.commons;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AppStarter {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) {
		
	}
}
