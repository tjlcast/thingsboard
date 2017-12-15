package org.thingsboard.server.controller;

import com.google.gson.Gson;
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
        DeviceShadowMsg msg = new DeviceShadowMsg(device,parser.parse(json).getAsJsonObject(),result);
        actorService.onMsg(msg);
        return result;
    }
}
