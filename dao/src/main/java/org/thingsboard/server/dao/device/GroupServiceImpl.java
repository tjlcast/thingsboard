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

    @Override
    public Group saveGroup(Group group) {
        log.trace("Executing saveCustomer [{}]", group);
        return groupDao.save(group);
    }

    @Override
    public TextPageData<Group> findGroupsByTenantId(TenantId tenantId, TextPageLink pageLink) {
        log.trace("Executing findGroupsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        Validator.validateId(tenantId, "Incorrect tenantId " + tenantId);
        Validator.validatePageLink(pageLink, "Incorrect page link " + pageLink);
        List<Group> groups = groupDao.findGroupsByTenantId(tenantId.getId(), pageLink);
        return new TextPageData<>(groups, pageLink);
    }

    @Override
    public ListenableFuture<Group> findGroupByIdAsync(GroupId groupId) {
        log.trace("Executing findGroupById [{}]", groupId);
        validateId(groupId, "INCORRECT_GROUP_ID" + groupId);
        return groupDao.findByIdAsync(groupId.getId());
    }
}