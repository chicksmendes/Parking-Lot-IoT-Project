/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Zebra Technologies - initial API and implementation
 *     Sierra Wireless, - initial API and implementation
 *     Bosch Software Innovations GmbH, - initial API and implementation
 *******************************************************************************/

package org.eclipse.leshan.client.demo;

import static org.eclipse.leshan.LwM2mId.DEVICE;
import static org.eclipse.leshan.LwM2mId.LOCATION;
import static org.eclipse.leshan.LwM2mId.SECURITY;
import static org.eclipse.leshan.LwM2mId.SERVER;
import static org.eclipse.leshan.client.object.Security.noSec;

import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.leshan.LwM2m;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParkingSpotClient {
	
	private final static String ENDPOINTNAME = "Parking-Spot-Group129";

    private static final Logger LOG = LoggerFactory.getLogger(ParkingSpotClient.class);

    private final static String[] modelPaths = new String[] { "32700.xml", "3341.xml",  "3345.xml" };

    private static final int OBJECT_ID_PARKING_SPOT = 32700;
    private static final int OBJECT_ID_DISPLAY = 3341;
    private static final int OBJECT_ID_JOYSTICK = 3345;
    
    private final static String USAGE = "java -jar leshan-client-demo.jar [OPTION]\n\n";

    private static MyLocation locationInstance;

    public static void main(final String[] args) {
    	
    	Random r = new Random();
    	
    	// Gnerates random letter to test multiple clients
        String alphabet = "abcdefghijklmn";
        
        char x = alphabet.charAt(r.nextInt(alphabet.length()));
        
    	String endpoint = ENDPOINTNAME + "-" + x ;
    	
    	LOG.info("Endpoint name: " + endpoint);
    	
        // Define options for command line tools
        Options options = new Options();

        
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(90);
        formatter.setOptionComparator(null);

        // Parse arguments
        CommandLine cl;
        try {
            cl = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            formatter.printHelp(USAGE, options);
            return;
        }
        

        // get local address
        String localAddress = null;
		
		while(localAddress == null) {
	        try {
				localAddress = Discovery.discoverService();
				if(localAddress == null) {
		        	LOG.info("Can't find a server, trying again...");
		        }
				localAddress="192.168.0.105";
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		
		System.out.println(localAddress);
		
		// Get server URI
        String serverURI = "coap://" + localAddress + ":" + LwM2m.DEFAULT_COAP_PORT;
        
        
        int localPort = 0;
      
        
        Float latitude = null;
        Float longitude = null;
        Float scaleFactor = 1.0f;
        // get initial Location
        if (cl.hasOption("pos")) {
            try {
                String pos = cl.getOptionValue("pos");
                int colon = pos.indexOf(':');
                if (colon == -1 || colon == 0 || colon == pos.length() - 1) {
                    System.err.println("Position must be a set of two floats separated by a colon, e.g. 48.131:11.459");
                    formatter.printHelp(USAGE, options);
                    return;
                }
                latitude = Float.valueOf(pos.substring(0, colon));
                longitude = Float.valueOf(pos.substring(colon + 1));
            } catch (NumberFormatException e) {
                System.err.println("Position must be a set of two floats separated by a colon, e.g. 48.131:11.459");
                formatter.printHelp(USAGE, options);
                return;
            }
        }
        if (cl.hasOption("sf")) {
            try {
                scaleFactor = Float.valueOf(cl.getOptionValue("sf"));
            } catch (NumberFormatException e) {
                System.err.println("Scale factor must be a float, e.g. 1.0 or 0.01");
                formatter.printHelp(USAGE, options);
                return;
            }
        }
        
        Camera camera = new Camera();
        
        camera.takePicContinuosly();
        
        LOG.info("Start taking pictures");
        
        
        try {
            createAndStartClient(endpoint, localAddress, localPort,  serverURI, latitude, longitude, scaleFactor);
        } catch (Exception e) {
            System.err.println("Unable to create and start client ...");
            e.printStackTrace();
            return;
        }
    }

    public static void createAndStartClient(String endpoint, String localAddress, int localPort, String serverURI, 
            Float latitude, Float longitude, float scaleFactor) throws CertificateEncodingException {

        locationInstance = new MyLocation(latitude, longitude, scaleFactor);

        // Initialize model
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models", modelPaths));

        // Initialize object list
        ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(models));

    
        initializer.setInstancesForObject(SECURITY, noSec(serverURI, 123));
        initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            
        
        initializer.setClassForObject(DEVICE, MyDevice.class);
        initializer.setInstancesForObject(LOCATION, locationInstance);
        initializer.setInstancesForObject(OBJECT_ID_PARKING_SPOT, new ParkingSpot(endpoint));
        initializer.setInstancesForObject(OBJECT_ID_DISPLAY, new Display());
        initializer.setInstancesForObject(OBJECT_ID_JOYSTICK, new Joystick());
        List<LwM2mObjectEnabler> enablers = initializer.create(SECURITY, SERVER, DEVICE, LOCATION,
                OBJECT_ID_PARKING_SPOT, OBJECT_ID_DISPLAY, OBJECT_ID_JOYSTICK);

        // Create CoAP Config
        NetworkConfig coapConfig;
        File configFile = new File(NetworkConfig.DEFAULT_FILE_NAME);
        if (configFile.isFile()) {
            coapConfig = new NetworkConfig();
            coapConfig.load(configFile);
        } else {
            coapConfig = LeshanClientBuilder.createDefaultNetworkConfig();
            coapConfig.store(configFile);
        }

        // Create client
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        //builder.setLocalAddress(localAddress, localPort);
        builder.setObjects(enablers);
        //builder.setCoapConfig(coapConfig);
        final LeshanClient client = builder.build();

        // Start the client
        client.start();

        // De-register on shutdown and stop client.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                client.destroy(true); // send de-registration request before destroy
            }
        });

        // Change the location through the Console
        LOG.info("Press 'w','a','s','d' to change reported Location ({},{}).", locationInstance.getLatitude(),
                locationInstance.getLongitude());
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                String nextMove = scanner.next();
                locationInstance.moveLocation(nextMove);
            }
        }
    }
}
