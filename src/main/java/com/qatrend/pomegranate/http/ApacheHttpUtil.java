package com.qatrend.pomegranate.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.qatrend.pomegranate.logging.PLogger;


public class ApacheHttpUtil {
	private static ApacheHttpUtil instance = null;
	private static DefaultHttpClient client = null;
	private static BasicHttpContext context = null;
	
	public static enum AuthType {
		BASIC,
		USERNAME_PASSWORD;
	}

	public static enum ResponsePart {
		RESPONSE,
		CONTENTS;
	}
	
	protected ApacheHttpUtil(String username, String password){
		client = new DefaultHttpClient();
		// Then provide the right credentials
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		// Generate BASIC scheme object and stick it to the execution context
		BasicScheme basicAuth = new BasicScheme();
		context = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);
		// Add as the first (because of the zero) request interceptor
		// It will first intercept the request and preemptively initialize the authentication scheme if there is not
		client.addRequestInterceptor(new PreemptiveAuth(), 0);
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		//trustAllCertificates();
	}
	protected ApacheHttpUtil(){
		client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		//trustAllCertificates();
	}

	public static ApacheHttpUtil getInstance(String username, String password){
		if(instance == null) {
			instance = new ApacheHttpUtil(username, password);
		}
		return instance;
	}
	public static ApacheHttpUtil getInstance(){
		if(instance == null) {
			instance = new ApacheHttpUtil();
		}
		return instance;
	}
	
	public HttpResponse getHttpResponse(String url) {
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = client.execute(get, context);
			PLogger.getLogger().debug( "Response: \n" + response);
			HttpEntity entity = response.getEntity();
			//print contents
		    InputStream instream = entity.getContent();
			String responseText = getStringFromStream(instream);
			PLogger.getLogger().debug( "Response contents: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public String getHttpResponseContents(String url) {
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		String responseText = "";
		try {
			response = client.execute(get, context);
			//PLogger.getLogger().debug( "Response: \n" + response);
			HttpEntity entity = response.getEntity();
			//print contents
		    InputStream instream = entity.getContent();
			responseText = getStringFromStream(instream);
			//PLogger.getLogger().debug( "Response contents: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseText;
	}

	public String postHttpResponseContents(String url, String postMsg) {
		HttpPost post = new HttpPost(url);
		HttpResponse response = null;
		String responseText = "";
		try {
			response = client.execute(post, context);
			//PLogger.getLogger().debug( "Response: \n" + response);
			HttpEntity entity = response.getEntity();
			//print contents
		    InputStream instream = entity.getContent();
			responseText = getStringFromStream(instream);
			//PLogger.getLogger().debug( "Response contents: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseText;
	}
	

	public HashMap<ResponsePart, String> getHttpResponseAndContents(String url) {
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		String responseText = "";
		HashMap<ResponsePart, String> responseMap = new HashMap<ResponsePart, String>();
		try {
			response = client.execute(get, context);
			//PLogger.getLogger().debug( "Response: \n" + response);
			responseMap.put(ResponsePart.RESPONSE, response.toString());
			HttpEntity entity = response.getEntity();
			//print contents
		    InputStream instream = entity.getContent();
			responseText = getStringFromStream(instream);
			responseMap.put(ResponsePart.CONTENTS, responseText);
			PLogger.getLogger().debug( "Response contents: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseMap;
	}
	
	
	public HttpResponse postHttpResponse(String url, List<NameValuePair> qparams) {
		HttpResponse response = null;
		try {
			URI uri = new URI(url);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			String path = uri.getPath();
			uri = URIUtils.createURI(scheme, host, -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null);

			HttpPost httppost = new HttpPost(uri);
			response = client.execute(httppost, context);
			PLogger.getLogger().debug( "Response: \n" + response);
			HttpEntity entity = response.getEntity();
			//print contents
			InputStream instream = entity.getContent();
			String responseText = getStringFromStream(instream);
			PLogger.getLogger().debug( "Response Text: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public HttpResponse postHttpResponse(String url, String largeString) {
		HttpResponse response = null;
		try {
			URI uri = new URI(url);
			HttpPost httppost = new HttpPost(uri);
			//StringEntity strEntity = new StringEntity(largeString, "text/plain; charset=\"UTF-8\"");  //application/x-www-form-urlencoded; charset=UTF-8
			StringEntity strEntity = new StringEntity(largeString, HTTP.UTF_8);  //application/x-www-form-urlencoded; charset=UTF-8
			httppost.setEntity(strEntity);
			response = client.execute(httppost, context);
			PLogger.getLogger().debug( "Response: \n" + response);
			HttpEntity entity = response.getEntity();
			//print contents
			InputStream instream = entity.getContent();
			String responseText = getStringFromStream(instream);
			PLogger.getLogger().debug( "Response Text: \n" + responseText);
			EntityUtils.consume(entity);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	
	public static String getStringFromStream(InputStream is){
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String theString = writer.toString();
		return theString;
	}
	
	/**
	 * Preemptive authentication interceptor
	 *
	 */
 	static class PreemptiveAuth implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
		 * org.apache.http.protocol.HttpContext)
		 */
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			// Get the AuthState
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

		}

	}

 	private void trustAllCertificates(){
 		try {
 			SSLContext ctx = SSLContext.getInstance("TLS");
 			X509TrustManager tm = new X509TrustManager() {
 			 
 			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
 			}
 			 
 			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
 			}
 			 
 			public X509Certificate[] getAcceptedIssuers() {
 			return null;
 			}
 			};
 			ctx.init(null, new TrustManager[]{tm}, null);
 			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
 			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
 			ClientConnectionManager ccm = client.getConnectionManager();
 			SchemeRegistry sr = ccm.getSchemeRegistry();
 			sr.register(new Scheme("https", ssf, 443));
 			client = new DefaultHttpClient(ccm, client.getParams());
 			} catch (Exception ex) {
 			ex.printStackTrace();
 			} 	
 			}

 	
 	
	public static String mapToURLEnc(String hostURL, Map<String, String> paramsMap){
		String url="";
		String query = "";
		BasicNameValuePair nvp = null; 
		List<BasicNameValuePair> nvpList = new ArrayList<BasicNameValuePair>();
		//create npv list from map
		for(String key : paramsMap.keySet() ) {
			nvp = new BasicNameValuePair( key, paramsMap.get(key) );
			nvpList.add(nvp);
		}
		query = URLEncodedUtils.format(nvpList, "UTF-8");
		url = hostURL + "?" + query;
		
		return url;
	}

	
}
