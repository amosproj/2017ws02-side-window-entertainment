package de.tuberlin.amos.ws17.swit.information_source;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KnowledgeGraphSearch implements InformationProvider {

    private static final String API_KEY = "AIzaSyCDYEQsEKR6KXY23qbQC5Daih1RoW1vEho";

    @Override
    public String getInfoById(String id) {
        GenericUrl url = createGenericUrl();
        url.put("ids", id);
        return getDescription(url);
    }

    @Override
    public String getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        return getDescription(url);
    }

    private GenericUrl createGenericUrl() {
        GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
        url.put("limit", "10");
        url.put("indent", "true");
        url.put("key", API_KEY);
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

            for (Object element : elements) {
                String detailedDescription = JsonPath.read(element, "$.result.detailedDescription.articleBody").toString();
                if (!detailedDescription.isEmpty()) {
                    return detailedDescription;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
