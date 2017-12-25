package org.thingsboard.server.dao.model.nosql;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.ServiceTableId;
import org.thingsboard.server.dao.model.BaseEntity;

import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;
/**
 * Created by CZX on 2017/12/25.
 */
@Table(name = SERVICE_COLUMN_FAMILY_NAME)
@EqualsAndHashCode
@ToString
public final class ServiceTableEntity implements BaseEntity<ServiceTable>{

    @PartitionKey(value = 0)
    @Column(name = SERVICE_ID_PROPERTY)
    private UUID id;

    @PartitionKey(value = 1)
    @Column(name = SERVICE_COORDINATE_PROPERTY)
    private String coordinate;

    @Column(name = SERVICE_DESCRIPTION_PROPERTY)
    private String description;

    public ServiceTableEntity() {
        super();
    }

    public ServiceTableEntity(ServiceTable serviceTable) {
        if (serviceTable.getId() != null) {
            this.id = serviceTable.getId().getId();
        }
        this.coordinate = serviceTable.getCoordinate();
        this.description = serviceTable.getDescription();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ServiceTable toData() {
        ServiceTable service = new ServiceTable(new ServiceTableId(id));
        service.setCreatedTime(UUIDs.unixTimestamp(id));
        service.setCoordinate(coordinate);
        service.setDescription(description);
        return service;
    }
}
