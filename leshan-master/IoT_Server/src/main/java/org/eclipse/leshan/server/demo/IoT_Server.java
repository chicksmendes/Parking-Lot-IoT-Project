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
 *     Sierra Wireless - initial API and implementation
 *     Bosch Software Innovations - added Redis URL support with authentication
 *     Firis SA - added mDNS services registering 
 *******************************************************************************/
package org.eclipse.leshan.server.demo;

import java.io.File;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.jmdns.JmDNS;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig.Builder;
import org.eclipse.californium.scandium.dtls.CertificateMessage;
import org.eclipse.californium.scandium.dtls.DTLSSession;
import org.eclipse.californium.scandium.dtls.HandshakeException;
import org.eclipse.californium.scandium.dtls.x509.CertificateVerifier;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.leshan.LwM2m;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mObject;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.codec.CodecException;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeDecoder;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeEncoder;
import org.eclipse.leshan.core.node.codec.LwM2mNodeDecoder;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.exception.ClientSleepingException;
import org.eclipse.leshan.core.request.exception.InvalidResponseException;
import org.eclipse.leshan.core.request.exception.RequestCanceledException;
import org.eclipse.leshan.core.request.exception.RequestRejectedException;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.cluster.RedisRegistrationStore;
import org.eclipse.leshan.server.cluster.RedisSecurityStore;
import org.eclipse.leshan.server.demo.servlet.ClientReservationServlet;
import org.eclipse.leshan.server.demo.servlet.ClientServlet;
import org.eclipse.leshan.server.demo.servlet.EventServlet;
import org.eclipse.leshan.server.demo.servlet.ObjectSpecServlet;
import org.eclipse.leshan.server.demo.servlet.SecurityServlet;
import org.eclipse.leshan.server.impl.FileSecurityStore;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

public class IoT_Server {
	
    private static final long TIMEOUT = 5000; // ms
    
    private static final String FORMAT_PARAM = "format";

    static {
        // Define a default logback.configurationFile
        String property = System.getProperty("logback.configurationFile");
        if (property == null) {
            System.setProperty("logback.configurationFile", "logback-config.xml");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(IoT_Server.class);

    private final static String[] modelPaths = new String[] {  "3341.xml", "3345.xml", "32700.xml", 

                            "Communication_Characteristics-V1_0.xml",

                            "LWM2M_Lock_and_Wipe-V1_0.xml", "LWM2M_Cellular_connectivity-v1_0.xml",
                            "LWM2M_APN_connection_profile-v1_0.xml", "LWM2M_WLAN_connectivity4-v1_0.xml",
                            "LWM2M_Bearer_selection-v1_0.xml", "LWM2M_Portfolio-v1_0.xml", "LWM2M_DevCapMgmt-v1_0.xml",
                            "LWM2M_Software_Component-v1_0.xml", "LWM2M_Software_Management-v1_0.xml",

                            "Non-Access_Stratum_NAS_configuration-V1_0.xml" };

    private final static String USAGE = "java -jar leshan-server-demo.jar [OPTION]\n\n";
    
    static LeshanServer lwServer;

    public static void main(String[] args) {
        // Define options for command line tools
        Options options = new Options();

        


        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
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
        String localAddress = cl.getOptionValue("lh");
        String localPortOption = cl.getOptionValue("lp");
        int localPort = LwM2m.DEFAULT_COAP_PORT;
        if (localPortOption != null) {
            localPort = Integer.parseInt(localPortOption);
        }



        // get http address
        String webAddress = cl.getOptionValue("wh");
        int webPort = 8080;

        // Get models folder
        String modelsFolderPath = cl.getOptionValue("m");

        // get the Redis hostname:port
        String redisUrl = cl.getOptionValue("r");

        
        try {
            createAndStartServer(webAddress, webPort, localAddress, localPort,
                    modelsFolderPath, redisUrl);
        } catch (BindException e) {
            System.err.println(
                    String.format("Web port %s is already used, you could change it using 'webport' option.", webPort));
            formatter.printHelp(USAGE, options);
        } catch (Exception e) {
            LOG.error("Jetty stopped with unexpected error ...", e);
        }
    }

    public static void createAndStartServer(String webAddress, int webPort, String localAddress, int localPort,
            String modelsFolderPath, String redisUrl) throws Exception {
        // Prepare LWM2M server
        LeshanServerBuilder builder = new LeshanServerBuilder();
        builder.setLocalAddress(localAddress, localPort);
        builder.setEncoder(new DefaultLwM2mNodeEncoder());
        LwM2mNodeDecoder decoder = new DefaultLwM2mNodeDecoder();
        builder.setDecoder(decoder);

        // Create CoAP Config
        NetworkConfig coapConfig;
        File configFile = new File(NetworkConfig.DEFAULT_FILE_NAME);
        if (configFile.isFile()) {
            coapConfig = new NetworkConfig();
            coapConfig.load(configFile);
        } else {
            coapConfig = LeshanServerBuilder.createDefaultNetworkConfig();
            coapConfig.store(configFile);
        }
        builder.setCoapConfig(coapConfig);

        // Connect to redis if needed
        Pool<Jedis> jedis = null;
        if (redisUrl != null) {
            // TODO: support sentinel pool and make pool configurable
            jedis = new JedisPool(new URI(redisUrl));
        }

        X509Certificate serverCertificate = null;

        
        // Set up RPK mode
        try {
            PrivateKey privateKey = SecurityUtil.privateKey.readFromResource("credentials/server_privkey.der");
            serverCertificate = SecurityUtil.certificate.readFromResource("credentials/server_cert.der");
            builder.setPrivateKey(privateKey);
            builder.setCertificateChain(new X509Certificate[] { serverCertificate });

            // Use a certificate verifier which trust all certificates by default.
            Builder dtlsConfigBuilder = new DtlsConnectorConfig.Builder();
            dtlsConfigBuilder.setCertificateVerifier(new CertificateVerifier() {
                @Override
                public void verifyCertificate(CertificateMessage message, DTLSSession session)
                        throws HandshakeException {
                    // trust all means never raise HandshakeException
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            });
            builder.setDtlsConfig(dtlsConfigBuilder);

        } catch (Exception e) {
            LOG.error("Unable to load embedded X.509 certificate.", e);
            System.exit(-1);
        }


        // Define model provider
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
        if (modelsFolderPath != null) {
            models.addAll(ObjectLoader.loadObjectsFromDir(new File(modelsFolderPath)));
        }
        LwM2mModelProvider modelProvider = new StaticModelProvider(models);
        builder.setObjectModelProvider(modelProvider);

        // Set securityStore & registrationStore
        EditableSecurityStore securityStore;
        if (jedis == null) {
            // use file persistence
            securityStore = new FileSecurityStore();
        } else {
            // use Redis Store
            securityStore = new RedisSecurityStore(jedis);
            builder.setRegistrationStore(new RedisRegistrationStore(jedis));
        }
        builder.setSecurityStore(securityStore);

        // Create and start LWM2M server
        lwServer = builder.build();
        
        lwServer.getRegistrationService().addListener(new RegistrationListener() {
        	
//            private Map<String, Integer> stateRegistry;
//            private Map<String, String> clientToSpot;

		    public void registered(Registration registration_in, Registration previousReg, Collection<Observation> previousObsersations) {
		    	
		    	final Registration registration = registration_in;
		    	
		        System.out.println("new device: " + registration.getEndpoint());
		        //String yValueTarget = "/3345/0/5703";
		        String parkingSpotIdObject = "/32700";
		        
		        // Get ID and State of parking Spot
                ReadRequest PSrequest = new ReadRequest(parkingSpotIdObject);
                ReadResponse response = null;
				try {
					response = lwServer.send(registration, PSrequest, TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LwM2mObject objectID = (LwM2mObject) response.getContent();
				LwM2mObjectInstance objectIDinstance = objectID.getInstance(0);
				System.out.println("ID: " + objectIDinstance.getResource(32800).getValue());
				System.out.println("State: " + objectIDinstance.getResource(32801).getValue());
				
				// ADD TO THE DATABASE
				
		        // create & process request
                
                ObserveRequest request = new ObserveRequest(ContentFormat.JSON, 3345, 5703);
                try {
					ObserveResponse cResponse = lwServer.send(registration, request, TIMEOUT);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
//		        
//		        yValueCoapClient.observe(new CoapHandler() {
//
//		            @Override
//		            public void onLoad(CoapResponse response) {
//		                float yValue = Float.parseFloat(response.getResponseText());
//
//		                if (yValue == 100 && yValue != stateRegistry.get(registration.getEndpoint())) {
//		                    stateClient.put("occupied", MediaTypeRegistry.TEXT_PLAIN);
//		                    System.out.println("changed to ocuppied");
//		                    stateRegistry.put(registration.getEndpoint(), 100);
//		                    //ReservationDao.writeEventToDatabase(registration.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "occupy");
//		                }
//
//		                if (yValue == -100 && yValue != stateRegistry.get(registration.getEndpoint())) {
//		                    stateClient.put("free", MediaTypeRegistry.TEXT_PLAIN);
//		                    System.out.println("changed to free");
//		                    stateRegistry.put(registration.getEndpoint(), -100);
//		                    //ReservationDao.writeEventToDatabase(registration.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "free");
//		                }
//		            }
//
//		            @Override
//		            public void onError() {
//		                System.err.println("Error on setting the observe relation");
//		            }
//		        });
		    }

		    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
		        System.out.println("device is still here: " + updatedReg.getEndpoint());
		    }

		    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired, Registration newReg) {
		        System.out.println("device left: " + registration.getEndpoint());
		    }
		    
		    
		});
        
//        lwServer.getObservationRegistry().addListener(new ObservationRegistryListener() {
//        	
//        })
        
        	
        lwServer.getObservationService().addListener(new ObservationListener() {
        	public void newObservation(Observation observation, Registration registration) {
        		
        	}
        	
        	public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
        		LwM2mObject objectYValue = (LwM2mObject) response.getContent();
				LwM2mObjectInstance objectYValueinstance = objectYValue.getInstance(0);
				System.out.println("Y Value: " + objectYValueinstance.getResource(5703).getValue());
				
				Double Yvalue = (Double) objectYValueinstance.getResource(5703).getValue();
				
        		float yValue = Yvalue.floatValue();
        		
        		// PUTTING THINGS IN DATA BASE IF A CHANGE IS FOUND

//                if (yValue == 100 && yValue != stateRegistry.get(registration.getEndpoint())) {
//                    stateClient.put("occupied", MediaTypeRegistry.TEXT_PLAIN);
//                    System.out.println("changed to ocuppied");
//                    stateRegistry.put(registration.getEndpoint(), 100);
//                    //ReservationDao.writeEventToDatabase(registration.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "occupy");
//                }
//
//                if (yValue == -100 && yValue != stateRegistry.get(registration.getEndpoint())) {
//                    stateClient.put("free", MediaTypeRegistry.TEXT_PLAIN);
//                    System.out.println("changed to free");
//                    stateRegistry.put(registration.getEndpoint(), -100);
//                    //ReservationDao.writeEventToDatabase(registration.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "free");
//                }
        	}
        	
        	public void cancelled(Observation observation) {
        		
        	}
        	
        	public void onError(Observation observation, Registration registration, Exception exception) {
        		
        	}
        });
        
        // Now prepare Jetty
        InetSocketAddress jettyAddr;
        if (webAddress == null) {
            jettyAddr = new InetSocketAddress(webPort);
        } else {
            jettyAddr = new InetSocketAddress(webAddress, webPort);
        }
        Server server = new Server(jettyAddr);
        WebAppContext root = new WebAppContext();
        root.setContextPath("/");
        root.setResourceBase(IoT_Server.class.getClassLoader().getResource("webapp").toExternalForm());
        root.setParentLoaderPriority(true);
        server.setHandler(root);

        // Create Servlet
        EventServlet eventServlet = new EventServlet(lwServer, lwServer.getSecuredAddress().getPort());
        ServletHolder eventServletHolder = new ServletHolder(eventServlet);
        root.addServlet(eventServletHolder, "/event/*");

        ServletHolder clientServletHolder = new ServletHolder(new ClientServlet(lwServer));
        root.addServlet(clientServletHolder, "/api/clients/*");

        ServletHolder securityServletHolder = new ServletHolder(new SecurityServlet(securityStore, serverCertificate));
        root.addServlet(securityServletHolder, "/api/security/*");

        ServletHolder objectSpecServletHolder = new ServletHolder(new ObjectSpecServlet(lwServer.getModelProvider()));
        root.addServlet(objectSpecServletHolder, "/api/objectspecs/*");
        
        // New servlet to manage the reservation process
        ServletHolder reservationServletHolder = new ServletHolder(new ClientReservationServlet(lwServer));
        root.addServlet(reservationServletHolder, "/api/reservation/*");
        

        
        DataBase db = new DataBase();
        db.deleteDataBase("ParkingLotDB.db");
		db.connect("ParkingLotDB");
		
		db.createTable();
		
        LOG.info("DataBase Created");
        
        // Register a service to DNS-SD
		try {
			JmDNS jmdns = Publish.createService();
			Publish.publishCoapService(jmdns, webPort);
			Publish.publishHTTPService(jmdns, localPort);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // Start Jetty & Leshan
        lwServer.start();
        server.start();

        
        LOG.info("Web server started at {}.", server.getURI());
    }
}
