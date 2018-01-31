package de.tuberlin.amos.ws17.swit.information_source;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class AbstractProvider implements InformationProvider, Module{

    private static final String API_KEY = ApiConfig.getProperty("KnowledgeGraphSearch");
    private static final String LANGUAGE = "de";

    private  static  AbstractProvider instance;

    public static AbstractProvider getInstance() throws ServiceNotAvailableException {
        if (instance == null) {
            instance = new AbstractProvider();
        }
        return instance;
    }

    public PointOfInterest setInfoAndUrl(PointOfInterest poi) throws ServiceNotAvailableException {
        if (poi != null) {
            AbstractProvider.Tuple<String, String> result = null;

            // try with id
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
            getWikiInformation(poi);
            return poi;
        } else {
            return null;
        }
    }


    private void getWikiInformation (PointOfInterest poi) throws ServiceNotAvailableException{
        if (poi != null) {
            try {
            String wikiUrl = poi.getWikiUrl();

            if (wikiUrl==null){
                DebugLog.log("No WikiUrl found. Proceeding to retrieve the abstract via the name of the POI");
                String abstractInfo= getAbstract(poi.getName(), LANGUAGE);
                if (abstractInfo == null) {
                    throw new ServiceNotAvailableException("Information is not available");
                } else {
                    DebugLog.log("Successfully retrieved the abstract via the POI name");
                    poi.setInformationAbstract(abstractInfo);
                }
            }
            if (!StringUtils.isEmpty(wikiUrl)) {
                DebugLog.log("Successfully retrieved WikiUrl of POI");
                // if wiki url available -> query info from wikipedia
                String abstractInfo = getAbstract(wikiUrl);
                if (!StringUtils.isEmpty(abstractInfo)) {
                    DebugLog.log("Successfully retrieved the abstract via the WikiUrl");
                    poi.setInformationAbstract(abstractInfo);
                } else {
                    throw new ServiceNotAvailableException("Information is not available");
                }
            }

            } catch (ServiceNotAvailableException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new ServiceNotAvailableException("No POI could be find");
        }
    }

    private static String getAbstract(String wikiUrl) {
        String wikiName = getNameFromUrl(wikiUrl);
        String wikiLanguage = getLanguageFromUrl(wikiUrl);
        return getAbstract(wikiName, wikiLanguage);
    }

    private static String getAbstract(String searchTerm, String language) {
        if (searchTerm == null) {
            return null;
        }
        String result = "";
        searchTerm=searchTerm.replaceAll(" ", "_");
        try {
            String json = readHTTP("https://"+ language + ".wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + searchTerm);
            int idStartIndex = json.indexOf("extract\":\"") + 10;
            result = json.substring(idStartIndex);
            int idEndIndex = result.indexOf("\"}");
            result = result.substring(0, idEndIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringEscapeUtils.unescapeJava(result);
    }



    public static  String getNameFromUrl(String wikiUrl) {
        if (!wikiUrl.equals("")) {
            String[] temp = wikiUrl.split("/");
            return temp[temp.length - 1];
        }
        return "";
    }

    @Nullable
    public static String getLanguageFromUrl(String wikiUrl) {
        String[] temp = wikiUrl.split("/");
        System.out.println(Arrays.toString(temp));
        String[] language = temp[2].split("\\.");
        if (!language[0].equals("")) {
            return language[0];
        }
        return null;
    }
    /*
     * Reads the HTTP informations of a given URL and returns it as a string.
     */
    private static String readHTTP(String websiteURL) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(websiteURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result.toString();
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
    private AbstractProvider.Tuple<String, String> getInfoById(String id) {
        GenericUrl url = createGenericUrl();
        url.put("ids", id);
        return getInfoAndWikiUrl(url);
    }

    @Nullable
    private AbstractProvider.Tuple<String, String> getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        return getInfoAndWikiUrl(url);
    }

    @Nullable
    private AbstractProvider.Tuple<String, String> getInfoAndWikiUrl(GenericUrl url) {
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
                return new AbstractProvider.Tuple<>(info, wikiUrl);
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


    class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Startet das Modul, sodass es Einsatzbereit ist. Voraussetzung ist die Initialisierung einer Instanz.
     *
     * @throws ModuleNotWorkingException
     */
    @Override
    public void startModule() throws ModuleNotWorkingException {

    }

    /**
     * Beendet das Modul, sodass Threads geschlossen werden und die Funktionalität nichtmehr verfügbar ist.
     *
     * @return
     */
    @Override
    public boolean stopModule() {
        return false;
    }

    /**
     * Falls das Modul nicht funktioniert, wird dieses Bild als Hinweis auf der Oberfläche angezeigt.
     * Bilder, die hier aufgerufen werden, gehören in "/resources/module_images/"
     *
     * @return
     */
    @Override
    public BufferedImage getModuleImage() {
        String path = "";
        try {
            this.getClass();
            this.getClass().getResource("");
            path = this.getClass().getClassLoader().getResource("module_images/information_source.jpg").getPath();
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(path);
        }
        return null;
    }

    /**
     * Gibt den Namen des Moduls zurück.
     *
     * @return
     */
    @Override
    public String getModuleName() {
        return "Information Source - AbstractProvider";
    }
}
