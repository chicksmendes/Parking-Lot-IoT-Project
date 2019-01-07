package org.eclipse.leshan.server.demo.servlet;

import java.util.ArrayList;
import java.util.List;

public class ParkObject {
    private String endPoint;
    private float latitude;
    private float longitude;
    private List<ParkingSpotObject> ParkingSpotList = new ArrayList<ParkingSpotObject>();

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
    	// Put thing in the DataBase
        this.endPoint = endPoint;
    }
    
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
    	// Put thing in the DataBase
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
    	// Put thing in the DataBase
        this.longitude = longitude;
    }

    public List<ParkingSpotObject> getParkingSpotList() {
        return ParkingSpotList;
    }
    
    public void addParkingSpot(ParkingSpotObject ps) {
        this.ParkingSpotList.add(ps);
    }

    public void setParkingSpotList(List<ParkingSpotObject> spotList) {
    	// Put thing in the DataBase
        this.ParkingSpotList = spotList;
    }
}
