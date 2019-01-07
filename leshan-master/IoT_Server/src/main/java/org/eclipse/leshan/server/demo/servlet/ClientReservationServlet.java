package org.eclipse.leshan.server.demo.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObject;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.codec.CodecException;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.request.WriteRequest.Mode;
import org.eclipse.leshan.core.request.exception.ClientSleepingException;
import org.eclipse.leshan.core.request.exception.InvalidRequestException;
import org.eclipse.leshan.core.request.exception.InvalidResponseException;
import org.eclipse.leshan.core.request.exception.RequestCanceledException;
import org.eclipse.leshan.core.request.exception.RequestRejectedException;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeDeserializer;
import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeSerializer;
import org.eclipse.leshan.server.demo.servlet.json.RegistrationSerializer;
import org.eclipse.leshan.server.demo.servlet.json.ResponseSerializer;
import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
/**
 * Service HTTP REST API calls.
 */
public class ClientReservationServlet extends HttpServlet {
	
	//private static final String FORMAT_PARAM = "format";

    private static final Logger LOG = LoggerFactory.getLogger(ClientReservationServlet.class);

    private static final long TIMEOUT = 5000; // ms

    private static final long serialVersionUID = 1L;

    private final LwM2mServer server;

    private final Gson gson;

    public ClientReservationServlet(LwM2mServer server) {
        this.server = server;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Registration.class,
                new RegistrationSerializer(server.getPresenceService()));
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mResponse.class, new ResponseSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeDeserializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        this.gson = gsonBuilder.create();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	// all registered clients
        if (req.getPathInfo() == null) {
        	
        	// Get all Pi - parkingSpots in the system
        	Collection<Registration> devices = new ArrayList<>();
            for (Iterator<Registration> iterator = server.getRegistrationService().getAllRegistrations(); iterator
                    .hasNext();) {
                devices.add(iterator.next());
            }
            
            List<ParkingSpotObject> ListParkingSpots = new ArrayList<ParkingSpotObject>();      
            List<ParkingSpotObject> ListFreeParkingSpots = new ArrayList<ParkingSpotObject>();      
            for (Registration device : devices) {
            	ParkObject parkObject = new ParkObject();
                String endPoint = device.getEndpoint();

                System.out.println("endpoint :" + endPoint);
                
                String locationTarget = "/6";
                ReadRequest locationRequest = new ReadRequest(locationTarget);
                ReadResponse cLocationResponse = null;
				try {
					cLocationResponse = server.send(device, locationRequest, TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                LwM2mObject locationNode = (LwM2mObject) cLocationResponse.getContent();
                LwM2mObjectInstance location = locationNode.getInstance(0);
                Double latitude = (Double) location.getResource(0).getValue();
                Double longitude = (Double) location.getResource(1).getValue();
                parkObject.setLatitude(latitude.floatValue());
                parkObject.setLongitude(longitude.floatValue());
                parkObject.setEndPoint(endPoint);

                String spotTarget = "/32700";
                ReadRequest spotRequest = new ReadRequest(spotTarget);
                ReadResponse cSpotResponse = null;
				try {
					cSpotResponse = server.send(device, spotRequest, TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                LwM2mObject spotNode = (LwM2mObject) cSpotResponse.getContent();
                Map<Integer, LwM2mObjectInstance> spotInstances = spotNode.getInstances();
                for (Map.Entry<Integer, LwM2mObjectInstance> entry : spotInstances.entrySet()) {
                    String spotId = (String) entry.getValue().getResource(32800).getValue();
                    String spotState = (String) entry.getValue().getResource(32801).getValue();
                    String vehicleId = (String) entry.getValue().getResource(32802).getValue();
                    Double billingRate = (Double) entry.getValue().getResource(32803).getValue();
                    ParkingSpotObject ps = new ParkingSpotObject(spotId, spotState, vehicleId, billingRate.floatValue());
                    parkObject.addParkingSpot(ps);
                    ListParkingSpots.add(ps);   
                    if(spotState.equals("free")) {
                    	ListFreeParkingSpots.add(ps);   
                    }
                }
                
                       
            }
            String json = this.gson.toJson(ListFreeParkingSpots);
            // String json = this.gson.toJson(clients.toArray(new Client[] {}));
            resp.setContentType("application/json");
            resp.getOutputStream().write(json.getBytes("UTF-8"));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] path = StringUtils.split(req.getPathInfo(), '/');
        String clientEndpoint = path[0];

        // at least /endpoint/objectId/instanceId
        if (path.length < 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        try {
            String target = StringUtils.removeStart(req.getPathInfo(), "/" + clientEndpoint);
            Registration registration = server.getRegistrationService().getByEndpoint(clientEndpoint);
            if (registration != null) {
//                WriteResponse cResponse = reservationRequest(registration);
//                processDeviceResponse(req, resp, cResponse);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().format("No registered client with id '%s'", clientEndpoint).flush();
            }
        } catch (RuntimeException e) {
            handleException(e, resp);
        }
    }
    
    private void handleException(Exception e, HttpServletResponse resp) throws IOException {
        if (e instanceof InvalidRequestException || e instanceof CodecException
                || e instanceof ClientSleepingException) {
            LOG.warn("Invalid request", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().append("Invalid request:").append(e.getMessage()).flush();
        } else if (e instanceof RequestRejectedException) {
            LOG.warn("Request rejected", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Request rejected:").append(e.getMessage()).flush();
        } else if (e instanceof RequestCanceledException) {
            LOG.warn("Request cancelled", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Request cancelled:").append(e.getMessage()).flush();
        } else if (e instanceof InvalidResponseException) {
            LOG.warn("Invalid response", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Invalid Response:").append(e.getMessage()).flush();
        } else if (e instanceof InterruptedException) {
            LOG.warn("Thread Interrupted", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Thread Interrupted:").append(e.getMessage()).flush();
        } else {
            LOG.warn("Unexpected exception", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Unexpected exception:").append(e.getMessage()).flush();
        }
    }
    
    private void processDeviceResponse(HttpServletRequest req, HttpServletResponse resp, LwM2mResponse cResponse)
            throws IOException {
        if (cResponse == null) {
            LOG.warn(String.format("Request %s%s timed out.", req.getServletPath(), req.getPathInfo()));
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append("Request timeout").flush();
        } else {
            String response = this.gson.toJson(cResponse);
            resp.setContentType("application/json");
            resp.getOutputStream().write(response.getBytes());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
    
//    private LwM2mNode extractLwM2mNode(String target, HttpServletRequest req) throws IOException {
//        String contentType = StringUtils.substringBefore(req.getContentType(), ";");
//        if ("application/json".equals(contentType)) {
//            String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
//            LwM2mNode node;
//            try {
//                node = gson.fromJson(content, LwM2mNode.class);
//            } catch (JsonSyntaxException e) {
//                throw new InvalidRequestException(e, "unable to parse json to tlv:%s", e.getMessage());
//            }
//            return node;
//        } else if ("text/plain".equals(contentType)) {
//            String content = IOUtils.toString(req.getInputStream(), req.getCharacterEncoding());
//            int rscId = Integer.valueOf(target.substring(target.lastIndexOf("/") + 1));
//            return LwM2mSingleResource.newStringResource(rscId, content);
//        }
//        throw new InvalidRequestException("content type %s not supported", req.getContentType());
//    }



    // TODO refactor the code to remove this method.
    private WriteResponse reservationRequest(Registration registration, String target, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Map<String, String> parameters = new HashMap<String, String>();
        String contentType = HttpFields.valueParameters(req.getContentType(), parameters);
        WriteResponse response = null;
        if ("text/plain".equals(contentType)) {
            String content = IOUtils.toString(req.getInputStream(), parameters.get("charset"));
            int rscId = Integer.valueOf(target.substring(target.lastIndexOf("/") + 1));
            WriteRequest writeRequest = new WriteRequest(Mode.REPLACE, ContentFormat.TEXT, target,
                    LwM2mSingleResource.newStringResource(rscId, content));
            try {
				return server.send(registration, writeRequest, TIMEOUT);
			} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
					| ClientSleepingException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        } else if ("application/json".equals(contentType)) {
            String content = IOUtils.toString(req.getInputStream(), parameters.get("charset"));
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
            String vehicleId = jsonObject.get("value").getAsString();
            String action = jsonObject.get("action").getAsString();

            // hack to reserve parking spot--------------------------
            String content2 = "";
            String target2 = "";
            String contentColor = "";
            String targetColor = "";
            if ("reserve".equalsIgnoreCase(action)) {
                content2 = "{'id':'32801','value':'reserved'}";
                int i = target.lastIndexOf('/');
                target2 = target.substring(0, i + 1) + "32801";
                contentColor = "{'id':'5527','value':'orange'}";
                targetColor = "/3341/0/5527";
            
	            LwM2mNode node2 = null;
	            LwM2mNode nodeColor = null;
	            try {
	                node2 = gson.fromJson(content2, LwM2mNode.class);
	                nodeColor = gson.fromJson(contentColor, LwM2mNode.class);
	            } catch (JsonSyntaxException e) {
	                throw new IllegalArgumentException("unable to parse json to tlv:" + e.getMessage(), e);
	            }
	
	            try {
					server.send(registration, new WriteRequest(Mode.REPLACE, null, targetColor, nodeColor), TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InvalidRequestException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            try {
					server.send(registration, new WriteRequest(Mode.REPLACE, null, target2, node2), TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InvalidRequestException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            //get spot info
	        	String spotTarget = "/32700";
	            ReadRequest spotRequest = new ReadRequest(spotTarget);
	            ReadResponse cSpotResponse = null;
				try {
					cSpotResponse = server.send(registration, spotRequest, TIMEOUT);
				} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
						| ClientSleepingException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            LwM2mObject spotNode = (LwM2mObject) cSpotResponse.getContent();
	            LwM2mObjectInstance spotInstance = spotNode.getInstance(0);
	            String spotId = (String) spotInstance.getResource(32800).getValue();
	            //String spotState = (String) spotInstance.getResource(32801).getValue();	          
	            Double billingRate = (Double) spotInstance.getResource(32803).getValue();
	            
	            // WRITE TO THE DATABASE
            }
            

            LwM2mNode node = null;
            
            try {
                node = gson.fromJson(content, LwM2mNode.class);
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException("unable to parse json to tlv:" + e.getMessage(), e);
            }
            try {
            	response = server.send(registration, new WriteRequest(Mode.REPLACE, null, target, node), TIMEOUT);
			} catch (CodecException | InvalidResponseException | RequestCanceledException | RequestRejectedException
					| ClientSleepingException | InvalidRequestException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
           

        } else {
            throw new IllegalArgumentException(
                    "content type " + req.getContentType() + " not supported for write requests");
        }
        return response;
    }
    
    public void destroy(){
    	//ReservationDao.closeConnection();
    }
            
}
