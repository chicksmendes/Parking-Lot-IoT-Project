package org.eclipse.leshan.server.demo;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.leshan.core.request.Identity;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;
import org.eclipse.leshan.core.response.SendableResponse;
import org.eclipse.leshan.server.impl.RegistrationServiceImpl;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationHandler;
import org.eclipse.leshan.server.registration.RegistrationIdProvider;
import org.eclipse.leshan.server.security.Authorizer;

public class NewParkHandler extends RegistrationHandler{
	
    private Registration clientRegistry;
    private Map<String, Integer> stateRegistry;
    private Map<String, String> clienToSpot;


    public NewParkHandler(Registration clientRegistry, RegistrationServiceImpl registrationService, Authorizer authorizer,
            RegistrationIdProvider registrationIdProvider) {
        super(registrationService, authorizer, registrationIdProvider);
        this.clientRegistry = clientRegistry;
        this.stateRegistry = new HashMap<>();
        this.clienToSpot = new HashMap<>();
    }

    @Override
    public SendableResponse<RegisterResponse> register(Identity sender, RegisterRequest registerRequest, InetSocketAddress serverEndpoint) {
        SendableResponse<RegisterResponse> response = super.register(sender, registerRequest, serverEndpoint);
        
        //server.getRegistrationService().getAllRegistrations()
        final Registration client = clientRegistry;
        //final Registration client = clientRegistry.findByRegistrationId(((RegisterResponse) response).getRegistrationID());

        String yValueTarget = "/3345/0/5703";
        String clientAddr = client.getAddress().getHostAddress();
        String uriPrefix = "coap://" + clientAddr + ":" + Integer.toString(client.getPort());

        String stateTarget = "/32700/0/32801";
        final CoapClient stateClient = new CoapClient(uriPrefix + stateTarget);

        String parkingSpotIdTarget = "/32700/0/32800";
        final CoapClient parkingSpotIdClient = new CoapClient(uriPrefix + parkingSpotIdTarget);


        stateRegistry.put(client.getEndpoint(), -100);

        String parkingSpotId = parkingSpotIdClient.get().getResponseText();
        clienToSpot.put(client.getEndpoint(), parkingSpotId);


        CoapClient yValueCoapClient = new CoapClient(uriPrefix + yValueTarget);
        System.out.println(uriPrefix + yValueTarget);

        yValueCoapClient.observe(new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
                float yValue = Float.parseFloat(response.getResponseText());

                if (yValue == 100 && yValue != stateRegistry.get(client.getEndpoint())) {
                    stateClient.put("occupied", MediaTypeRegistry.TEXT_PLAIN);
                    System.out.println("changed to ocuppied");
                    stateRegistry.put(client.getEndpoint(), 100);
                    //ReservationDao.writeEventToDatabase(client.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "occupy");
                }

                if (yValue == -100 && yValue != stateRegistry.get(client.getEndpoint())) {
                    stateClient.put("free", MediaTypeRegistry.TEXT_PLAIN);
                    System.out.println("changed to free");
                    stateRegistry.put(client.getEndpoint(), -100);
                    //ReservationDao.writeEventToDatabase(client.getEndpoint(), clienToSpot.get(client.getRegistrationId()), null, null, "free");
                }
            }

            @Override
            public void onError() {
                System.err.println("Error on setting the observe relation");
            }
        });


        return response;
    }

}
