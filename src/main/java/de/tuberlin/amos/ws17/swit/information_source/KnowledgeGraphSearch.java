    package de.tuberlin.amos.ws17.swit.information_source;

    import com.google.api.client.http.*;
    import com.google.api.client.http.javanet.NetHttpTransport;
    import com.jayway.jsonpath.JsonPath;
    import de.tuberlin.amos.ws17.swit.common.ApiConfig;
    import javafx.beans.property.ObjectProperty;
    import org.apache.commons.lang3.StringEscapeUtils;
    import org.apache.jena.base.Sys;
    import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;
    import org.json.simple.parser.JSONParser;


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
        private String detailledInfo="";
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
        public PointOfInterest getInfoByName(PointOfInterest poi) throws ServiceNotAvailableException {
            GenericUrl url = createGenericUrl();
            url.put("query", poi.getName());
            getDescription(url);
            if (this.detailledInfo.equals("")) {
                poi.setInformationAbstract(StringEscapeUtils.unescapeJava("Der Wikipedia Artikel ist leider nicht verfügbar"));
            } else {
                poi.setInformationAbstract(StringEscapeUtils.unescapeJava(detailledInfo));
            }
            System.out.println(ObjectUrl);
            System.out.println(poi.toString());
            return poi;
        }


    @Override
    public PointOfInterest getUrlById(PointOfInterest poi) throws ServiceNotAvailableException {
        GenericUrl url = createGenericUrl();
        url.put("ids", poi.getId());
        getDescription(url);
        if (this.detailledInfo.equals("")) {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava("Der Wikipedia Artikel ist leider nicht verfügbar"));
        } else {
            poi.setInformationAbstract(StringEscapeUtils.unescapeJava(detailledInfo));
        }
        System.out.println(ObjectUrl);
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

                for (Object element: elements) {
                    String detailedDescription = "";
                    String Objecturl = JsonPath.read(element, "$.result.detailedDescription.url").toString();
                    if (!Objecturl.isEmpty()) {
                        this.ObjectUrl = Objecturl;
                        String[] temp = ObjectUrl.split("/");
                        System.out.println(temp);
                        String[] language = temp[2].split("\\.");
                        if(!language[0].equals("")){
                            this.detailledInfo = WikiAbstractProvider.getExtract(getNameFromUrl(ObjectUrl), language[0]);
                        }

                        return this.detailledInfo;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        public String getNameFromUrl(String objectUrl){
            if(!objectUrl.equals("")) {
                String[] temp =  objectUrl.split("/");
                return temp[temp.length - 1];
            }
            return "";
        }

    }
