import java.io.*;
import java.net.*;

public class WikiAPI {

    public static String language;
    public static Gson gson;

    public static void main(String[] args) {
        language = "en";
        gson = new Gson();
        ResultQueryTitles rqt = searchArticles("HTML");
        if(rqt.query.pages.ids.isEmpty()) {
            System.out.println("no pages found");
        } else {
            System.out.println(getExtract(rqt.query.pages.ids.get(0)));
        }
    }

    public static ResultQueryTitles searchArticles(String searchTerm) {
        try {
            String json = readHTTP("https://" + language + ".wikipedia.org/w/api.php?action=query&format=json&titles=" + searchTerm);
        } catch(Exception e) {
            e.printStackTrace();
        }
        ResultQueryTitles result = gson.fromJson(json, ResultQueryTitles.class);
        return result;
    }

    public static String getExtract(int atricleID) {
        String result = "";
        try {
            String json = readHTTP("https://" + language + ".wikipedia.org/w/api.php?action=query&prop=extracts&exintro&explaintext&format=json&pageids=" + articleID);            
        } catch(Exception e) {
            e.printStackTrace();
        }
        ResultQueryTitles result = gson.fromJson(json, ResultQueryTitles.class);
        return result.query.pages.ids.get(0).extract;
    }

    public static String readHTTP(String websiteURL) throws Exception {
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