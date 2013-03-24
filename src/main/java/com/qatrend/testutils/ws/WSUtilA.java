package com.qatrend.testutils.ws;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.qatrend.testutils.logging.PLogger;

public class WSUtilA {
	private static final String KEY_STORE_PATH = "src/main/resources/keys/idealclient_protected.jks";
	private static final String KEY_STORE_PASSWORD = "passwordstg2";

	public static HttpResponse getRequest(String serviceUrl) {
		HttpResponse response = null;
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream keystoreInput = new FileInputStream(KEY_STORE_PATH);
			keystore.load(keystoreInput, KEY_STORE_PASSWORD.toCharArray());
			System.out.println("Keystore has " + keystore.size() + " keys");

			// load the truststore, leave it null to rely on cacerts distributed
			// with the JVM
			// KeyStore truststore =
			// KeyStore.getInstance(KeyStore.getDefaultType());
			// KeyStore truststore = null;
			// truststore.load(new FileInputStream("server.jks"),
			// "bara".toCharArray());

			// JSSE Algorithms - RSA, DSA, AES -
			// http://docs.oracle.com/javase/6/docs/technotes/guides/security/jsse/JSSERefGuide.html#Introduction

			KeyManagerFactory keyManagerFact = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFact.init(keystore, KEY_STORE_PASSWORD.toCharArray());
			KeyManager[] keyManagers = keyManagerFact.getKeyManagers();

			TrustManagerFactory trustManagerFact = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			trustManagerFact.init(keystore);
			TrustManager[] trustManagers = trustManagerFact.getTrustManagers();

			SSLContext sslContext = SSLContext.getInstance("SSL");

			// set up a TrustManager that trusts everything
			sslContext.init(keyManagers, new TrustManager[] { new X509TrustManager() { // sslContext.init(null,
																						// new
																						// TrustManager[]
																						// {
																						// new
																						// X509TrustManager()
																						// {
						public X509Certificate[] getAcceptedIssuers() {
							System.out.println("getAcceptedIssuers =============");
							return null;
						}

						public void checkClientTrusted(X509Certificate[] certs, String authType) {
							System.out.println("checkClientTrusted =============");
						}

						public void checkServerTrusted(X509Certificate[] certs, String authType) {
							System.out.println("checkServerTrusted =============");
						}
					} }, new SecureRandom());

			SchemeRegistry schemeRegistry = new SchemeRegistry();

			// SSLSocketFactory socketFactory = new MySSLSocketFactory(keystore,
			// KEY_STORE_PASSWORD, keystore);
			// socketFactory.setHostnameVerifier(socketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
			socketFactory.setHostnameVerifier(socketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, socketFactory));

			DefaultHttpClient httpclient = new DefaultHttpClient();
			URI targetURI = new URI(serviceUrl);

			// This is the default port number only; others are allowed
			Scheme sch = new Scheme("https", 443, socketFactory);
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			// httpclient.getConnectionManager().

			HttpGet httpget = new HttpGet(targetURI);
			String url = serviceUrl;

			PLogger.getLogger().info("Service url: " + serviceUrl);
			PLogger.getLogger().info("Executing request " + httpget.getRequestLine());
			// Message "peer not authenticated" means the server presented
			// a certificate that was not found in the local truststore.
			response = httpclient.execute(httpget);
			PLogger.getLogger().info("Response code: " + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			byte[] responseBytes = EntityUtils.toByteArray(entity);
			String responseTxt = new String(responseBytes);
			PLogger.getLogger().debug("Response Text:\n" + responseTxt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public static HttpResponse postRequest(String serviceUrl, String xmlReqStr) {
		HttpResponse response = null;
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream keystoreInput = new FileInputStream(KEY_STORE_PATH);
			keystore.load(keystoreInput, KEY_STORE_PASSWORD.toCharArray());
			System.out.println("Keystore has " + keystore.size() + " keys");

			// load the truststore, leave it null to rely on cacerts distributed
			// with the JVM
			// KeyStore truststore =
			// KeyStore.getInstance(KeyStore.getDefaultType());
			// KeyStore truststore = null;
			// truststore.load(new FileInputStream("server.jks"),
			// "bara".toCharArray());

			// JSSE Algorithms - RSA, DSA, AES -
			// http://docs.oracle.com/javase/6/docs/technotes/guides/security/jsse/JSSERefGuide.html#Introduction

			KeyManagerFactory keyManagerFact = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFact.init(keystore, KEY_STORE_PASSWORD.toCharArray());
			KeyManager[] keyManagers = keyManagerFact.getKeyManagers();

			TrustManagerFactory trustManagerFact = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			trustManagerFact.init(keystore);
			TrustManager[] trustManagers = trustManagerFact.getTrustManagers();

			SSLContext sslContext = SSLContext.getInstance("SSL");

			// set up a TrustManager that trusts everything
			sslContext.init(keyManagers, new TrustManager[] { new X509TrustManager() { // sslContext.init(null,
																						// new
																						// TrustManager[]
																						// {
																						// new
																						// X509TrustManager()
																						// {
						public X509Certificate[] getAcceptedIssuers() {
							System.out.println("getAcceptedIssuers =============");
							return null;
						}

						public void checkClientTrusted(X509Certificate[] certs, String authType) {
							System.out.println("checkClientTrusted =============");
						}

						public void checkServerTrusted(X509Certificate[] certs, String authType) {
							System.out.println("checkServerTrusted =============");
						}
					} }, new SecureRandom());

			SchemeRegistry schemeRegistry = new SchemeRegistry();

			// SSLSocketFactory socketFactory = new MySSLSocketFactory(keystore,
			// KEY_STORE_PASSWORD, keystore);
			// socketFactory.setHostnameVerifier(socketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);
			socketFactory.setHostnameVerifier(socketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			schemeRegistry.register(new Scheme("https", 443, socketFactory));

			DefaultHttpClient httpclient = new DefaultHttpClient();
			URI targetURI = new URI(serviceUrl);

			// This is the default port number only; others are allowed
			Scheme sch = new Scheme("https", 443, socketFactory);
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			// httpclient.getConnectionManager().

			// SOAP request send
			HttpPost post = new HttpPost(serviceUrl);
			ByteArrayEntity myEntity = new ByteArrayEntity(xmlReqStr.getBytes());
			post.setEntity(myEntity);
			// post.setEntity(new InputStreamEntity(new
			// FileInputStream(req_xml), req_xml.length()));
			post.setHeader("Content-type", "text/xml; charset=UTF-8");
			// post.setHeader("SOAPAction", "");
			response = httpclient.execute(post);

			// SOAP response(xml) get
			// String res_xml = EntityUtils.toString(response.getEntity());
			// Content-Type: text/xml;charset=UTF-8
			// String encoding =
			// response.getLastHeader("Content-Encoding").toString();
			String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			PLogger.getLogger().info("Service url: " + serviceUrl);
			PLogger.getLogger().debug("Request message:\n" + xmlReqStr);
			PLogger.getLogger().info("Response code: " + response.getStatusLine());
			PLogger.getLogger().debug("Response text:\n" + body);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	public static String getResponseCode(HttpResponse response) {
		String retCode = null;
		if (response != null) {
			retCode = response.getStatusLine().toString();
		} else {
			System.out.println("Response is null");
		}
		return retCode;
	}

	public static String getResponseText(HttpResponse response) {
		String responseTxt = null;
		try {
			if (response != null) {
				HttpEntity entity = response.getEntity();
				byte[] responseBytes = EntityUtils.toByteArray(entity);
				responseTxt = new String(responseBytes);
			} else {
				System.out.println("Response is null");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return responseTxt;
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore keystore, String pass, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(keystore, pass, truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

}
