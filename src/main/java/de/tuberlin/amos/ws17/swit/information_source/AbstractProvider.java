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
import de.tuberlin.amos.ws17.swit.common.exceptions.InformationNotAvailableException;
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

    // loads the Google API key via the ApiConfig class
    private static final String API_KEY = ApiConfig.getProperty("KnowledgeGraphSearch");
    private static final String LANGUAGE = "de";

    private  static  AbstractProvider instance;

    public static AbstractProvider getInstance() throws ServiceNotAvailableException {
        if (instance == null) {
            instance = new AbstractProvider();
        }
        return instance;
    }


    /**
     *  Retrieves information of a point of interest with the help of the Google knowledge graph as
     *  the Wikipedia API. The information retrieved are an abstract of the POI as well as the URL to
     *  the Wikipedia article.
     *  It gets the knowledge graph abstract information first and then tries to
     *  retrieve an information abstract from Wikipedia.
     * @param poi Point of interest of which you want to get the information from
     * @return The point of interest now also containing an information abstract
     * @throws ServiceNotAvailableException Thrown, when the knowledge graph is not available.
     */
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

    /**
     * Retrieves the abstract information belonging to a POI from Wikipedia and adds that data to the
     * PointOfInterest object it was called with.
     * @param poi Point of interest of which you want to retrieve the abstract from.
     * @throws ServiceNotAvailableException Thrown, when the knowledge graph is not available.
     */
    private void getWikiInformation (PointOfInterest poi) throws ServiceNotAvailableException {
        if (poi != null) {
            try {
            String wikiUrl = poi.getWikiUrl();
                DebugLog.log("InformationSource","Fetching Wiki-Article...");
            if (wikiUrl==null){

                String abstractInfo= getAbstract(poi.getName(), poi.getLanguage());
                if (StringUtils.isEmpty(abstractInfo)) {
                    throw new InformationNotAvailableException("Information is not available on Wikipedia");
                } else {
                    poi.setInformationAbstract(abstractInfo);
                }
            }
            if (!StringUtils.isEmpty(wikiUrl)) {
                // if wiki url available -> query info from wikipedia
                String abstractInfo = getAbstract(wikiUrl);
                if (!StringUtils.isEmpty(abstractInfo)) {
                    poi.setInformationAbstract(abstractInfo);
                } else {
                    throw new InformationNotAvailableException("Information is not available on Wikipedia");
                }
            }

            } catch (ServiceNotAvailableException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new ServiceNotAvailableException("No POI could be found");
        }
    }

    /**
     * Retrieves the abstract of a Wikipedia article belonging to the given URL.
     * @param wikiUrl URL to a Wikipedia article
     * @return The abstract of the Wikipedia article
     */
    private static String getAbstract(String wikiUrl) {
        String wikiName = getNameFromUrl(wikiUrl);
        String wikiLanguage = getLanguageFromUrl(wikiUrl);
        return getAbstract(wikiName, wikiLanguage);
    }


    /**
     * Retrieves the abstract of a Wikipedia article of the given search term and in the given language.
     * @param searchTerm The term for a Wikipedia article.
     * @param language The language in which the abstract should be in.
     * @return The abstract of the Wikipedia article.
     */
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

    /**
     * Retrieves the name of a Wikipedia article from a given URL.
     * @param wikiUrl The URL to a Wikipedia article.
     * @return The name of the article to which to URL leads.
     */
    public static  String getNameFromUrl(String wikiUrl) {
        if (!wikiUrl.equals("")) {
            String[] temp = wikiUrl.split("/");
            return temp[temp.length - 1];
        }
        return "";
    }

    /**
     * Retrieves the language of a Wikipedia article from a given URL.
     * @param wikiUrl The URL to a Wikipedia article.
     * @return The language of the article to which to URL leads.
     */
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
    /**
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

    /**
     *  Method for accessing the Google knowledge Graph API
     * @return  an URL to call Knowledge Graph
     */
    private GenericUrl createGenericUrl() {
        GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
        url.put("limit", "10");
        url.put("indent", "true");
        url.put("key", API_KEY);
        url.set("languages", LANGUAGE);
        return url;
    }

    /**
     * query by Id
     * @param id
     * @return detailled Description and WikiUrl of the found object
     */
    @Nullable
    private AbstractProvider.Tuple<String, String> getInfoById(String id) {
        GenericUrl url = createGenericUrl();
        url.put("ids", id);
        return getInfoAndWikiUrl(url);
    }

    /**
     * query by name
     * @param name
     * @return detailled Description and WikiUrl of the found object
     */
    @Nullable
    private AbstractProvider.Tuple<String, String> getInfoByName(String name) {
        GenericUrl url = createGenericUrl();
        url.put("query", name);
        return getInfoAndWikiUrl(url);
    }


    /**
     *
     * @param url takes in knowledge Graph Url
     * @return  The detailed Description and Wiki URL of found object
     */
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
     * Beendet das Modul, sodass Threads geschlossen werden und die Funktionalit??t nichtmehr verf??gbar ist.
     *
     * @return
     */
    @Override
    public boolean stopModule() {
        return false;
    }

    /**
     * Falls das Modul nicht funktioniert, wird dieses Bild als Hinweis auf der Oberfl??che angezeigt.
     * Bilder, die hier aufgerufen werden, geh??ren in "/resources/module_images/"
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
     * Gibt den Namen des Moduls zur??ck.
     *
     * @return
     */
    @Override
    public String getModuleName() {
        return "Information Source - AbstractProvider";
    }
}
