package org.thingsboard.server.common.msg.device;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.aware.DeviceAwareMsg;
import org.thingsboard.server.common.msg.aware.TenantAwareMsg;

/**
 * Created by Administrator on 2017/12/10.
 */
public class DeviceRecognitionMsg implements TenantAwareMsg,DeviceAwareMsg{

    private final String manufacture;
    private final String deviceType;
    private final String model;
    private final Device device;

    public DeviceRecognitionMsg(String manufacture, String deviceType, String model,Device device) {
        this.manufacture = manufacture;
        this.deviceType = deviceType;
        this.model = model;
        this.device = device;
    }

    @Override
    public TenantId getTenantId() {
        return device.getTenantId();
    }

    @Override
    public DeviceId getDeviceId() {
        return device.getId();
    }

    public String getManufacture() {
        return manufacture;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getModel() {
        return model;
    }
}
