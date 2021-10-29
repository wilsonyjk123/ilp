package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MenuParser {
    String jsonText;
    String webPort;
    MenuParser(String webPort){
        this.webPort = webPort;
    }
    ArrayList<Menu> menuList = null;
    public static class Menu {
        String name;
        String location;
        List<Item> menu;
        public static class Item {
            String item;
            int pence;
        }
    }

    public ArrayList<Menu> parseMenus(){
        WebConn webConn = new WebConn(webPort);
        try{
            HttpResponse<String> response = webConn.createResponse(webConn.createRequest(webConn.getURLStringForMenus()));
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
        Type listType = new TypeToken<ArrayList<Menu>>(){}.getType();
        menuList = new Gson().fromJson(this.jsonText, listType);
        return menuList;
    }
}