package com.qatrend.testutils.http;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;

public class JerseyClient {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    
    String url;
    Map<String, String> restHeaders = new HashMap<String, String>();

    public JerseyClient(String targetUrl, Map<String, String> headers) {
        this.url = targetUrl;
        this.restHeaders = headers;
    }

    public JerseyClient(String targetUrl) {
        this.url = targetUrl;
        Map<String, String> headers = new HashMap<String, String>();
        this.restHeaders = headers;
    }

    public String getXml() {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client.target(this.url);
        /**
         * Using JAX-RS Client API fluently String entity =
         * client.target("http://example.com/rest")
         * .register(FilterForExampleCom.class) .path("resource/helloworld")
         * .queryParam("greeting", "Hi World!")
         * .request(MediaType.TEXT_PLAIN_TYPE) .header("some-header", "true")
         * .get(String.class);
         */
        Invocation.Builder invocationBuilder = webTarget
                .request(MediaType.APPLICATION_XML);
        for (String key : this.restHeaders.keySet()) {
            invocationBuilder.header(key, this.restHeaders.get(key));
        }

        Response response = invocationBuilder.get();
        logger.debug("Response status: " + response.getStatus());
        String responseText = response.readEntity(String.class);
        logger.debug(responseText);
        client.close();
        return responseText;
    }

    public String getJson() {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget webTarget = client.target(this.url);
        /**
         * Using JAX-RS Client API fluently String entity =
         * client.target("http://example.com/rest")
         * .register(FilterForExampleCom.class) .path("resource/helloworld")
         * .queryParam("greeting", "Hi World!")
         * .request(MediaType.TEXT_PLAIN_TYPE) .header("some-header", "true")
         * .get(String.class);
         */
        Invocation.Builder invocationBuilder = webTarget
                .request(MediaType.APPLICATION_JSON_TYPE);
        for (String key : this.restHeaders.keySet()) {
            invocationBuilder.header(key, this.restHeaders.get(key));
        }

        Response response = invocationBuilder.get();
        logger.debug("Response status: " + response.getStatus());
        String responseText = response.readEntity(String.class);
        logger.debug(responseText);
        client.close();
        return responseText;
    }
    
    public String postXml(String xmlReqStr) {
        String responseText = null;
        try {
            ClientConfig clientConfig = new ClientConfig();
            Client client = ClientBuilder.newClient(clientConfig);

            WebTarget webTarget = client.target(this.url);
            /**
             * Using JAX-RS Client API fluently String entity =
             * client.target("http://example.com/rest")
             * .register(FilterForExampleCom.class) .path("resource/helloworld")
             * .queryParam("greeting", "Hi World!")
             * .request(MediaType.TEXT_PLAIN_TYPE) .header("some-header", "true")
             * .get(String.class);
             */
            Invocation.Builder invocationBuilder = webTarget
                    .request(MediaType.APPLICATION_XML);
            for (String key : this.restHeaders.keySet()) {
                invocationBuilder.header(key, this.restHeaders.get(key));
            }
            Response response = invocationBuilder.post(Entity.xml(xmlReqStr));
            logger.debug("Response status: " + response.getStatus());
            responseText = response.readEntity(String.class);
            logger.debug(responseText);
            client.close();
        } catch(Exception ex) {
            logger.error(ex);
        }
        return responseText;
    }

    public String putXml(String xmlReqStr) {
        String responseText = null;
        try {
            ClientConfig clientConfig = new ClientConfig();
            Client client = ClientBuilder.newClient(clientConfig);

            WebTarget webTarget = client.target(this.url);
            Invocation.Builder invocationBuilder = webTarget
                    .request(MediaType.APPLICATION_XML);
            for (String key : this.restHeaders.keySet()) {
                invocationBuilder.header(key, this.restHeaders.get(key));
            }
            Response response = invocationBuilder.put(Entity.xml(xmlReqStr));
            logger.debug("Response status: " + response.getStatus());
            responseText = response.readEntity(String.class);
            logger.debug(responseText);
            client.close();
        } catch(Exception ex) {
            logger.error(ex);
        }
        return responseText;
    }

    public String putJson(String jsonReqStr) {
        String responseText = null;
        try {
            ClientConfig clientConfig = new ClientConfig();
            Client client = ClientBuilder.newClient(clientConfig);

            WebTarget webTarget = client.target(this.url);
            Invocation.Builder invocationBuilder = webTarget
                    .request(MediaType.APPLICATION_JSON_TYPE);
            for (String key : this.restHeaders.keySet()) {
                invocationBuilder.header(key, this.restHeaders.get(key));
            }
            Response response = invocationBuilder.put(Entity.json(jsonReqStr));
            logger.debug("Response status: " + response.getStatus());
            responseText = response.readEntity(String.class);
            logger.debug(responseText);
            client.close();
        } catch(Exception ex) {
            logger.error(ex);
        }
        return responseText;
    }
    
    public String postJson(String jsonReqStr) {
        String responseText = null;
        try {
            ClientConfig clientConfig = new ClientConfig();
            Client client = ClientBuilder.newClient(clientConfig);

            WebTarget webTarget = client.target(this.url);
            /**
             * Using JAX-RS Client API fluently String entity =
             * client.target("http://example.com/rest")
             * .register(FilterForExampleCom.class) .path("resource/helloworld")
             * .queryParam("greeting", "Hi World!")
             * .request(MediaType.TEXT_PLAIN_TYPE) .header("some-header", "true")
             * .get(String.class);
             */
            Invocation.Builder invocationBuilder = webTarget
                    .request(MediaType.APPLICATION_JSON_TYPE);
            for (String key : this.restHeaders.keySet()) {
                invocationBuilder.header(key, this.restHeaders.get(key));
            }
            Response response = invocationBuilder.post(Entity.json(jsonReqStr));
            logger.debug("Response status: " + response.getStatus());
            responseText = response.readEntity(String.class);
            logger.debug(responseText);
            client.close();
        } catch(Exception ex) {
            logger.error(ex);
        }
        return responseText;
    }
    
    
}