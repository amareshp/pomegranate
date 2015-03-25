package com.qatrend.pomegranate.ws;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.qatrend.pomegranate.logging.PLogger;


public class WSUtilJ {
	
	public static WSResponse sendASFMsg(String urlStr, String reqMsg) {
		WSResponse asfResponse = new WSResponse();
		String result = "";
		
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
			
			HostnameVerifier hv = new HostnameVerifier()
			{
				public boolean verify(String urlHostName, SSLSession session)
				{
					PLogger.getLogger().debug("Warning: URL Host: " + urlHostName + " vs. "
							+ session.getPeerHost());
					return true;
				}
			};			

			// Install the all-trusting trust manager
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    HttpsURLConnection.setDefaultHostnameVerifier(hv);
		    
		    URL url = new URL( urlStr );
		    PLogger.getLogger().debug( "URL: " + url.toURI());
		    
		    //HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    con.setRequestMethod("POST");
		    //con.setSSLSocketFactory(sslsocketfactory);
		    
		    //set properties
		    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		    System.setProperty("javax.net.ssl.trustStoreType", "jks");
		    System.setProperty("javax.net.ssl.keyStore", "clientcertificate.p12");
		    System.setProperty("javax.net.ssl.trustStore", "gridserver.keystore");
		    System.setProperty("javax.net.debug", "ssl # very verbose debug");
		    System.setProperty("javax.net.ssl.keyStorePassword", "$PASS");
		    System.setProperty("javax.net.ssl.trustStorePassword", "$PASS");
		    
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setConnectTimeout( 20000 );  // long timeout, but not infinite
		    con.setReadTimeout( 20000 );

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    //con.setRequestProperty ( "Content-Type", "SOAP/XML-RPC" );
		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    //PLogger.getLogger().debug( reqMsg);
		    //JawsLogger.getJawsLogger().log(Level.FINE, reqMsg);
		    PLogger.getLogger().debug( "Request message: \n" + reqMsg);
		    //reqMsg = URLEncoder.encode(reqMsg, "UTF-8");
		    writer.write( reqMsg );
		    writer.flush();
		    //writer.close();

		    // reading the response
		    Thread.sleep(10000);
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    result = buf.toString();
		    //PLogger.getLogger().debug( "Response code: " + con.getResponseCode() + " Response message: " + con.getResponseMessage());
		    //PLogger.getLogger().debug(  "\nResponse from server after POST:\n" + result );
		    asfResponse.setResponseCode( con.getResponseCode() );
		    asfResponse.setResponseMessage( con.getResponseMessage() );
		    asfResponse.setResponseText(result);
		    PLogger.getLogger().debug( "Response message: \n" + asfResponse.getResponseText() );
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}
		return asfResponse;
		
	}

	public static WSResponse sendASFMsg(String urlStr, String reqMsg, String username, String password) {
		WSResponse asfResponse = new WSResponse();
		String result = "";
		final String username_1 = username;
		final String password_1 = password;
		
		try
		{
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
			
			HostnameVerifier hv = new HostnameVerifier()
			{
				public boolean verify(String urlHostName, SSLSession session)
				{
					PLogger.getLogger().debug( "Warning: URL Host: " + urlHostName + " vs. "
							+ session.getPeerHost());
					return true;
				}
			};			

			// Install the all-trusting trust manager
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		    HttpsURLConnection.setDefaultHostnameVerifier(hv);
		    
		    URL url = new URL( urlStr );
		    PLogger.getLogger().debug( "URL: " + url.toURI());
            Authenticator myAuth = new Authenticator() 
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username_1, password_1.toCharArray());
                }
            };

            Authenticator.setDefault(myAuth);
		    
		    //HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();
		    String authentication = (new sun.misc.BASE64Encoder()).encode( ( username + ":"+ password).getBytes() );
		    con.setRequestProperty("Authorization", authentication);
		    con.setRequestMethod("POST");
		    //con.setSSLSocketFactory(sslsocketfactory);
		    
		    //set properties
		    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		    System.setProperty("javax.net.ssl.trustStoreType", "jks");
		    System.setProperty("javax.net.ssl.keyStore", "clientcertificate.p12");
		    System.setProperty("javax.net.ssl.trustStore", "gridserver.keystore");
		    System.setProperty("javax.net.debug", "ssl # very verbose debug");
		    System.setProperty("javax.net.ssl.keyStorePassword", "$PASS");
		    System.setProperty("javax.net.ssl.trustStorePassword", "$PASS");
		    
		    // specify that we will send output and accept input
		    con.setDoInput(true);
		    con.setDoOutput(true);

		    con.setConnectTimeout( 20000 );  // long timeout, but not infinite
		    con.setReadTimeout( 20000 );

		    con.setUseCaches (false);
		    con.setDefaultUseCaches (false);

		    // tell the web server what we are sending
		    con.setRequestProperty ( "Content-Type", "text/xml" );
		    OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
		    //PLogger.getLogger().debug( reqMsg);
		    //JawsLogger.getJawsLogger().log(Level.FINE, reqMsg);
		    PLogger.getLogger().debug( reqMsg);
		    //reqMsg = URLEncoder.encode(reqMsg, "UTF-8");
		    writer.write( reqMsg );
		    writer.flush();
		    //writer.close();

		    // reading the response
		    Thread.sleep(10000);
		    InputStreamReader reader = new InputStreamReader( con.getInputStream() );

		    StringBuilder buf = new StringBuilder();
		    char[] cbuf = new char[ 2048 ];
		    int num;

		    while ( -1 != (num=reader.read( cbuf )))
		    {
		        buf.append( cbuf, 0, num );
		    }

		    result = buf.toString();
		    //PLogger.getLogger().debug( "Response code: " + con.getResponseCode() + " Response message: " + con.getResponseMessage());
		    //PLogger.getLogger().debug(  "\nResponse from server after POST:\n" + result );
		    asfResponse.setResponseCode( con.getResponseCode() );
		    asfResponse.setResponseMessage( con.getResponseMessage() );
		    asfResponse.setResponseText(result);
		}
		catch( Throwable t )
		{
		    t.printStackTrace( System.out );
		}
		return asfResponse;
		
	}

	
}
