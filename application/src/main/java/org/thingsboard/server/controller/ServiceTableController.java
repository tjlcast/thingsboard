package org.thingsboard.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.actors.device.DeviceShadow;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.RuleId;
import org.thingsboard.server.common.data.rule.RuleMetaData;
import org.thingsboard.server.dao.serviceTable.ServiceTableService;
import org.thingsboard.server.exception.ThingsboardException;

import java.util.List;
import java.util.Optional;

import static org.thingsboard.server.controller.RuleController.RULE_ID;

/**
 * Created by Administrator on 2017/12/26.
 */
@RestController
@RequestMapping("/api")
public class ServiceTableController extends BaseController{

    @Autowired
    ServiceTableService serviceTableService;

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/save", method = RequestMethod.POST)
    @ResponseBody
    public ServiceTable save(@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        ServiceTable serviceTable= new ServiceTable();
        String manufacture = service.get("manufacture").getAsString();
        String deviceType = service.get("deviceType").getAsString();
        String model = service.get("model").getAsString();
        String coordinate = manufacture+"-"+deviceType+"-"+model;

        Optional<ServiceTable> serviceTable1=  serviceTableService.findServiceTableByCoordinate(coordinate);


        JsonArray array = service.get("description").getAsJsonArray();
        JsonObject des = new JsonObject();
        des.add("attributes",new JsonArray());
        des.add("telemetries",new JsonArray());
        des.add("services",array);

        serviceTable1.orElse(serviceTable).setCoordinate(coordinate);
        serviceTable1.orElse(serviceTable).setDescription(des.toString());
        serviceTable1.orElse(serviceTable).setCreatedTime(System.currentTimeMillis());
       // serviceTable.setId(Ser);
        return serviceTableService.saveServiceTable(serviceTable1.orElse(serviceTable));
    }

    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN')")
    @RequestMapping(value = "/servicetable/getAll", method = RequestMethod.GET)
    @ResponseBody
    public List<ServiceTable> serviceTableList() throws ThingsboardException {
        return serviceTableService.findServiceTables();
    }
}
