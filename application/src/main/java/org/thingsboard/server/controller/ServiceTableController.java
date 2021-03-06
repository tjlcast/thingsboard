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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.actors.device.DeviceShadow;
import org.thingsboard.server.actors.service.ActorService;
import org.thingsboard.server.actors.service.DefaultActorService;
import org.thingsboard.server.actors.tenant.ServiceGroupUpdateMsg;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.dao.serviceTable.ServiceTableService;
import org.thingsboard.server.exception.ThingsboardException;
import org.thingsboard.server.transport.http.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by Administrator on 2017/12/26.
 */
@RestController
@RequestMapping("/api")
public class ServiceTableController extends BaseController{

    @Autowired
    ServiceTableService serviceTableService;

    @Autowired
    ActorService actorService;

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/manufatures", method = RequestMethod.GET)
    @ResponseBody
    public String manufatures() throws ThingsboardException {
        return serviceTableService.findAllManufactures().toString();
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/{manufature}/deviceTypes", method = RequestMethod.GET)
    @ResponseBody
    public String deviceTypes(@PathVariable String manufature ) throws ThingsboardException {
        return serviceTableService.findDeviceTypesByManufacture(manufature).toString();
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/{manufature}/{deviceType}/models", method = RequestMethod.GET)
    @ResponseBody
    public String models(@PathVariable String manufature , @PathVariable String deviceType) throws ThingsboardException {
        return serviceTableService.findModelsByManufactureAndDeviceType(manufature,deviceType).toString();
    }


    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/saveServiceGroup", method = RequestMethod.POST)
    @ResponseBody
    public ServiceTable save(@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        ServiceTable serviceTable= new ServiceTable();
        String coordinate = getCoordinate(service);

        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);


   //     JsonArray array = service.get("description").getAsJsonArray();
        JsonObject des = new JsonObject();
//        des.add("attributes",new JsonArray());
//        des.add("telemetries",new JsonArray());
//        des.add("services",new JsonObject());

        serviceTable1.orElse(serviceTable).setCoordinate(coordinate);
        serviceTable1.orElse(serviceTable).setDescription(des.toString());
        serviceTable1.orElse(serviceTable).setCreatedTime(System.currentTimeMillis());
        serviceTable1.orElse(serviceTable).setManufacture(service.get("manufacture").getAsString());
        serviceTable1.orElse(serviceTable).setDevice_type(service.get("deviceType").getAsString());
        serviceTable1.orElse(serviceTable).setModel(service.get("model").getAsString());
       // serviceTable.setId(Ser);
        return serviceTableService.saveServiceTable(serviceTable1.orElse(serviceTable));
    }
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/deleteServiceGroup", method = RequestMethod.POST)
    @ResponseBody
    public void delete(@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        String coordinate = getCoordinate(service);

        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);

        if(!serviceTable1.isPresent()) return;
        serviceTableService.deleteServiceTable(serviceTable1.get().getId());
        ServiceGroupUpdateMsg msg  = new ServiceGroupUpdateMsg(getTenantId(),service.get("manufacture").getAsString(),
                service.get("deviceType").getAsString(),service.get("model").getAsString());

        try{
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }

        ((DefaultActorService)actorService).onMsg(msg);
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/add", method = RequestMethod.POST)
    @ResponseBody
    public ServiceTable addServiceToGroup(@RequestBody String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        String coordinate = getCoordinate(service);

        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);
        if(!serviceTable1.isPresent()) return null;

        ServiceTable serviceTable = serviceTable1.get();
        JsonObject obj = new JsonParser().parse(serviceTable.getDescription()).getAsJsonObject();
        obj.add(service.get("description").getAsJsonObject().get("serviceName").getAsString(),service.get("description"));
        serviceTable.setDescription(obj.toString());

        ServiceTable ss = serviceTableService.saveServiceTable(serviceTable);

        ServiceGroupUpdateMsg msg  = new ServiceGroupUpdateMsg(getTenantId(),service.get("manufacture").getAsString(),
                service.get("deviceType").getAsString(),service.get("model").getAsString());
        //TODO 临时方法
        try{
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }
        ((DefaultActorService)actorService).onMsg(msg);

       return ss;
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/delete", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServiceFromServiceGroup(@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        String coordinate = getCoordinate(service);
        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);
        if(!serviceTable1.isPresent()) return ;

        ServiceTable serviceTable = serviceTable1.get();
        String  str = serviceTable.getDescription();
        JsonObject obj = new JsonParser().parse(str).getAsJsonObject();
        obj.remove(service.get("serviceName").getAsString());
        serviceTable.setDescription(obj.toString());
        ServiceGroupUpdateMsg msg  = new ServiceGroupUpdateMsg(getTenantId(),service.get("manufacture").getAsString(),
                service.get("deviceType").getAsString(),service.get("model").getAsString());
        serviceTableService.saveServiceTable(serviceTable);
        //TODO 临时方法
        try{
            Thread.sleep(500);
        }catch(Exception e){
            e.printStackTrace();
        }
        ((DefaultActorService)actorService).onMsg(msg);
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/getAll", method = RequestMethod.GET)
    @ResponseBody
    public List<ServiceTable> serviceTableList() throws ThingsboardException {
        return serviceTableService.findServiceTables();
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/services/{manufacture}/{deviceType}/{model}", method = RequestMethod.GET)
    @ResponseBody
    public String serviceTableList(@PathVariable String manufacture,@PathVariable String deviceType,@PathVariable String model) throws ThingsboardException {
        String coordinate = manufacture+"%"+deviceType+"%"+model;
        Optional<ServiceTable> table = serviceTableService.findServiceTableByCoordinate(coordinate);
        JsonObject services = new JsonParser().parse(table.get().getDescription()).getAsJsonObject();
        JsonArray res = new JsonArray();
        for(Map.Entry<String,JsonElement> entry:services.entrySet()){
            res.add(entry.getValue());
        }
        return res.toString();
    }

    private String getCoordinate(JsonObject service){
        String manufacture = service.get("manufacture").getAsString();
        String deviceType = service.get("deviceType").getAsString();
        String model = service.get("model").getAsString();
        String coordinate = manufacture+"%"+deviceType+"%"+model;
        if(StringUtil.checkNotNull(manufacture,manufacture,model)){
            return coordinate;
        }
        return null;
    }
}
