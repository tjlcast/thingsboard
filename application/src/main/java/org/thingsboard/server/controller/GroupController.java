package org.thingsboard.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.device.GroupService;

/**
 * Created by Administrator on 2017/12/13.
 */
public class GroupController {
    @Autowired
    GroupService groupService;
    @Autowired
    DeviceService deviceService;


}
