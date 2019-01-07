package org.eclipse.leshan.server.demo.servlet;

public class ParkingSpotObject {
    private String parkingSpotId;
    private String parkingSpotState;
    private String vehicleId;
    private float bilingRate;

    public String getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(String parkingSpotId) {
    	// Put thing in the DataBase
        this.parkingSpotId = parkingSpotId;
    }
    
    public float getBilingRate() {
        return bilingRate;
    }

    public void setBilingRate(float newBilingRate) {
    	// Put thing in the DataBase
        this.bilingRate = newBilingRate;
    }

    public String getParkingSpotState() {
        return parkingSpotState;
    }

    public void setParkingSpotState(String parkingSpotState) {
    	// Put thing in the DataBase
        this.parkingSpotState = parkingSpotState;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
    	// Put thing in the DataBase
        this.vehicleId = vehicleId;
    }

    public ParkingSpotObject(String parkingSpotId, String parkingSpotState, String vehicleId, float newBilingRate) {
    	// Put thing in the DataBase
        this.parkingSpotId = parkingSpotId;
        this.parkingSpotState = parkingSpotState;
        this.vehicleId = vehicleId;
        this.bilingRate = newBilingRate;
    }
}
