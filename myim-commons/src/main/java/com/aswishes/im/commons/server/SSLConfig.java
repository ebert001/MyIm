package com.aswishes.im.commons.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.aswishes.im.commons.util.ImConstants;

public class SSLConfig implements ImConstants {

	private SSLContext sslContext;
	private SSLEngine sslEngine;

	private KeyManager[] keyManagers;
	private TrustManager[] trustManagers;

	public SSLConfig setKeyStoreParam(File ksFile, char[] pwd) throws Exception {
		KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
		ks.load(new FileInputStream(ksFile), pwd);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYSTORE_ALG);
		kmf.init(ks, pwd);
		keyManagers = kmf.getKeyManagers();
		return this;
	}

	public SSLConfig setTrustStoreParam(File ksFile, char[] pwd) throws Exception {
		KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
		ks.load(new FileInputStream(ksFile), pwd);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(KEYSTORE_ALG);
		tmf.init(ks);
		trustManagers = tmf.getTrustManagers();
		return this;
	}

	public SSLConfig init() throws Exception {
		sslContext = SSLContext.getInstance(SSL_PRTOCOL, SSL_PROVIDER_NAME);
		sslContext.init(keyManagers, trustManagers, new SecureRandom());

		sslEngine = sslContext.createSSLEngine();
		return this;
	}

	public SSLConfig setUseClientMode(boolean mode) {
		this.sslEngine.setUseClientMode(mode);
		return this;
	}

	public SSLConfig setNeedClientAuth(boolean need) {
		this.sslEngine.setNeedClientAuth(need);
		return this;
	}

	public SSLContext getSSLContext() {
		return this.sslContext;
	}

	public SSLEngine getSSLEngine() {
		return this.sslEngine;
	}
}
