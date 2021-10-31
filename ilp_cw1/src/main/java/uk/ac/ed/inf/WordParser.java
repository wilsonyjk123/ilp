package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class WordParser {
    String webPort;
    String jsonText;
    WordParser(String webPort){
        this.webPort = webPort;
    }
    Word word = null;

    public static class Word {
        String country;
        Square square;
        public static class Square {
            Southwest southwest;
            public static class Southwest {
                double lng;
                double lat;
            }
            Northeast northeast;
            public static class Northeast {
                double lng;
                double lat;
            }
        }
        String nearestPlace;
        Coordinates coordinates;

        public static class Coordinates {
            double lng;
            double lat;
        }

        String language;
        String map;
    }
    public Word parseWord(String threeWord){
        WebConn webConn = new WebConn(webPort);
        try{
            HttpResponse<String> response = webConn.createResponse(webConn.createRequest(webConn.getURLStringForThreeWordsLocation(threeWord)));
            if(response.statusCode() == 200){
                this.jsonText = response.body();
            }else{
                System.out.println("Connection Failed with Response Code: " + response.statusCode());
                System.exit(1);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        word = new Gson().fromJson(jsonText, Word.class);
        return word;
    }
}
