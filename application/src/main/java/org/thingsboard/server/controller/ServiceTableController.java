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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.actors.device.DeviceShadow;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.dao.serviceTable.ServiceTableService;
import org.thingsboard.server.exception.ThingsboardException;
import org.thingsboard.server.transport.http.utils.StringUtil;

import java.util.List;
import java.util.Optional;


/**
 * Created by Administrator on 2017/12/26.
 */
@RestController
@RequestMapping("/api")
public class ServiceTableController extends BaseController{

    @Autowired
    ServiceTableService serviceTableService;

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
        des.add("attributes",new JsonArray());
        des.add("telemetries",new JsonArray());
        des.add("services",new JsonArray());

        serviceTable1.orElse(serviceTable).setCoordinate(coordinate);
        serviceTable1.orElse(serviceTable).setDescription(des.toString());
        serviceTable1.orElse(serviceTable).setCreatedTime(System.currentTimeMillis());
       // serviceTable.setId(Ser);
        return serviceTableService.saveServiceTable(serviceTable1.orElse(serviceTable));
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/add", method = RequestMethod.POST)
    @ResponseBody
    public ServiceTable addServiceToServiceGroup (@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        String coordinate = getCoordinate(service);

        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);

        if(!serviceTable1.isPresent()) return null;
        ServiceTable serviceTable = serviceTable1.get();
        JsonObject desc = service.get("description").getAsJsonObject();
        JsonObject obj = new JsonParser().parse(serviceTable.getDescription()).getAsJsonObject();
        obj.get("services").getAsJsonArray().add(desc);
        serviceTable.setDescription(obj .toString());
        return serviceTableService.saveServiceTable(serviceTable);
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
        return new JsonParser().parse(table.get().getDescription()).getAsJsonObject().get("services").toString();
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
