package de.tuberlin.amos.ws17.swit.information_source;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.Nullable;
import java.util.Arrays;

public class KnowledgeGraphSearch implements InformationProvider {

    private static final String API_KEY = ApiConfig.getProperty("KnowledgeGraphSearch");
    private static final String LANGUAGE = "de";
    private static KnowledgeGraphSearch instance;

    private KnowledgeGraphSearch() {
    }

    public static InformationProvider getInstance() throws ServiceNotAvailableException {
        if (instance == null) {
            instance = new KnowledgeGraphSearch();
        }
        return instance;
    }

    private GenericUrl createGenericUrl() {
        GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
        url.put("limit", "10");
        url.put("indent", "true");
        url.put("key", API_KEY);
        url.set("languages", LANGUAGE);
        return url;
    }

    @Nullable
    private Tuple<String, String> getInfoById(String id) {
        GenericUrl url = createGenericUrl();
        url.put("ids", id);
        return getInfoAndWikiUrl(url);
    }

    @Nullable
    private Tuple<String, String> getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        return getInfoAndWikiUrl(url);
    }

    @Override
    public PointOfInterest setInfoAndUrl(PointOfInterest poi) throws ServiceNotAvailableException {
        // try with id
        Tuple<String, String> result = null;

        if (StringUtils.isEmpty(poi.getId())) {
            // check if id is a kgs id
            if (poi.getId().contains("/m/")) {
                result = getInfoById(poi.getId());
            }
        }
        // if it fails, try with name
        if (result == null) {
            result = getInfoByName(poi.getName());
        }

        if (result != null) {
            poi.setInformationAbstract(result.x);
            poi.setWikiUrl(result.y);
        }
        return poi;
    }

    @Nullable
    private Tuple<String, String> getInfoAndWikiUrl(GenericUrl url) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray elements = (JSONArray) response.get("itemListElement");

            for (Object element : elements) {
                // only use first element
                System.out.println(url.get("query"));

                String info = JsonPath.read(element, "$.result.detailedDescription.articleBody").toString();
                String wikiUrl = JsonPath.read(element, "$.result.detailedDescription.url").toString();
                return new Tuple<>(info, wikiUrl);
            }
        } catch(HttpResponseException hre) {
            System.out.println("Bad Request");
        } catch (PathNotFoundException pnfe) {
            System.out.println("No info found for POI");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getNameFromUrl(String wikiUrl) {
        if (!wikiUrl.equals("")) {
            String[] temp = wikiUrl.split("/");
            return temp[temp.length - 1];
        }
        return "";
    }

    @Nullable
    public String getLanguageFromUrl(String wikiUrl) {
        String[] temp = wikiUrl.split("/");
        System.out.println(Arrays.toString(temp));
        String[] language = temp[2].split("\\.");
        if (!language[0].equals("")) {
            return language[0];
        }
        return null;
    }

    class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
