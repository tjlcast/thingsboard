package org.thingsboard.server.dao.model.nosql;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.SearchTextEntity;

import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

/**
 * Created by CZX on 2017/12/11.
 */

@Table(name = GROUP_COLUMN_FAMILY_NAME)
@EqualsAndHashCode
@ToString
public final class GroupEntity implements SearchTextEntity<Group> {
    @PartitionKey(value = 0)
    @Column(name = ID_PROPERTY)
    private UUID id;

    @PartitionKey(value = 1)
    @Column(name = GROUP_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @PartitionKey(value = 2)
    @Column(name = GROUP_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Column(name = GROUP_NAME_PROPERTY)
    private String name;

    @Column(name = SEARCH_TEXT_PROPERTY)
    private String searchText;

    public GroupEntity() {
        super();
    }

    public GroupEntity(Group group) {
        if (group.getId() != null) {
            this.id = group.getId().getId();
        }
        if (group.getTenantId() != null) {
            this.tenantId = group.getTenantId().getId();
        }
        if (group.getCustomerId() != null) {
            this.customerId = group.getCustomerId().getId();
        }
        this.name = group.getName();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSearchText() {
        return searchText;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String getSearchTextSource() {
        return getName();
    }

    @Override
    public Group toData() {
        Group group = new Group(new GroupId(id));
        group.setCreatedTime(UUIDs.unixTimestamp(id));
        if (tenantId != null) {
            group.setTenantId(new TenantId(tenantId));
        }
        if (customerId != null) {
            group.setCustomerId(new CustomerId(customerId));
        }
        group.setName(name);
        return group;
    }
}
