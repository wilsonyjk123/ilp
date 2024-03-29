package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DroneMap {

    public ArrayList<Feature> lfLandmarks = new ArrayList<>();
    public ArrayList<Point> confinementArea = new ArrayList<>();
    public ArrayList<LongLat> landmarks = new ArrayList<>();
    public ArrayList<Line2D> line2DArrayListNoFlyZone = new ArrayList<>();
    public ArrayList<Line2D> line2DArrayListConfinementArea = new ArrayList<>();
    public String webPort;

    // Class Constructor
    public DroneMap(String webPort){
        this.webPort = webPort;
    }

    /**
     * Get the URL string for accessing the no-fly zone on website
     *
     * @return URL string for accessing the no-fly zone on website
     * */
    public String getURLStringForNoFlyZones(){ return "http://localhost:" + webPort + "/buildings/no-fly-zones.geojson"; }

    /**
     * Get the URL string for accessing the landmark on website
     *
     * @return URL string for accessing the landmark on website
     * */
    public String getURLStringForLandmarks(){
        return "http://localhost:" + webPort + "/buildings/landmarks.geojson";
    }

    /**
     * Get the confinement area from the defined final local variables and set them into lines in type-Line2D
     *
     * @return a list of type-Line2D object that contains the confinement area
     * */
    public ArrayList<Line2D> getConfinementArea(){
        Point pointFH = Point.fromLngLat(getFHLong(),getFHLat());
        Point pointKFC = Point.fromLngLat(getKFCLong(),getKFCLat());
        Point pointMeadows = Point.fromLngLat(getMeadowsLong(),getMeadowsLat());
        Point pointBuccleuch = Point.fromLngLat(getBuccleuchLong(),getBuccleuchLat());
        confinementArea = new ArrayList<>();
        confinementArea.add(pointFH);
        confinementArea.add(pointKFC);
        confinementArea.add(pointMeadows);
        confinementArea.add(pointBuccleuch);
        ArrayList<Point2D> point2DS = new ArrayList<>();
        for (Point point:confinementArea){
            Point2D point2D = new Point2D.Double();
            point2D.setLocation(point.coordinates().get(0),point.coordinates().get(1));
            point2DS.add(point2D);
        }
        for (int i = 0;i<point2DS.size();i++){
            Line2D line2D = new Line2D.Double();
            if (i == point2DS.size()-1){
                line2D.setLine(point2DS.get(i),point2DS.get(0));
            }else {
                line2D.setLine(point2DS.get(i),point2DS.get(i+1));
            }
            line2DArrayListConfinementArea.add(line2D);
        }
        return line2DArrayListConfinementArea;
    }

    /**
     * Get the landmarks from the website and set them into LongLat objects
     *
     * @return a list of LongLat object that contains the landmarks
     * */
    public ArrayList<LongLat> getLandMarks(){
        WebConn webConn = new WebConn(webPort);
        HttpResponse<String> response = webConn.createResponse(webConn.createRequest(getURLStringForLandmarks()));
        FeatureCollection fc = FeatureCollection.fromJson(response.body());
        lfLandmarks = (ArrayList<Feature>) fc.features();
        assert lfLandmarks != null;
        for(Feature feature: lfLandmarks) {
            Point point = (Point) feature.geometry();
            assert point != null;
            double lng = point.coordinates().get(0);
            double lat = point.coordinates().get(1);
            LongLat landmark = new LongLat(lng, lat);
            landmarks.add(landmark);
        }
        return landmarks;
    }

    /**
     * Get the no-fly zone from the website and set them into lines in type-Line2D
     *
     * @return a list of type-Line2D object that contains the no-fly zone
     * */
    public ArrayList<Line2D> getNoFlyZone(){
        WebConn webConn = new WebConn(webPort);
        HttpResponse<String> response = webConn.createResponse(webConn.createRequest(getURLStringForNoFlyZones()));
        FeatureCollection featureCollection = FeatureCollection.fromJson(response.body());
        List<Feature> features = featureCollection.features();
        assert features != null;
        for(Feature feature:features){
            Polygon polygon = (Polygon)feature.geometry();
            assert polygon != null;
            for(List<Point> listPoint:polygon.coordinates()){
                ArrayList<Point2D> point2DS = new ArrayList<>();
                for (Point point:listPoint){
                    Point2D point2D = new Point2D.Double();
                    point2D.setLocation(point.coordinates().get(0),point.coordinates().get(1));
                    point2DS.add(point2D);
                }
                for (int i = 0;i<point2DS.size();i++){
                    Line2D line2D = new Line2D.Double();
                    if (i == point2DS.size()-1){
                        line2D.setLine(point2DS.get(i),point2DS.get(0));
                    }else {
                        line2D.setLine(point2DS.get(i),point2DS.get(i+1));
                    }
                    line2DArrayListNoFlyZone.add(line2D);
                }
            }
        }
        return line2DArrayListNoFlyZone;
    }

    /**
     * Write a text file in GeoJSON format
     *
     * @param pl - A list of point that represents all the points on the map that drone arrives
     * @param day - the day of this order
     * @param month - the month of this order
     * @param year - the year of this order
     * */
    public void printRoute(ArrayList<Point> pl, String day,String month,String year){
        LineString lineString = LineString.fromLngLats(pl);
        Feature feature = Feature.fromGeometry(lineString);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String flightPath = featureCollection.toJson();
        try {
            FileWriter fileWriter = new FileWriter("drone-" + day + "-" + month + "-" + year + ".geojson");
            fileWriter.write(flightPath);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    // Methods
    /**
     * set the Appleton Tower's LongLat position
     *
     * @return Appleton Tower's LongLat position
     * */
    public LongLat setAPT(){
        return new LongLat(getATLong(),getATLat());
    }

    /*Getters*/
    public double getATLong(){ return -3.186874; }

    public double getATLat(){ return 55.944494; }

    public double getFHLong(){ return -3.192473; }

    public double getFHLat(){ return 55.946233; }

    public double getKFCLong(){ return -3.184319; }

    public double getKFCLat(){ return 55.946233; }

    public double getMeadowsLong(){ return -3.192473; }

    public double getMeadowsLat(){ return 55.942617; }

    public double getBuccleuchLong(){ return -3.184319; }

    public double getBuccleuchLat(){ return 55.942617; }
}
