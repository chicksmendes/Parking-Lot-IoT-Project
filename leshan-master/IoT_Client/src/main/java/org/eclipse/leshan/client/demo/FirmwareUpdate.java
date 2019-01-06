package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

public class FirmwareUpdate extends BaseInstanceEnabler {
	private String packageURI;
    private String update;

    public FirmwareUpdate() {
        packageURI = "";
        update = "";
    }

    @Override
    public ReadResponse read(int resourceid) {
        System.out.println("Read on Firmware Update Resource " + resourceid);
        switch (resourceid) {
            case 1:
                return ReadResponse.success(resourceid, getPackageURI());
            default:
                return super.read(resourceid);
        }
    }

    @Override
    public ExecuteResponse execute(int resourceid, String params) {
        System.out.println("Execute on Firmware Update resource " + resourceid);

        return ExecuteResponse.success();
    }

    @Override
    public WriteResponse write(int resourceid, LwM2mResource value) {
        System.out.println("Write on Firmware Update Resource " + resourceid + " value " + value);
        switch (resourceid) {
            case 13:
                return WriteResponse.notFound();
            case 1:
                setPackageURI((String) value.getValue());
                fireResourcesChange(resourceid);
                return WriteResponse.success();
            default:
                return super.write(resourceid, value);
        }
    }

    public String getPackageURI() {
        return packageURI;
    }

    public void setPackageURI(String packageURI) {
        this.packageURI = packageURI;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
