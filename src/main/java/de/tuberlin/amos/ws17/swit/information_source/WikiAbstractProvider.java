package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WikiAbstractProvider implements AbstractProvider {
    /**
     * Provides an abstract for a POI
     *
     * @param poi PointOfInterest object, already contains either a name of a poi or its poi
     */
    @Override
    public void provideAbstract(PointOfInterest poi) {

        String poiName = poi.getName();
        poiName = poiName.replaceAll(" ", "_");
        String wikiAbstract = "";

        int wikiApiID = searchArticles(poiName);
        if(wikiApiID == -1) {
            System.out.println("no pages found");
        } else {
            wikiAbstract = getExtract(wikiApiID);
        }

        poi.setInformationAbstract(StringEscapeUtils.unescapeJava(wikiAbstract));
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
    private static String getExtract(int articleID) {
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

}
