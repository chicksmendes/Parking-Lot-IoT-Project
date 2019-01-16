package org.eclipse.leshan.client.demo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Discovery {
	public static String discoverService() throws InterruptedException {
		String ip = null;
    	try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            
            
            // Add a service listener
            //jmdns.addServiceListener("_coap._udp.local.", new SampleListener());
            for(ServiceInfo service : jmdns.list("_coap._udp.local.")) {
            	for(Inet4Address addr : service.getInet4Addresses()) {
            		ip = addr.getHostAddress();
            		
            	}
            }
            
            // Wait a bit
            Thread.sleep(5000);
            
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    	return ip;

    }
}
