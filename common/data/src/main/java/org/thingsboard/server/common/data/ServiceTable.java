package org.thingsboard.server.common.data;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.ServiceTableId;

/**
 * Created by CZX on 2017/12/25.
 */
@EqualsAndHashCode(callSuper = true)
public class ServiceTable extends BaseData<ServiceTableId> {

    private static final long serialVersionUID = 1598123490298929745L;

    private String coordinate;
    private String description;

    public ServiceTable() {
        super();
    }

    public ServiceTable(ServiceTableId id) {
        super(id);
    }

    public ServiceTable(ServiceTable serviceTable) {
        super(serviceTable);
        this.coordinate = serviceTable.getCoordinate();
        this.description = serviceTable.getDescription();
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
    public String toString() {
        return "ServiceTable{" +
                "coordinate='" + coordinate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
