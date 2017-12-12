package de.tuberlin.amos.ws17.swit.information_source;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.ServiceNotAvailableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KnowledgeGraphSearch implements InformationProvider {

    private static final String API_KEY = ApiConfig.getProperty("KnowledgeGraphSearch");
    private static final String LANGUAGE = ApiConfig.getProperty("language");
    private static KnowledgeGraphSearch instance;
    private String detailledInfo;
    private String ObjectUrl;

    private KnowledgeGraphSearch() {}

    public static InformationProvider getInstance() throws ServiceNotAvailableException {
        if (instance == null) {
            instance = new KnowledgeGraphSearch();
        }
        return instance;
    }

    @Override
    public String getInfoById(String id) {
        GenericUrl url = createGenericUrl();
        url.put("ids", id);
        getDescription(url);
        return detailledInfo;
    }

    @Override
    public String getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        getDescription(url);
        return detailledInfo;
    }

    @Override
    public PointOfInterest getUrlById(PointOfInterest poi) throws ServiceNotAvailableException {
        GenericUrl url = createGenericUrl();
        url.put("query", poi.getName());
        getDescription(url);
        poi.setInformationAbstract(ObjectUrl);
        return poi;
    }

    private GenericUrl createGenericUrl() {
        GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
        url.put("limit", "10");
        url.put("indent", "true");
        url.put("key", API_KEY);
        url.set("languages", LANGUAGE);
        return url;
    }

    private String getDescription(GenericUrl url) {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
            JSONArray elements = (JSONArray) response.get("itemListElement");

            for (Object element: elements) {
                String detailedDescription = JsonPath.read(element, "$.result.detailedDescription.articleBody").toString();
                String Objecturl = JsonPath.read(element, "$.result.detailedDescription.url").toString();
                if (!detailedDescription.isEmpty()) {
                    this.detailledInfo = detailedDescription;
                    this.ObjectUrl = Objecturl;
                    return detailedDescription;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
