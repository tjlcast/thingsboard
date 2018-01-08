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
package org.thingsboard.server.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.actors.device.DeviceShadowMsg;
import org.thingsboard.server.actors.service.ActorService;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.dao.device.DeviceService;
import com.google.gson.JsonParser;
/**
 * Created by Administrator on 2017/12/13.
 */
@RestController
@RequestMapping("/api/shadow")
public class ShadowController {

    @Autowired
    protected ActorService actorService;

    @Autowired
    protected DeviceService deviceService;

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{deviceId}", method = RequestMethod.POST)
    public DeferredResult<String> shadow(@RequestBody String json,@PathVariable String deviceId){
        DeferredResult<String> result = new DeferredResult<>();
        JsonParser parser = new JsonParser();
        Device device = deviceService.findDeviceById(DeviceId.fromString(deviceId));
        JsonObject obj = parser.parse(json).getAsJsonObject();
        if(obj.get("requestName").getAsString().equals("serviceCall")){
            if(device.getParentDeviceId()==null||"".equals(device.getParentDeviceId())||
                    "13814000-1dd2-11b2-8080-808080808080".equals(device.getParentDeviceId())){
                DeviceShadowMsg msg = new DeviceShadowMsg(device,parser.parse(json).getAsJsonObject(),result);
                actorService.onMsg(msg);
            }else{
              Device parentDevice =  deviceService.findDeviceById(DeviceId.fromString(device.getParentDeviceId()));
                DeviceShadowMsg msg = new DeviceShadowMsg(parentDevice,parser.parse(json).getAsJsonObject(),result);
                actorService.onMsg(msg);
            }
        }else{
            DeviceShadowMsg msg = new DeviceShadowMsg(device,parser.parse(json).getAsJsonObject(),result);
            actorService.onMsg(msg);
        }
        return result;
    }
}
