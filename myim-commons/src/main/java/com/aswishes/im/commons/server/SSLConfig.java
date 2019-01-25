package com.aswishes.im.commons.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.aswishes.im.commons.util.ImConstants;

public class SSLConfig implements ImConstants {

	private SSLContext sslContext;
	private SSLEngine sslEngine;

	private KeyManager[] keyManagers;
	private TrustManager[] trustManagers;
	
	private String sslProtocol = SSL_PRTOCOL_TLSV12;
	private String provider = SSL_PROVIDER_BC;
	
	private long handshakeTimeout = -1;
	private TimeUnit handshakeTimeoutUnit = null;
	
	public SSLConfig setJKSKeyStoreParam(File ksFile, String ksPwd, String keyPwd) throws Exception {
		return setKeyStoreParam(ksFile, KS_TYPE_JKS, ksPwd, keyPwd, KS_ALG_JKS);
	}
	
	public SSLConfig setBKSKeyStoreParam(File ksFile, String ksPwd, String keyPwd) throws Exception {
		return setKeyStoreParam(ksFile, KS_TYPE_BKS, ksPwd, keyPwd, KS_ALG_BKS);
	}
	
	public SSLConfig setPKCS12KeyStoreParam(File ksFile, String ksPwd, String keyPwd) throws Exception {
		return setKeyStoreParam(ksFile, KS_TYPE_PKCS12, ksPwd, keyPwd, KS_ALG_PKCS12);
	}

	public SSLConfig setKeyStoreParam(File ksFile, String ksType, String ksPwd, String keyPwd, String ksAlg) throws Exception {
		KeyStore ks = KeyStore.getInstance(ksType, provider);
		ks.load(new FileInputStream(ksFile), ksPwd.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(ksAlg, provider);
		kmf.init(ks, keyPwd.toCharArray());
		keyManagers = kmf.getKeyManagers();
		return this;
	}
	
	public SSLConfig setJKSTrustStoreParam(File ksFile, String ksPwd) throws Exception {
		return setTrustStoreParam(ksFile, KS_TYPE_JKS, ksPwd, KS_ALG_JKS);
	}
	
	public SSLConfig setBKSTrustStoreParam(File ksFile, String ksPwd) throws Exception {
		return setTrustStoreParam(ksFile, KS_TYPE_BKS, ksPwd, KS_ALG_BKS);
	}
	
	public SSLConfig setPKCS12TrustStoreParam(File ksFile, String ksPwd) throws Exception {
		return setTrustStoreParam(ksFile, KS_TYPE_PKCS12, ksPwd, KS_ALG_PKCS12);
	}

	public SSLConfig setTrustStoreParam(File ksFile, String ksType, String ksPwd, String ksAlg) throws Exception {
		KeyStore ks = KeyStore.getInstance(ksType);
		ks.load(new FileInputStream(ksFile), ksPwd.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(ksAlg, provider);
		tmf.init(ks);
		trustManagers = tmf.getTrustManagers();
		return this;
	}

	public SSLConfig init() throws Exception {
		sslContext = SSLContext.getInstance(sslProtocol, provider);
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
	
	public SSLConfig loadBouncyCastle() {
		Security.addProvider(new BouncyCastleProvider());
		return this;
	}
	
	public SSLContext getSSLContext() {
		return this.sslContext;
	}

	public SSLEngine getSSLEngine() {
		return this.sslEngine;
	}
	
	public SSLConfig setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
		return this;
	}
	
	public SSLConfig setProvider(String provider) {
		this.provider = provider;
		return this;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public SSLConfig setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
		this.handshakeTimeout = handshakeTimeout;
		this.handshakeTimeoutUnit = unit;
		return this;
	}
	
	public long getHandshakeTimeout() {
		return handshakeTimeout;
	}
	
	public TimeUnit getHandshakeTimeoutUnit() {
		return handshakeTimeoutUnit;
	}
}
