package org.thingsboard.server.actors.device;

import com.google.gson.JsonObject;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.extensions.api.device.ToDeviceActorNotificationMsg;

/**
 * Created by Administrator on 2017/12/13.
 */
public class DeviceShadowMsg implements ToDeviceActorNotificationMsg {

    private final Device device;
    private final JsonObject payLoad;
    private final DeferredResult<String> result;

    public DeviceShadowMsg(Device device,JsonObject json,DeferredResult<String> result){
        this.device = device;
        this.payLoad = json;
        this.result = result;
    }

    @Override
    public TenantId getTenantId() {
        return device.getTenantId();
    }

    @Override
    public DeviceId getDeviceId() {
        return device.getId();
    }

    public JsonObject getPayLoad(){
        return payLoad;
    }
    public void setResult(String res){
        result.setResult(res);
    }
}
