package hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class HttpRequest {

    public static String HttpRequestMethod(String id) throws IOException {
        URL url = new URL("https://openlibrary.org/works/"+id+".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        String response = content.toString();

        JSONObject jsonResponse = new JSONObject(response);
        JSONObject description;
        try{
            description = new JSONObject(jsonResponse.getJSONObject("description").toString());
        }
        catch (Exception e){
            String desc = jsonResponse.getString("description").toString().toUpperCase();
            return desc;
        }
        String value = description.getString("value").toString().toUpperCase();
        return value;
    }
}



