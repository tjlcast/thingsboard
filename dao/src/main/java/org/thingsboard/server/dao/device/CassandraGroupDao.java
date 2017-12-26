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

import com.datastax.driver.core.querybuilder.Select;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.nosql.GroupEntity;
import org.thingsboard.server.dao.nosql.CassandraAbstractSearchTextDao;
import org.thingsboard.server.dao.util.NoSqlDao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.thingsboard.server.dao.model.ModelConstants.GROUP_BY_CUSTOMER_AND_NAME_COLUMN_FAMILY_NAME;
import static org.thingsboard.server.dao.model.ModelConstants.GROUP_CUSTOMER_ID_PROPERTY;
import static org.thingsboard.server.dao.model.ModelConstants.GROUP_NAME_PROPERTY;

/**
 * Created by CZX on 2017/12/11.
 */
@Component
@Slf4j
@NoSqlDao
public class CassandraGroupDao extends CassandraAbstractSearchTextDao<GroupEntity, Group> implements GroupDao{

    @Override
    protected Class<GroupEntity> getColumnFamilyClass() {
        return GroupEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return ModelConstants.GROUP_COLUMN_FAMILY_NAME;
    }

    @Override
    public List<Group> findGroupsByTenantId(UUID tenantId, TextPageLink pageLink) {
        log.debug("Try to find group by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<GroupEntity> groupEntities = findPageWithTextSearch(ModelConstants.GROUP_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(ModelConstants.GROUP_TENANT_ID_PROPERTY, tenantId)),
                pageLink);
        log.trace("Found groups [{}] by tenantId [{}] and pageLink [{}]", groupEntities, tenantId, pageLink);
        return DaoUtil.convertDataList(groupEntities);
    }

    @Override
    public List<Group> findGroupsByCustomerId(UUID customerId, TextPageLink pageLink) {
        log.debug("Try to find groups by customerId [{}] and pageLink [{}]",  customerId , pageLink);
        List<GroupEntity> groupEntities = findPageWithTextSearch(ModelConstants.GROUP_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(ModelConstants.GROUP_CUSTOMER_ID_PROPERTY, customerId)), pageLink);
        log.trace("Found groups [{}] by customerId [{}] and pageLink [{}]", groupEntities, customerId,  pageLink);
        return DaoUtil.convertDataList(groupEntities);
    }

    @Override
    public Optional<Group> findGroupByCustomerIdAndName(UUID customerId, String name){
        Select select = select().from(GROUP_BY_CUSTOMER_AND_NAME_COLUMN_FAMILY_NAME);
        Select.Where query = select.where();
        query.and(eq(GROUP_CUSTOMER_ID_PROPERTY, customerId));
        query.and(eq(GROUP_NAME_PROPERTY, name));
        return Optional.ofNullable(DaoUtil.getData(findOneByStatement(query)));
    }

}
