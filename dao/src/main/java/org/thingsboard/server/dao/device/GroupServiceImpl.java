package org.thingsboard.server.dao.device;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.service.Validator;

import java.util.List;

import static org.thingsboard.server.dao.service.Validator.validateId;

/**
 * Created by CZX on 2017/12/11.
 */

@Service
@Slf4j
public class GroupServiceImpl extends AbstractEntityService implements GroupService{

    @Autowired
    private GroupDao groupDao;

    @Override
    public Group saveGroup(Group group) {
        log.trace("Executing saveCustomer [{}]", group);
        return groupDao.save(group);
    }

    //查找tenant下的所有设备组信息（包括从属与tenant的customer下的设备组信息）
    @Override
    public TextPageData<Group> findGroupsByTenantId(TenantId tenantId, TextPageLink pageLink) {
        log.trace("Executing findGroupsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<Group> groups = groupDao.findGroupsByTenantId(tenantId.getId(), pageLink);
        return new TextPageData<>(groups, pageLink);
    }

    //查找某个customer下的所有设备组信息
    @Override
    public TextPageData<Group> findGroupsByCustomerId(CustomerId customerId, TextPageLink pageLink) {
        log.trace("Executing findGroupsByCustomerId, customerId [{}], pageLink [{}]", customerId, pageLink);
        Validator.validateId(customerId, "Incorrect customerId " + customerId);
        Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<Group> groups = groupDao.findGroupsByCustomerId(customerId.getId(), pageLink);
        return new TextPageData<>(groups, pageLink);
    }

    @Override
    public ListenableFuture<Group> findGroupByIdAsync(GroupId groupId) {
        log.trace("Executing findGroupById [{}]", groupId);
        validateId(groupId, "INCORRECT_GROUP_ID" + groupId);
        return groupDao.findByIdAsync(groupId.getId());
    }

    @Override
    public void deleteGroup(GroupId groupId) {
        log.trace("Executing deleteGroup [{}]", groupId);
        validateId(groupId, "Incorrect groupId " + groupId);
        groupDao.removeById(groupId.getId());
        /**
         * TODO:处理被删除组中的设备
         */
    }
}
