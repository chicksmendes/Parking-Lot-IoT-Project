import java.util.Collection;

import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

public class IoTServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LeshanServerBuilder builder = new LeshanServerBuilder();
		// add this line if you are using leshan 1.0.0-M4 because of 
		// https://github.com/eclipse/leshan/issues/392
		// builder.setSecurityStore(new InMemorySecurityStore());
		LeshanServer server = builder.build();
		
		server.getRegistrationService().addListener(new RegistrationListener() {

		    public void registered(Registration registration, Registration previousReg,
		            Collection<Observation> previousObsersations) {
		        System.out.println("new device: " + registration.getEndpoint());
		    }

		    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
		        System.out.println("device is still here: " + updatedReg.getEndpoint());
		    }

		    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
		            Registration newReg) {
		        System.out.println("device left: " + registration.getEndpoint());
		    }
		});
		
		try {
			Publish.publishService();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		DataBase db = new DataBase();
		
		db.connect("ParkingLotDB");
		db.createTable();
		//db.insert();
//		db.select();
//		db.update();
//		db.delete();
		db.close();
	
        server.start();
	}
}





//server.getRegistrationService().addListener(new RegistrationListener() {
//
//	public void registered(Registration registration, Registration previousReg, 
//			Collection<Observation> previousObsersations) {
//	    System.out.println("new device: " + registration.getEndpoint());
//	    try {
//	        ReadResponse response = server.send(registration, new ReadRequest(3,0,10));
//	        if (response.isSuccess()) {
//	            System.out.println("Device time:" + ((LwM2mResource)response.getContent()).getValue());
//	        }else {
//	            System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
//	        }
//	    } catch (InterruptedException e) {
//	        e.printStackTrace();
//	    }
//	}
//
//    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
//        System.out.println("device is still here: " + updatedReg.getEndpoint());
//    }
//
//    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
//            Registration newReg) {
//        System.out.println("device left: " + registration.getEndpoint());
//    }
//});
//