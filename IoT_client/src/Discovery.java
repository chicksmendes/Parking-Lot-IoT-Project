import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public abstract class Discovery {
//	private static class SampleListener implements ServiceListener {
//        @Override
//        public void serviceAdded(ServiceEvent event) {
//            System.out.println("Service added: " + event.getInfo());
//        }
//
//        @Override
//        public void serviceRemoved(ServiceEvent event) {
//            System.out.println("Service removed: " + event.getInfo());
//        }
//
//        @Override
//        public void serviceResolved(ServiceEvent event) {
//            System.out.println("Service resolved: " + event.getInfo());
//        }
//    }

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
            		//url = "127.0.0.1";
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
