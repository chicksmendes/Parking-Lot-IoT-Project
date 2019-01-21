package org.eclipse.leshan.client.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

public class Camera {
	
	RPiCamera piCamera;
	public void takePic() {
		
		try {
			piCamera = new RPiCamera("./");
		} catch (FailedToRunRaspistillException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//piCamera.takeStill("plateImage.jpg");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Takes a photo every second
	public  void takePicContinuosly() {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  @Override
		  public void run() {
			  takePic();
			  System.out.println("New Pic");
		  }
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	public float getPlateConfidence() {
		ProcessBuilder process_alpr = new ProcessBuilder("alpr", "-c eu", "plateImage.jpg") ;
		process_alpr.redirectErrorStream(true);
		Process process = null;
		try {
			process = process_alpr.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String confidenceStr = line.substring(line.indexOf(":") + 2, line.length());
		System.out.println(confidenceStr);
		
		float confidence = Float.parseFloat(confidenceStr);
		
		return confidence;
	}
	
	public boolean plateMatch(String plateToMatch) {
		
		
		ProcessBuilder process_alpr = new ProcessBuilder("alpr", "-c eu", "plateImage.jpg") ;
		process_alpr.redirectErrorStream(true);
		Process process = null;
		try {
			process = process_alpr.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String plate = line.substring(line.indexOf("-") + 2, line.indexOf("c") - 1);
		System.out.println(plate);
		
		if(plateToMatch.equalsIgnoreCase(plate) == true) {
			System.out.println("Plate Matches");
			return true;
		}
		
		return false;
	}

	public static String getPlate() {
		
		
		ProcessBuilder process_alpr = new ProcessBuilder("alpr", "-c eu", "plateImage.jpg") ;
		process_alpr.redirectErrorStream(true);
		Process process = null;
		try {
			process = process_alpr.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			line = reader.readLine();
			System.out.println(line);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		if(line != null) {	
			String plate = line.substring(line.indexOf("-") + 2, line.indexOf("c") - 1);
			
			System.out.println("New plate" + plate);
			return plate;
		}
		else {
			System.out.println("Plate not found");
		}
		return "__-___-_";
	}


}
