package org.eclipse.leshan.client.demo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

public class ParkingSpot extends BaseInstanceEnabler {
	private static final int PARKING_SPOT_ID = 32800;
	private static final int PARKING_SPOT_STATE = 32801;
	private static final int VEHICLE_ID = 32802;
	private static final int BILING_RATE = 32803;
	private static final int VEIHICLE_PLATE_IMAGE = 32804;
	
	private static final List<Integer> supportedResources = Arrays.asList(PARKING_SPOT_ID, PARKING_SPOT_STATE,
			BILING_RATE);
	
	private final String ParkingSpotID;
	private String ParkingSpotState;
	private String VehicleID;
	private float BillingRate;
	//private FILE image;
	
	//private RPiCamera piCamera;
	
	public ParkingSpot(String endpoint) {
		ParkingSpotID = endpoint ;
		ParkingSpotState = "free";
		VehicleID = "__-___-_";
		BillingRate = 0.01f;
		detectChange();
		
	}
	
	private void detectChange() {
		System.out.println("detectChange");
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
			
		
		  @Override
		  public void run() {
			  String newPlate = "__-___-_";
			  
			  newPlate = Camera.getPlate();
			  
			  if(newPlate.equalsIgnoreCase(VehicleID)) {

				  System.out.println("Same State");
			  }
			  else {
				  System.out.println("Found Change");
				  System.out.println("New Plate: " + newPlate);
				  if(newPlate.equalsIgnoreCase("__-___-_")) {
					  if(ParkingSpotState.equalsIgnoreCase("reserved")) {
						  
						  System.out.println("Waiting for car to arrive to reserve spot");
						  
					  }
					  else if(ParkingSpotState.equalsIgnoreCase("occupied")) {
						  ParkingSpotState = "free";
						  System.out.println("Car left");
						  VehicleID = newPlate;
						  fireResourcesChange(32801);
					  }
				  }
				  
				  else {
					  if(ParkingSpotState.equalsIgnoreCase("free")) {
						  ParkingSpotState = "occupied";
						  System.out.println("Car detected");
					  }
					  else if(ParkingSpotState.equalsIgnoreCase("reserved")) {
						  ParkingSpotState = "occupied";
						  if(newPlate.equalsIgnoreCase(VehicleID)) {
							  System.out.println("Car park in reserved Spot");
						  }
						  else {
							  System.out.println("Car parked didn't reserve this spot");
						  }
					  }
					  else if(ParkingSpotState.equalsIgnoreCase("occupied")) {
						  ParkingSpotState = "free";
						  System.out.println("Car left");
					  }
					  VehicleID = newPlate;
					  fireResourcesChange(32801);
				  }
			  }
		  }
		}, 0, 1, TimeUnit.SECONDS);
		
	}
	
	@Override
	public ReadResponse read(int resourceId) {
		System.out.println("Device : read on resource " + resourceId);
		
		switch (resourceId) {
		case PARKING_SPOT_ID:
			return ReadResponse.success(resourceId, ParkingSpotID);
		
		case PARKING_SPOT_STATE:
			return ReadResponse.success(resourceId, ParkingSpotState);
		
		case VEHICLE_ID:
			return ReadResponse.success(resourceId, VehicleID);
		
		case BILING_RATE:
			return ReadResponse.success(resourceId, BillingRate);
			
//		case VEIHICLE_PLATE_IMAGE:
//			return ReadResonse.success(resourceId, image);
		default:
			return super.read(resourceId);
		}
	}
	
	@Override
	public WriteResponse write(int resourceId, LwM2mResource value) {
		System.out.println("Device : write on resource " + resourceId);
		
		switch (resourceId) {
		case PARKING_SPOT_STATE:
			ParkingSpotState = (String) value.getValue();
			fireResourcesChange(resourceId);
			return WriteResponse.success();
		
		case VEHICLE_ID:
			VehicleID = (String) value.getValue();
			fireResourcesChange(resourceId);
			return WriteResponse.success();
		
		case BILING_RATE:
			BillingRate = (float) value.getValue();
			fireResourcesChange(resourceId);
			return WriteResponse.success();
			
		case VEIHICLE_PLATE_IMAGE:
			ParkingSpotState = (String) value.getValue();
			
			return WriteResponse.success();
		default:
			return super.write(resourceId, value);
		}
	}
	
	@Override
	public synchronized ExecuteResponse execute(int resourceId, String params) {
		System.out.println("Device : exec on resource " + resourceId);
		
		switch (resourceId) {
		
		default:
			return super.execute(resourceId, params);
		}
	}
	
    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }
}
