package com.example.mcardle_daniel_s2375827;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;

public class Situation {
    private String comment;
    private String type;
    private String sourceURL;
    private String fromLatitude;
    private String fromLongitude;
    private String toLatitude;
    private String toLongitude;
    private String direction;
    private String delay;
    private String startTime;
    private String[] impact;
    private ArrayList<String> toLocations = new ArrayList<String>();
    private ArrayList<String> fromLocations = new ArrayList<String>();
    private double distance;


    public Situation(){

    }

    public String getComment() { return comment; }
    public String getType() { return type; }
    public String getSourceURL() { return sourceURL; }
    public String getFromLatitude() { return fromLatitude; }
    public String getFromLongitude() { return fromLongitude; }
    public String getToLatitude() { return toLatitude; }
    public String getToLongitude() { return toLongitude; }
    public String getDirection() { return direction; }
    public String getDelay() { return delay; }
    public String getStartTime() { return startTime; }
    public String[] getImpact() { return impact; }
    public ArrayList<String> getToLocations() { return toLocations; }
    public ArrayList<String> getFromLocations() { return fromLocations; }
    public double getDistance() { return distance; }

    public void setComment(String comment){
        this.comment = comment;
        Log.d("Situation",String.format("Comment set: %s",this.comment));
    }

    public void setType(String type){
        this.type = type;
        Log.d("Type",String.format("Type set: %s",this.type));
    }

    public void setSourceURL(String sourceURL){
        this.sourceURL = sourceURL;
        Log.d("sourceURL",String.format("sourceURL set: %s",this.sourceURL));
    }

    public void setFromLatitude(String fromLatitude) {
        this.fromLatitude = fromLatitude;
        Log.d("From latitude",String.format("fromLatitude set: %s",this.fromLatitude));
    }

    public void setFromLongitude(String fromLongitude) {
        this.fromLongitude = fromLongitude;
        Log.d("From Longitude",String.format("fromLongitude set: %s",this.fromLongitude));
    }

    public void setToLatitude(String toLatitude) {
        this.toLatitude = toLatitude;
        Log.d("To latitude",String.format("toLatitude set: %s",this.toLatitude));
    }

    public void setToLongitude(String toLongitude) {
        this.toLongitude = toLongitude;
        Log.d("To Longitude",String.format("toLongitude set: %s",this.toLongitude));
    }

    public void setDirection(String direction) {
        this.direction = direction;
        Log.d("Direction",String.format("Direction set: %s",this.direction));
    }

    public void setDelay(String delay) {
        this.delay = delay;
        Log.d("Delay",String.format("Delay set: %s", this.delay));
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        Log.d("Start time",String.format("startTime set: %s", this.startTime));
    }

    public void setImpact(String[] impact) {
        this.impact = impact;
        Log.d("Impact type",String.format("Impact type set: %s", this.impact[0]));
        Log.d("Impact value",String.format("Impact type set: %s", this.impact[1]));
    }

    public void setToLocations(String location) {
        this.toLocations.add(location);
        Log.d("This to location",String.format("to location added: %s", this.toLocations));
    }
    public void setFromLocations(String location) {
        this.fromLocations.add(location);
        Log.d("This from location",String.format("from location added: %s", this.fromLocations));
    }
    public void setDistance(double distance){
        this.distance = distance;
        Log.d("distance",String.format("distance set: %s", this.distance));
    }

    @Override
    public String toString(){
        return "Situation{" +
                "generalPublicComment = " + comment +
                "\ntype = " + type +
                "\nsourceURL = " + sourceURL +
                "\nfromLatitude = " + fromLatitude +
                "\nfromLongitude = " + fromLongitude +
                "\ntoLatitude = " + toLatitude +
                "\ntoLongitude = " + toLongitude +
                "\nfromLocations = " + fromLocations +
                "\ntoLocations = " + toLocations +
                "\ndirection = " + direction +
                "\ndelay = " + delay +
                "\nstartTime = " + startTime +
                "\nimpact = " + Arrays.toString(impact) +
                "\ndistance = " + distance +
                "}";
    }
}
