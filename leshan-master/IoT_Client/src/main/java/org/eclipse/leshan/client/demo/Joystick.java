package org.eclipse.leshan.client.demo;

import java.util.Arrays;
import java.util.List;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

public class Joystick extends BaseInstanceEnabler {
	
	private static final int DIGITAL_INPUT_COUNTER = 5501;
	private static final int Y_VALUE = 5703;
	
	private int DigitalInputCounter;
	private int YValue;
	
	private static final List<Integer> supportedResources = Arrays.asList(DIGITAL_INPUT_COUNTER, Y_VALUE);
	
	public Joystick() {
		DigitalInputCounter = 0;
		YValue = 0;
	}

	@Override
	public ReadResponse read(int resourceId) {
		System.out.println("Device : read on resource " + resourceId);
		
		switch (resourceId) {
		case DIGITAL_INPUT_COUNTER:
			return ReadResponse.success(resourceId, DigitalInputCounter);
		case Y_VALUE:
			return ReadResponse.success(resourceId, YValue);
		default:
			return super.read(resourceId);
		}
	}
	
	@Override
	public WriteResponse write(int resourceId, LwM2mResource value) {
		System.out.println("Device : write on resource " + resourceId);
		
		switch (resourceId) {
		case DIGITAL_INPUT_COUNTER:
			DigitalInputCounter = (int) value.getValue();
			fireResourcesChange(resourceId);
			return WriteResponse.success();
		case Y_VALUE:
			YValue = (int) value.getValue();
			fireResourcesChange(resourceId);
			return WriteResponse.success();
		default:
			return super.write(resourceId, value);
		}
	}
	
	@Override
	public synchronized ExecuteResponse execute(int resourceId, String params) {
		System.out.println("Device : exec on resource " + resourceId);
		
		switch (resourceId) {
		
		default:
			return super.execute(resourceId, params);
		}
	}
	
    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }
	
}
