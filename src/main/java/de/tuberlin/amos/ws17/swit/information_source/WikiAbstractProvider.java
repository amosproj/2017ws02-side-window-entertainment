package de.tuberlin.amos.ws17.swit.information_source;


import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

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

public class WikiAbstractProvider implements InformationProvider, Module {
    /**
     * Provides an abstract for a POI
     *
     * @param poi PointOfInterest object, already contains either a name of a poi or its poi
     */
    InformationProvider knowledgeGraphSearch;

    /*@Override
    public PointOfInterest provideAbstract(PointOfInterest poi) {

        if (poi != null) {
            String poiName = poi.getName();
            poiName = poiName.replaceAll(" ", "_");
            String wikiAbstract = "";

            int wikiApiID = searchArticles(poiName);
            if(wikiApiID == -1) {
                wikiAbstract = "No Wikipedia article for the point of interest " + poiName + " was found.";
            } else {
                wikiAbstract = getAbstract(wikiApiID);
            }

            poi.setInformationAbstract(StringEscapeUtils.unescapeJava(wikiAbstract));

            return poi;
        } else {
            return null;
        }

    }*/

    @Override
    public PointOfInterest setInfoAndUrl(PointOfInterest poi) throws ServiceNotAvailableException{
        if (poi!= null) {
            try {
                knowledgeGraphSearch = KnowledgeGraphSearch.getInstance();
                poi = knowledgeGraphSearch.setInfoAndUrl(poi);
                String wikiUrl = poi.getWikiUrl();
                if (wikiUrl==null){
                    String abstractInfo= getAbstract(poi.getName(), "en");
                    if (abstractInfo == null) {
                        throw new ServiceNotAvailableException("Information is not available");
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
                        throw new ServiceNotAvailableException("Information is not available");
                    }
                }

            } catch (ServiceNotAvailableException ex) {
                ex.printStackTrace();
            }

            return poi;
        } else {
            throw new ServiceNotAvailableException("No POI could be find");
        }
    }


    /*
     * Searches for the Wikipedia article ID and returns -1 if no page was found.
     */
    private static int searchArticles(String searchTerm) {
        String json;
        int result = -1;
        try {
            json = readHTTP("https://en.wikipedia.org/w/api.php?action=query&format=json&titles=" + searchTerm);
            int idStartIndex = json.indexOf("pages\":{\"") + 9;
            json = json.substring(idStartIndex);
            int idEndIndex = json.indexOf("\"");
            json = json.substring(0, idEndIndex);
            result = Integer.parseInt(json);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * Retrieves the short information text for a Wikipedia article with the given ID and returns an empty string if the ID is invalid.
     */
    private static String getAbstract(int articleID) {
        String result = "";
        try {
            String json = readHTTP("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&pageids=" + articleID);
            int idStartIndex = json.indexOf("extract\":\"") + 10;
            result = json.substring(idStartIndex);
            int idEndIndex = result.indexOf("\"}");
            result = result.substring(0, idEndIndex);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
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
            path = this.getClass().getClassLoader().getResource("module_images/informatin_source.jpg").getPath();
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
        return "Information Source - WikiAbstractProvider";
    }
}
