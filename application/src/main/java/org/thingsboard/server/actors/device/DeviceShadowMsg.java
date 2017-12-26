/**
 * Copyright © 2016-2017 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
