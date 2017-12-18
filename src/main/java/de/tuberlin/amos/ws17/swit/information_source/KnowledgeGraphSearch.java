package de.tuberlin.amos.ws17.swit.information_source;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.ServiceNotAvailableException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;

public class KnowledgeGraphSearch implements InformationProvider {

    private static final String API_KEY = ApiConfig.getProperty("KnowledgeGraphSearch");
    private static final String LANGUAGE = "de";
    private static KnowledgeGraphSearch instance;
    private String detailedInfo = "";
    private String objectUrl;

    private KnowledgeGraphSearch() { }

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
        return getDescription(url);
    }

    @Override
    public String getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        return getDescription(url);
    }

    @Override
    public PointOfInterest getInfoByName(PointOfInterest poi) throws ServiceNotAvailableException {
        GenericUrl url = createGenericUrl();
        url.put("query", poi.getName());
        getDescription(url);
        if (this.detailedInfo.equals("")) {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava("Der Wikipedia Artikel ist leider nicht verfügbar"));
        } else {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava(detailedInfo));
        }
        System.out.println(objectUrl);
        System.out.println(poi.toString());
        return poi;
    }


    @Override
    public PointOfInterest getUrlById(PointOfInterest poi) throws ServiceNotAvailableException {
        GenericUrl url = createGenericUrl();
        url.put("ids", poi.getId());
        getDescription(url);
        if (this.detailedInfo.equals("")) {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava("Der Wikipedia Artikel ist leider nicht verfügbar"));
        } else {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava(detailedInfo));
        }
        System.out.println(objectUrl);
        System.out.println(poi.toString());
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

            for (Object element : elements) {
                String objectUrl = JsonPath.read(element, "$.result.detailedDescription.url").toString();
                if (!objectUrl.isEmpty()) {
                    this.objectUrl = objectUrl;
                    String[] temp = this.objectUrl.split("/");
                    System.out.println(Arrays.toString(temp));
                    String[] language = temp[2].split("\\.");
                    if (!language[0].equals("")) {
                        this.detailedInfo = WikiAbstractProvider.getExtract(getNameFromUrl(this.objectUrl), language[0]);
                    }

                    return this.detailedInfo;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getNameFromUrl(String objectUrl) {
        if (!objectUrl.equals("")) {
            String[] temp = objectUrl.split("/");
            return temp[temp.length - 1];
        }
        return "";
    }

}
