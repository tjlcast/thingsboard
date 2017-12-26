package org.thingsboard.server.controller;

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
    @RequestMapping(value = "/servicetable/save", method = RequestMethod.GET)
    @ResponseBody
    public ServiceTable save(@RequestBody  String json) throws ThingsboardException {
        JsonObject service = new JsonParser().parse(json).getAsJsonObject();
        DeviceShadow.isValidService(service);
        ServiceTable serviceTable= new ServiceTable();
        String manufacture = service.get("manufacture").getAsString();
        String deviceType = service.get("deviceType").getAsString();
        String model = service.get("model").getAsString();

        String des = service.get("description").toString();
        serviceTable.setCoordinate(manufacture+"-"+deviceType+"-"+model);
        serviceTable.setDescription(des);
        serviceTable.setCreatedTime(System.currentTimeMillis());
       // serviceTable.setId(Ser);
        return serviceTableService.saveServiceTable(serviceTable);
    }
}
