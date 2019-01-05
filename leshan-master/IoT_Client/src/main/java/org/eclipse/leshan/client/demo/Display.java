package org.eclipse.leshan.client.demo;

import java.util.Arrays;
import java.util.List;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

public class Display extends BaseInstanceEnabler {
	
	private static final int TEXT = 5527;
	
	private String Text;
	
	private static final List<Integer> supportedResources = Arrays.asList(TEXT);
	
	public Display() {
		Text = "green";
	}
	
	@Override
	public ReadResponse read(int resourceId) {
		System.out.println("Device : read on resource " + resourceId);
		
		switch (resourceId) {
		case TEXT:
			return ReadResponse.success(resourceId, Text);
		default:
			return super.read(resourceId);
		}
	}
	
	@Override
	public WriteResponse write(int resourceId, LwM2mResource value) {
		System.out.println("Device : write on resource " + resourceId);
		
		switch (resourceId) {
		case TEXT:
			Text = (String) value.getValue();
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
