package org.thingsboard.server.dao.device;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.tenant.TenantDao;

import java.util.List;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
import static org.thingsboard.server.dao.service.Validator.validateId;

/**
 * Created by CZX on 2017/12/11.
 */

@Service
@Slf4j
public class GroupServiceImpl extends AbstractEntityService implements GroupService{

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DeviceService deviceService;

    @Override
    public Group saveGroup(Group group) {
        log.trace("Executing saveGroup [{}]", group);
        groupValidator.validate(group);
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

    //删除设备组，并且unassign设备组下的所有设备。
    @Override
    public void deleteGroup(GroupId groupId) {
        log.trace("Executing deleteGroup [{}]", groupId);
        validateId(groupId, "Incorrect groupId " + groupId);
        deviceService.unassignDevicesByGroupId(groupId);
        groupDao.removeById(groupId.getId());
    }

    private DataValidator<Group> groupValidator =
            new DataValidator<Group>() {

                @Override
                protected void validateCreate(Group group) {
                    groupDao.findGroupByCustomerIdAndName(group.getCustomerId().getId(),  group.getName()).ifPresent(
                            c -> {
                                throw new DataValidationException("Group with such name already exists!");
                            }
                    );
                }

                @Override
                protected void validateUpdate(Group group) {
                    groupDao.findGroupByCustomerIdAndName(group.getCustomerId().getId(),  group.getName()).ifPresent(
                            c -> {
                                if (!c.getId().equals(group.getId())) {
                                    throw new DataValidationException("Group with such name already exists!");
                                }
                            }
                    );
                }

                @Override
                protected void validateDataImpl(Group group) {
                    if (StringUtils.isEmpty(group.getName())) {
                        throw new DataValidationException("Group name should be specified!");
                    }
                    if (group.getTenantId() == null) {
                        throw new DataValidationException("Group should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(group.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Group is referencing to non-existent tenant!");
                        }
                    }
                    if (group.getCustomerId() == null) {
                        group.setCustomerId(new CustomerId(NULL_UUID));
                    } else if (!group.getCustomerId().getId().equals(NULL_UUID)) {
                        Customer customer = customerDao.findById(group.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign group to non-existent customer!");
                        }
                        if (!customer.getTenantId().getId().equals(group.getTenantId().getId())) {
                            throw new DataValidationException("Can't assign group to customer from different tenant!");
                        }
                    }
                }
            };

}
