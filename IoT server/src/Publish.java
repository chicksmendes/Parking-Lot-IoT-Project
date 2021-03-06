import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public abstract class Publish {
	 
	public static void publishService() throws InterruptedException {

	        try {
	            // Create a JmDNS instance
	            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

	            // Register a service
	            ServiceInfo serviceInfo = ServiceInfo.create("_coap._udp.local.", "test", 5683, "testtt");
	            jmdns.registerService(serviceInfo);

	            // Wait a bit
	            //Thread.sleep(25000);

	            // Unregister all services
	            //jmdns.unregisterAllServices();

	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	        }
	    }
}
