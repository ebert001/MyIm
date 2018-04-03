package com.im.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertUtils implements ImConstants {
	private static final Logger logger = LoggerFactory.getLogger(CertUtils.class);

	public static KeyPair genRSAKeyPair(int keySize) throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(keySize, new SecureRandom());
		return keyGen.generateKeyPair();
	}

	// We could request BC here in order to gain support for certs
	// with > 2048 bit RSA keys also on Java 1.4. But unless there's
	// a way to eg. read JKS keystores containing such certificates
	// on Java 1.4 (think eg. importing such CA certs), that would
	// just help the user shoot herself in the foot...
	public static X509Certificate convert(Certificate certIn) {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bais = new ByteArrayInputStream(certIn.getEncoded());

			return (X509Certificate) cf.generateCertificate(bais);
		} catch (CertificateException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public static X500Name convert(Principal principal) {
		return new X500Name(principal.getName());
	}

	/**
	 * 基础方法：生成签名证书
	 *
	 * @param privateKey
	 *            签发者私钥
	 * @param publicKey
	 *            被签发者公钥
	 * @param issuerDn
	 *            签发者dn
	 * @param subjectDn
	 *            被签发者dn
	 * @param fromDate
	 *            有效期: 起始日期
	 * @param endDate
	 *            有效期: 结束日期
	 * @param signatureType
	 *            签名类型
	 * @return
	 */
	public static X509Certificate genX509Certificate(PrivateKey privateKey, PublicKey publicKey, X500Name issuerDn,
			X500Name subjectDn, Date fromDate, Date endDate, String signatureType) {
		Date date = new Date();
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuerDn, BigInteger.valueOf(date.getTime()),
				fromDate, endDate, subjectDn, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
		try {
			ContentSigner signer = new JcaContentSignerBuilder(signatureType)
					.setProvider(SSL_PROVIDER_NAME).build(privateKey);
			X509CertificateHolder holder = builder.build(signer);
			X509Certificate cert = new JcaX509CertificateConverter().setProvider(SSL_PROVIDER_NAME)
					.getCertificate(holder);
			cert.checkValidity(date);
			// 子证书验证父证书的key，因为没有传入签发者公钥，所以不验证了．
			// cert.verify(publicKey);
			return cert;
		} catch (Exception e) {
			logger.error("Generate certificate error", e);
		}
		return null;
	}

	public static X509Certificate genX509Certificate(PrivateKey privateKey, PublicKey publicKey, String issuerDn,
			String subjectDn, Date fromDate, Date endDate, String signatureType) {
		return genX509Certificate(privateKey, publicKey, new X500Name(issuerDn), new X500Name(subjectDn), fromDate,
				endDate, signatureType);
	}

	public static X509Certificate genX509Certificate(KeyPair kp, String dn, int years) {
		Calendar cal = Calendar.getInstance();
		Date fromDate = cal.getTime();
		Date endDate = DateUtils.addYears(fromDate, years);
		return genX509Certificate(kp.getPrivate(), kp.getPublic(), dn, dn, fromDate, endDate,
				SHA256WITHRSA);
	}

	/**
	 * 生成自签名证书
	 *
	 * @param dn
	 * @return
	 */
	public static X509Certificate genX509Certificate(String dn, int years) {
		KeyPair kp = null;
		try {
			kp = genRSAKeyPair(KEY_SIZE_2048);
		} catch (Exception e) {
			logger.error("Generate RSA key pair error", e);
			return null;
		}
		return genX509Certificate(kp, dn, years);
	}

	public static PublicKey getPublicKey(SubjectPublicKeyInfo pkInfo) throws Exception {
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pkInfo.getEncoded());
		ASN1ObjectIdentifier aid = pkInfo.getAlgorithm().getAlgorithm();

		KeyFactory kf = null;
		if (PKCSObjectIdentifiers.rsaEncryption.equals(aid)) {
			kf = KeyFactory.getInstance("RSA");
		} else if (X9ObjectIdentifiers.id_dsa.equals(aid)) {
			kf = KeyFactory.getInstance("DSA");
		} else if (X9ObjectIdentifiers.id_ecPublicKey.equals(aid)) {
			kf = KeyFactory.getInstance("ECDSA");
		} else {
			throw new InvalidKeySpecException("unsupported key algorithm: " + aid);
		}
		return kf.generatePublic(pubSpec);
	}

	public static PKCS10CertificationRequest genCsr(PrivateKey privateKey, PublicKey publicKey, String subjectDn,
			String signatureType) {
		try {
			PKCS10CertificationRequestBuilder builder = new PKCS10CertificationRequestBuilder(new X500Name(subjectDn),
					SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
			ContentSigner signer = new JcaContentSignerBuilder(signatureType)
					.setProvider(SSL_PROVIDER_NAME).build(privateKey);
			return builder.build(signer);
		} catch (Exception e) {
			logger.error("Generate CSR error", e);
			return null;
		}
	}

	public static PKCS10CertificationRequest genCsr(PrivateKey privateKey, PublicKey publicKey, String subjectDn) {
		return genCsr(privateKey, publicKey, subjectDn, SHA256WITHRSA);
	}

	public static X509Certificate issueCsr(PKCS10CertificationRequest csr, PrivateKey privateKey, X500Name issuer,
			Date fromDate, Date endDate) {
		try {
			PublicKey publicKey = getPublicKey(csr.getSubjectPublicKeyInfo());
			return genX509Certificate(privateKey, publicKey, issuer, csr.getSubject(), fromDate, endDate,
					SHA256WITHRSA);
		} catch (Exception e) {
			logger.error("Issue CSR error", e);
			return null;
		}
	}

	public static X509Certificate issueCsr(PKCS10CertificationRequest csr, PrivateKey privateKey, X500Name issuer,
			int years) {
		Calendar cal = Calendar.getInstance();
		Date fromDate = cal.getTime();
		Date endDate = DateUtils.addYears(fromDate, years);
		return issueCsr(csr, privateKey, issuer, fromDate, endDate);
	}

	/**
	 * 是否是有效的证书链
	 *
	 * @param parent
	 *            父证书
	 * @param child
	 *            子证书
	 */
	public static boolean isValidChain(X509Certificate parent, X509Certificate child) {
		if (!child.getIssuerX500Principal().equals(parent.getSubjectX500Principal())) {
			return false;
		}
		try {
			child.verify(parent.getPublicKey());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static byte[] encrypt(byte[] data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("encryptByKey failed", e);
			return null;
		}
	}

	public static byte[] decrypt(byte[] data, Key key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			logger.error("decryptByKey failed", e);
			return null;
		}
	}

	public static byte[] sig(PrivateKey privateKey, byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initSign(privateKey, new SecureRandom());
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			logger.error("Sign data error", e);
		}
		return null;
	}

	public static boolean verifySig(PublicKey publicKey, byte[] sig, byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initVerify(publicKey);
			signature.update(data);
			return signature.verify(sig);
		} catch (Exception e) {
			logger.error("Verify Signature error", e);
		}
		return false;
	}

	/**
	 * 判断证书所用密钥是否有变更
	 *
	 * @param csr
	 *            签名请求证书
	 * @param certificate
	 *            证书
	 * @return true变更 false 未变更
	 */
	public static boolean isSamePublicKey(PKCS10CertificationRequest csr, Certificate certificate) {
		try {
			RSAPublicKey key1 = (RSAPublicKey) getPublicKey(csr.getSubjectPublicKeyInfo());
			RSAPublicKey key2 = (RSAPublicKey) certificate.getPublicKey();
			return isSamePublicKey(key1, key2);
		} catch (Exception e) {
			logger.error("compare public key error", e);
		}
		return false;
	}

	public static boolean isSamePublicKey(PKCS10CertificationRequest csr1, PKCS10CertificationRequest csr2) {
		try {
			RSAPublicKey key1 = (RSAPublicKey) getPublicKey(csr1.getSubjectPublicKeyInfo());
			RSAPublicKey key2 = (RSAPublicKey) getPublicKey(csr2.getSubjectPublicKeyInfo());
			return isSamePublicKey(key1, key2);
		} catch (Exception e) {
			logger.error("compare public key error", e);
		}
		return false;
	}

	public static boolean isSamePublicKey(RSAPublicKey key1, RSAPublicKey key2) {
		try {
			return key1.getModulus().equals(key2.getModulus())
					&& key1.getPublicExponent().equals(key2.getPublicExponent());
		} catch (Exception e) {
			logger.error("compare public key error", e);
		}
		return false;
	}

	public static PKCS10CertificationRequest encodeToCsr(byte[] encoded) {
		try {
			return new PKCS10CertificationRequest(encoded);
		} catch (Exception e) {
			logger.error("Encoded array to CSR error", e);
		}
		return null;
	}

	public static PKCS10CertificationRequest pemToCsr(byte[] bs) {
		PemReader reader = null;
		try {
			reader = new PemReader(new InputStreamReader(new ByteArrayInputStream(bs)));
			PemObject pem = reader.readPemObject();
			PKCS10CertificationRequest request = new PKCS10CertificationRequest(pem.getContent());
			return request;
		} catch (Exception e) {
			logger.error("Read PEM CSR error", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return null;
	}

	/** 证书转pem字符串 */
	public static String toPemText(X509Certificate... certChain) throws IOException {
		ByteArrayOutputStream barr = new ByteArrayOutputStream();
		PemWriter writer = new PemWriter(new OutputStreamWriter(barr));
		for (X509Certificate cert : certChain) {
			writer.writeObject(new JcaMiscPEMGenerator(cert));
		}
		writer.close();
		return barr.toString();
	}

	/** 证书写到输出流 */
	public static void writePemCert(OutputStream out, X509Certificate... certs) throws IOException {
		PemWriter writer = new PemWriter(new OutputStreamWriter(out));
		for (X509Certificate cert : certs) {
			writer.writeObject(new JcaMiscPEMGenerator(cert));
		}
		writer.close();
	}

	/** 证书存储到文件 */
	public static void writePemCert(File file, X509Certificate... certs) throws IOException {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		writePemCert(new FileOutputStream(file), certs);
	}

	/** 读取Pem证书文件 */
	public static X509Certificate readPemCert(File file) throws Exception {
		if (file.exists() == false) {
			return null;
		}
		PemReader reader = new PemReader(new FileReader(file));
		PemObject pemObject = reader.readPemObject();
		X509CertificateHolder holder = new X509CertificateHolder(pemObject.getContent());
		X509Certificate cert = new JcaX509CertificateConverter().setProvider(SSL_PROVIDER_NAME)
				.getCertificate(holder);
		reader.close();
		return cert;
	}

	/** 读取Der证书文件 */
	public static X509Certificate readDerCert(String crtPath) throws Exception {
		InputStream inStream = new FileInputStream(crtPath);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
		inStream.close();
		return cert;
	}

	public static X509Certificate toX509Certificate(byte[] bs) throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bs));
		return cert;
	}

	/** 验证csr */
	public static boolean verifyCsr(PKCS10CertificationRequest csr) {
		try {
			return csr.isSignatureValid(new JcaContentVerifierProviderBuilder()
					.setProvider(SSL_PROVIDER_NAME).build(csr.getSubjectPublicKeyInfo()));
		} catch (Exception e) {
			logger.error("csr verify error", e);
		}
		return false;
	}

	public static void storeInPkcs12File(PrivateKey privateKey, Certificate[] certs, String keyStorePassword,
			OutputStream out) {
		try {
			KeyStore keystore = KeyStore.getInstance(KS_TYPE_PKCS12, SSL_PROVIDER_NAME);
			keystore.load(null, keyStorePassword.toCharArray());
			keystore.setKeyEntry("client", privateKey, keyStorePassword.toCharArray(), certs);
			keystore.store(out, keyStorePassword.toCharArray());
		} catch (Exception e) {
			logger.error("store pkcs12 error", e);
		}
	}

	public static void storeInPkcs12File(PrivateKey privateKey, Certificate[] certs, String keyStorePassword,
			File keyStoreFile) {
		try {
			storeInPkcs12File(privateKey, certs, keyStorePassword, new FileOutputStream(keyStoreFile));
		} catch (FileNotFoundException e) {
			logger.error("", e);
		}
	}

	public static boolean equalsPrincipal(X500Principal issuer, X500Principal subject) {
		try {
			return new X500Name(issuer.getName()).equals(new X500Name(subject.getName()));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取DN
	 *
	 * @param country
	 *            country name
	 * @param stateOrProvince
	 *            state or province name
	 * @param locality
	 *            locality name
	 * @param organization
	 *            organization name
	 * @param organizationalUnit
	 *            organizational unit name
	 * @param emailAddress
	 *            email address
	 * @param commonName
	 *            common name
	 * @return
	 */
	public static X500Name getX500Name(String country, String stateOrProvince, String locality, String organization,
			String organizationalUnit, String commonName, String emailAddress) {
		X500NameBuilder builder = new X500NameBuilder(X500Name.getDefaultStyle());
		builder.addRDN(BCStyle.C, country);
		builder.addRDN(BCStyle.ST, stateOrProvince);
		builder.addRDN(BCStyle.L, locality);
		builder.addRDN(BCStyle.O, organization);
		builder.addRDN(BCStyle.OU, organizationalUnit);
		builder.addRDN(BCStyle.CN, commonName);
		builder.addRDN(BCStyle.E, emailAddress);
		return builder.build();
	}

	public static X500Name appendDn(X500Name x500Name, String uid) {
		X500NameBuilder builder = new X500NameBuilder(X500Name.getDefaultStyle());
		for (RDN rdn : x500Name.getRDNs()) {
			builder.addRDN(rdn.getFirst());
		}
		builder.addRDN(BCStyle.UID, uid);
		return builder.build();
	}

	/**
	 * 获取X500Name的属性
	 *
	 * @param x500Name
	 * @param attr
	 *            BCStyle.CN BCStyle.C BCStyle.E...
	 * @return
	 */
	public static String getAttr(X500Name x500Name, ASN1ObjectIdentifier attr) {
		RDN cn = x500Name.getRDNs(attr)[0];
		return IETFUtils.valueToString(cn.getFirst().getValue());
	}

	public static String getCN(X500Name x500Name) {
		return getAttr(x500Name, BCStyle.CN);
	}

	public static X509Certificate findIssuerToCertificate(X509Certificate issuer, List<X509Certificate> certs) {
		for (X509Certificate cert : certs) {
			if (cert.equals(issuer)) {
				continue;
			}
			if (equalsPrincipal(cert.getIssuerX500Principal(), issuer.getSubjectX500Principal())) {
				return cert;
			}
		}
		return null;
	}

	public static X509Certificate findIssuerCertificate(X509Certificate subject, List<X509Certificate> certs) {
		for (X509Certificate cert : certs) {
			if (cert.equals(subject)) {
				continue;
			}
			if (equalsPrincipal(subject.getIssuerX500Principal(), cert.getSubjectX500Principal())) {
				return cert;
			}
		}
		return null;
	}

	public static X509Certificate findEndEntityCertificate(List<X509Certificate> certs) {
		if (certs.size() == 1) {
			return certs.get(0);
		}
		ArrayList<X509Certificate> rootList = new ArrayList<X509Certificate>();
		for (int i = 0; i < certs.size(); i++) {
			X509Certificate cert = certs.get(i);
			if (cert.getIssuerDN().equals(cert.getSubjectDN())) {
				rootList.add(cert);
				continue;
			}
			X509Certificate issuerCert = findIssuerCertificate(cert, certs);
			if (issuerCert == null) {
				rootList.add(cert);
				continue;
			}
		}
		// can only have one end entity cert - something's wrong, give up.
		if (rootList.size() > 1) {
			StringBuffer msg = new StringBuffer();
			msg.append("more than one root certificate found: \n");

			for (X509Certificate cert : rootList) {
				msg.append(cert.getSubjectDN().getName());
				msg.append("\n");
			}

			logger.error(msg.toString());
			return null;
		}
		return rootList.get(0);
	}

	/**
	 * Attempt to order the supplied list of X.509 certificates in <b>issued to</b>
	 * to <b>issued from</b> order.
	 *
	 * @param certs
	 * @return the sotred certificate chain in <b>issued to</b> to <b>issued
	 *         from</b> order
	 * @throws CryptoException
	 *             if the certificate chain is borken
	 */
	public static List<X509Certificate> sortCertificates(List<X509Certificate> certs) {
		if (certs.size() < 2) {
			return certs;
		}
		// find end-entity cert
		List<X509Certificate> retList = new ArrayList<X509Certificate>(certs.size());
		List<X509Certificate> originalList = new ArrayList<X509Certificate>(certs);

		X509Certificate rootCert = findEndEntityCertificate(originalList);

		retList.add(rootCert);
		originalList.remove(rootCert);

		X509Certificate currentIssuer = rootCert;
		while (true) {
			if (originalList.size() == 0) {
				break;
			}
			X509Certificate issuerTo = findIssuerToCertificate(currentIssuer, originalList);

			if (issuerTo == null) {
				if (originalList.size() > 0) {
					logger.error("Broken certificate chain");
					return null;
				}
			} else {
				retList.add(issuerTo);
				originalList.remove(issuerTo);
				currentIssuer = issuerTo;
			}
		}
		// reverse to issued to to issued from order.
		Collections.reverse(retList);
		return retList;
	}

	public static boolean writePemKey(OutputStream outputStream, PublicKey pubKey) {
		try {
			PemWriter writer = new PemWriter(new OutputStreamWriter(outputStream));
			writer.writeObject(new PemObject("PUBLIC KEY", pubKey.getEncoded()));
			writer.close();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	public static boolean writePemKey(File file, PublicKey pubKey) {
		try {
			return writePemKey(new FileOutputStream(file), pubKey);
		} catch (FileNotFoundException e) {
			logger.error("", e);
			return false;
		}
	}

	public static boolean writePemKey(OutputStream outputStream, PrivateKey priKey) {
		try {
			PemWriter writer = new PemWriter(new OutputStreamWriter(outputStream));
			writer.writeObject(new PemObject("PRIVATE KEY", priKey.getEncoded()));
			writer.close();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	public static boolean writePemKey(File file, PrivateKey priKey) {
		try {
			return writePemKey(new FileOutputStream(file), priKey);
		} catch (FileNotFoundException e) {
			logger.error("", e);
			return false;
		}
	}

	public static PublicKey readPemPubKey(InputStream inputStream) {
		try {
			PEMParser reader = new PEMParser(new InputStreamReader(inputStream));
			byte[] pubKey = reader.readPemObject().getContent();
			reader.close();

			KeyFactory keyFactory = KeyFactory.getInstance(ALG_ENCRYPT_ASYMMETRIC);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKey);
			return keyFactory.generatePublic(pubKeySpec);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public static PublicKey readPemPubKey(File file) {
		try {
			return readPemPubKey(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("", e);
			return null;
		}
	}

	public static PrivateKey readPemPriKey(InputStream inputStream) {
		try {
			PEMParser reader = new PEMParser(new InputStreamReader(inputStream));
			byte[] priKey = reader.readPemObject().getContent();
			reader.close();

			KeyFactory keyFactory = KeyFactory.getInstance(ALG_ENCRYPT_ASYMMETRIC);
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(priKey));
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public static PrivateKey readPemPriKey(File file) {
		try {
			return readPemPriKey(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("", e);
			return null;
		}
	}

}
