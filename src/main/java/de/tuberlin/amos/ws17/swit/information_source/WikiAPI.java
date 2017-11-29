package de.tuberlin.amos.ws17.swit.information_source;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WikiAPI {

    public static String language;

    public static void main(String[] args) {
        language = "en";
        String searchWord = "";
        if(args.length == 1) {
        	searchWord = args[0];
        } else {
        	System.out.println("Please enter the word of which you want the information of to be retrieved!");
        	Scanner scanner = new Scanner(System.in);
        	searchWord = scanner.nextLine();
        	scanner.close();
		}

        searchWord = searchWord.replaceAll(" ", "_");

        int rqt = searchArticles(searchWord);
        if(rqt == -1) {
            System.out.println("no pages found");
        } else {
            String test = getExtract(rqt);
            System.out.println(test);
            System.out.println(StringEscapeUtils.unescapeJava(test));
        }
    }

    /*
     * Getter function to retrieve the article information.
     */
    public static String getArticle(String articleName) {
		String article = "";
        language = "en";
		articleName = articleName.replaceAll(" ", "_");

		int articleID = searchArticles(articleName);
        if(articleID == -1) {
            System.out.println("no pages found");
        } else {
            article = (getExtract(articleID));
        }
    	return article;
	}

    /*
     * Searches for the Wikipedia article ID and returns -1 if no page was found.
     */
    private static int searchArticles(String searchTerm) {
    	String json = "";
    	int result = -1;
        try {
            json = readHTTP("https://" + language + ".wikipedia.org/w/api.php?action=query&format=json&titles=" + searchTerm);
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
            String json = readHTTP("https://" + language + ".wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&pageids=" + articleID);
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
     * Retrieves the infobox content of a Wikipedia article with the given search term. Git hub is so complicated. but now it works .git
     * TODO: requires a complex parser (maybe DBPedia is better?)
     */
    private static String getInfoBox(String searchTerm) {
    	String json = "";
    	try {
    		json = readHTTP("https://guarded-ridge-13729.herokuapp.com/" + searchTerm);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println(json);
		return json;
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
