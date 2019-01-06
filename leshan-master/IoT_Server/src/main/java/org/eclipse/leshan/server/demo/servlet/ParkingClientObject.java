package org.eclipse.leshan.server.demo.servlet;

import java.util.ArrayList;
import java.util.List;

public class ParkingClientObject {
    private String endPoint;
    private String latitude;
    private String longitude;
    private List<ParkingSpotObject> ParkingSpotList = new ArrayList<ParkingSpotObject>();

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
    	// Put thing in the DataBase
        this.endPoint = endPoint;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
    	// Put thing in the DataBase
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
    	// Put thing in the DataBase
        this.longitude = longitude;
    }

    public List<ParkingSpotObject> getParkingSpotList() {
        return ParkingSpotList;
    }

    public void setParkingSpotList(List<ParkingSpotObject> spotList) {
    	// Put thing in the DataBase
        this.ParkingSpotList = spotList;
    }
}
