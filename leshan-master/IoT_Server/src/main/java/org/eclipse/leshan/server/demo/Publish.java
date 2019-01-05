package org.eclipse.leshan.server.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Publish {
	public static JmDNS createService() throws InterruptedException {


        // Create a JmDNS instance
        JmDNS jmdns = null;
		try {
			jmdns = JmDNS.create(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return jmdns;

        
    }
	public static void publishCoapService(JmDNS jmdns, int localPort) throws InterruptedException {

        try {
            // Register a service
            ServiceInfo CoapServiceInfo = ServiceInfo.create("_coap._udp.local.", "leshan", localPort, "");
            jmdns.registerService(CoapServiceInfo);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
	public static void publishHTTPService(JmDNS jmdns, int webPort) throws InterruptedException {

        try {
        	 // Publish Leshan HTTP Service
            ServiceInfo httpServiceInfo = ServiceInfo.create("_http._tcp.local.", "leshan", webPort, "");
            jmdns.registerService(httpServiceInfo);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
