package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Map {
    // private fields
    private final double ATLong = -3.186874;
    private final double ATLat = 55.944494;
    private final double FHLong = -3.192473;
    private final double FHLat = 55.946233;
    private final double KFCLong = -3.184319;
    private final double KFCLat = 55.946233;
    private final double MeadowsLong = -3.192473;
    private final double MeadowsLat = 55.942617;
    private final double BuccleuchLong = -3.184319;
    private final double BuccleuchLat = 55.942617;
    private ArrayList<Feature> lfLandmarks = new ArrayList<>();
    private ArrayList<Feature> lfNoFlyZone = new ArrayList<>();
    private String webPort;

    // Constructor
    Map(String webPort){
        this.webPort = webPort;
    }

    // Methods
    public String getURLStringForNoFlyZones(){ return "http://localhost:" + webPort + "/buildings/no-fly-zones.geojson"; }

    public String getURLStringForLandmarks(){
        return "http://localhost:" + webPort + "/buildings/landmarks.geojson";
    }

    public void getLandMarks(){
        WebConn webConn = new WebConn(webPort);
        HttpResponse<String> response = webConn.createResponse(webConn.createRequest(getURLStringForLandmarks()));
        FeatureCollection fc = FeatureCollection.fromJson(response.body());
        lfLandmarks = (ArrayList<Feature>) fc.features();
    }

    public void getNoFlyZone(){
        WebConn webConn = new WebConn(webPort);
        HttpResponse<String> response = webConn.createResponse(webConn.createRequest(getURLStringForNoFlyZones()));
        FeatureCollection fc = FeatureCollection.fromJson(response.body());
        lfNoFlyZone = (ArrayList<Feature>) fc.features();
    }

}