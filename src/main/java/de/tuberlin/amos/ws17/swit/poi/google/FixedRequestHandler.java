package de.tuberlin.amos.ws17.swit.poi.google;

import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import se.walkercrou.places.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


/**
 * An implementation of the {@link RequestHandler}  that works (fix)
 */
public class FixedRequestHandler implements RequestHandler {
	public static void enableLogging() {
		  Logger logger = Logger.getLogger(HttpTransport.class.getName());
		  logger.setLevel(Level.CONFIG);
		  logger.addHandler(new Handler() {

		    @Override
		    public void close() throws SecurityException {
		    }

		    @Override
		    public void flush() {
		    }

		    @Override
		    public void publish(LogRecord record) {
		      // default ConsoleHandler will print &gt;= INFO to System.err
		      if (record.getLevel().intValue() < Level.INFO.intValue()) {
		        System.out.println(record.getMessage());
		      }
		    }
		  });
		}
	
	
	
	
	
   private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
   private final HttpClient client = HttpClientBuilder.create().build();
   private String characterEncoding;

   /**
    * Creates a new handler with the specified character encoding.
    *
    * @param characterEncoding to use
    */
   private FixedRequestHandler(String characterEncoding) {
       this.characterEncoding = characterEncoding;
   }

   /**
    * Creates a new handler with UTF-8 character encoding.
    */
   FixedRequestHandler() {
       this(DEFAULT_CHARACTER_ENCODING);
   }

   /**
    * Returns the character encoding used by this handler.
    *
    * @return character encoding
    */
   public String getCharacterEncoding() {
       return characterEncoding;
   }

   /**
    * Sets the character encoding used by this handler.
    *
    * @param characterEncoding to use
    */
   public void setCharacterEncoding(String characterEncoding) {
       this.characterEncoding = characterEncoding;
   }

   private String readString(HttpResponse response) throws IOException {
       String str = IOUtils.toString(response.getEntity().getContent(), characterEncoding);
       if (str == null || str.trim().length() == 0) {
           return null;
       }
       return str.trim();
   }

   public InputStream getInputStream(String uri) throws IOException {
       try {
           HttpGet get = new HttpGet(uri);
           return client.execute(get).getEntity().getContent();
       } catch (Exception e) {
           throw new IOException(e);
       }
   }

    
    public String get(String uri) throws IOException {

    	final HttpRequest request = HttpUtils.constructHttpRequest(HttpMethods.GET, uri);

            try {
                com.google.api.client.http.HttpResponse response= request.execute();
                return response.parseAsString();
		} catch (HttpResponseException e) {
			System.err.println(e.getStatusMessage());

			return null;
		}
    	
    }

    public String post(HttpPost data) throws IOException {
        try {
            return readString(client.execute(data));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
