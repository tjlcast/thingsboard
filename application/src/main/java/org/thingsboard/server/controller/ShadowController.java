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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.thingsboard.server.extensions.api.plugins.PluginConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Future;
import java.net.URLDecoder;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    protected ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    protected Map<String,Map<UUID,TaskBean>> taskMap = new HashMap<>();


    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{deviceId}", method = RequestMethod.POST)
    public DeferredResult<String> shadow(@RequestBody String json1,@PathVariable String deviceId){
        String json = URLDecoder.decode(json1);
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

  //  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{deviceId}/{scheduleType}/**", method = RequestMethod.POST)
    public DeferredResult<String> shadow2schedule(@RequestBody String json1,@PathVariable String deviceId,
                                                  @PathVariable String scheduleType,  HttpServletRequest request){
   //    String json = URLDecoder.decode(json1);
        String json = json1;
        DeferredResult<String> result = new DeferredResult<>();
        JsonParser parser = new JsonParser();
        Device device = deviceService.findDeviceById(DeviceId.fromString(deviceId));
        JsonObject obj = parser.parse(json).getAsJsonObject();

        String[] paths = getPathParams(request);

        if(obj.get("requestName").getAsString().equals("serviceCall")){
            if(device.getParentDeviceId()==null||"".equals(device.getParentDeviceId())||
                    "13814000-1dd2-11b2-8080-808080808080".equals(device.getParentDeviceId())){
                DeviceShadowMsg msg = new DeviceShadowMsg(device,parser.parse(json).getAsJsonObject(),result);
                helper(msg,scheduleType,paths);
            }else{
                Device parentDevice =  deviceService.findDeviceById(DeviceId.fromString(device.getParentDeviceId()));
                DeviceShadowMsg msg = new DeviceShadowMsg(parentDevice,parser.parse(json).getAsJsonObject(),result);
                helper(msg,scheduleType,paths);
            }
        }else{
            result.setResult("wrong type of requestName");
        }
        return result;
    }

  //  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/cancel/{deviceId}/{taskId}", method = RequestMethod.GET)
    public String cancelTask(@PathVariable String deviceId,@PathVariable String taskId) {
        Map<UUID,TaskBean> map = taskMap.get(deviceId);
        if(map!=null){
            TaskBean bean = map.get(UUID.fromString(taskId));
            if(bean!=null&&!bean.isCancel()){
                bean.getFuture().cancel(true);
                bean.setCancel(true);
                return "cancel success";
            }else{
                return "has bean canceled";
            }
        }else{
            return "specefied device has no task";
        }
    }

  //  @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/list/{deviceId}", method = RequestMethod.GET)
    public String listTask(@PathVariable String deviceId) {

        Map<UUID,TaskBean> map = taskMap.get(deviceId);
        if(map==null){
            return "";
        }
        JsonArray res =new JsonArray();
        for(Map.Entry<UUID,TaskBean> entry:map.entrySet()){
            JsonObject obj = new JsonObject();
            obj.addProperty("id",entry.getKey().toString());
            obj.addProperty("description",entry.getValue().getDescription());
            obj.addProperty("iscanceled",entry.getValue().isCancel());
            res.add(obj);
        }
        return res.toString();
    }

    private void helper(DeviceShadowMsg msg,String scheduleType,String[] paths){
        UUID id = UUID.randomUUID();
        DeviceId deviceId = msg.getDeviceId();
        if(!taskMap.containsKey(deviceId.toString())){
            taskMap.put(deviceId.toString(),new HashMap<>());
        }
        if(scheduleType.equals("delay")){
            String des = "这是一个下达于 "+new Date().toString()+" 的延时任务，任务指定 "+new Date(System.currentTimeMillis()+Long.parseLong(paths[0])*1000l).toString()+" 开始执行";
            java.util.concurrent.Future future= scheduleOnce(()->{
//                actorService.onMsg(msg);
                System.err.println("delay task start");
            } ,Long.parseLong(paths[0]),TimeUnit.SECONDS);
            taskMap.get(deviceId.toString()).put(id,new TaskBean(future,id,des,false));
            msg.setResult("task submit ok");
        }else if(scheduleType.equals("period")){
            String des = "这是一个下达于 "+new Date().toString()+" 的周期任务，任务指定 "+new Date(System.currentTimeMillis()+Long.parseLong(paths[0])*1000l).toString()+
                    " 开始执行，并在只有以 " + paths[1] + "秒为周期执行";
            java.util.concurrent.Future future = schedule(()->{
//                actorService.onMsg(msg);
                System.err.println("period task start");
            },Long.parseLong(paths[0]),Long.parseLong(paths[1]), TimeUnit.SECONDS);
            taskMap.get(deviceId.toString()).put(id,new TaskBean(future,id,des,false));
             msg.setResult("task submit ok");
        }
    }

    private String[] getPathParams(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        int index = requestUrl.indexOf("/api/shadow");
        String[] pathParams = requestUrl.substring(index + "/api/shadow".length()).split("/");
        String[] result = new String[pathParams.length -3];
        System.arraycopy(pathParams, 3, result, 0, result.length);
        return result;
    }

    private java.util.concurrent.Future schedule(Runnable run, long delay, long period, TimeUnit unit){
        java.util.concurrent.Future future = executorService.scheduleAtFixedRate(run,delay,period,unit);
        return future;

    }

    private java.util.concurrent.Future scheduleOnce(Runnable run, long delay,TimeUnit unit){
        java.util.concurrent.Future future = executorService.schedule(run,delay,unit);
        return future;
    }

    @Data
    @AllArgsConstructor
    class TaskBean{
        java.util.concurrent.Future future;
        UUID id;
        String Description;
        boolean cancel;
    }


}
