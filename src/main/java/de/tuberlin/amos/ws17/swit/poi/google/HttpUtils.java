package de.tuberlin.amos.ws17.swit.poi.google;

import java.io.IOException;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.StringUtils;

/**
 * @author javatips.net
 * https://www.javatips.net/api/stubby4j-master/functional/java/io/github/azagniotov/stubby4j/HttpUtils.java
 */
public class HttpUtils {
	

	   private static final HttpRequestFactory WEB_CLIENT; 
	 
	   static { 
	      WEB_CLIENT = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() { 
	         public void initialize(final HttpRequest request) { 
	            request.setThrowExceptionOnExecuteError(false); 
	            request.setReadTimeout(45000); 
	            request.setConnectTimeout(45000); 
	         } 
	      }); 
	   } 
	 
	 
	   static HttpRequest constructHttpRequest(final String method, final String targetUrl) throws IOException {
	 
	      return WEB_CLIENT.buildRequest(method, 
	         new GenericUrl(targetUrl), 
	         null); 
	   } 
	 
	   public static HttpRequest constructHttpRequest(final String method, final String targetUrl, final String content) throws IOException { 
	 
	      return WEB_CLIENT.buildRequest(method, 
	         new GenericUrl(targetUrl), 
	         new ByteArrayContent(null, StringUtils.getBytesUtf8(content))); 
	   } 

}