package org.thingsboard.server.dao.device;

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
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

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

}
