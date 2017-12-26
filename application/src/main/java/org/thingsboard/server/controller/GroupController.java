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

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.device.GroupService;
import org.thingsboard.server.exception.ThingsboardException;

import java.util.UUID;

/**
 * Created by Administrator on 2017/12/13.
 */

@RestController
@RequestMapping("/api/group")
public class GroupController extends BaseController{
    @Autowired
    GroupService groupService;
    @Autowired
    DeviceService deviceService;

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Group ceateGroup(@RequestBody String json) throws ThingsboardException {

        TenantId tId = getCurrentUser().getTenantId();
        CustomerId cId = getCurrentUser().getCustomerId();
        JsonObject job = new JsonParser().parse(json).getAsJsonObject();
        Group group = new Group(GroupId.fromString(UUIDs.timeBased().toString()));
        group.setCustomerId(cId);
        group.setTenantId(tId);
        group.setName(job.get("groupName").getAsString());
        groupService.saveGroup(group);
        return group;
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/delete/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public Group deleteGroup(@PathVariable("groupId") String gId) throws Exception {
        GroupId groupId =  GroupId.fromString(gId);
        ListenableFuture<Group> fu = groupService.findGroupByIdAsync(groupId);
        Group group = fu.get();
        groupService.deleteGroup(groupId);
        return group;
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Group> listGroups(@RequestParam int limit,
                                          @RequestParam(required = false) String textSearch,
                                          @RequestParam(required = false) String idOffset,
                                          @RequestParam(required = false) String textOffset) throws Exception {
        //TenantId tenantId =  new TenantId(UUID.fromString(tId));
        TenantId tenantId = getCurrentUser().getTenantId();
        TextPageLink link = createPageLink(limit,textSearch,idOffset,textOffset);

        return groupService.findGroupsByTenantId(tenantId,link);
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/device/{deviceId}/group/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public Device assignDeviceToGroup(@PathVariable("deviceId") String dId,@PathVariable("groupId") String gId) throws Exception {
        DeviceId deviceId = DeviceId.fromString(dId);
        GroupId groupId = GroupId.fromString(gId);
        Device device = deviceService.assignDeviceToGroup(deviceId,groupId);
        return device;
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/{groupId}/devices", method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Device>  getDevicesByGroupId(@PathVariable("groupId") String gId,@RequestParam int limit,
                                      @RequestParam(required = false) String textSearch,
                                      @RequestParam(required = false) String idOffset,
                                      @RequestParam(required = false) String textOffset) throws Exception {
        GroupId groupId = GroupId.fromString(gId);
        TextPageLink link = createPageLink(limit,textSearch,idOffset,textOffset);
        return deviceService.findDevicesByGroupId(groupId,link);
    }
}
