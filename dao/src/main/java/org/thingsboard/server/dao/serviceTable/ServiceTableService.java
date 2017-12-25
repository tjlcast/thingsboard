package org.thingsboard.server.dao.serviceTable;

import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.ServiceTableId;

import java.util.Optional;

/**
 * Created by CZX on 2017/12/25.
 */
public interface ServiceTableService {

    ServiceTable saveServiceTable(ServiceTable serviceTable);

    Optional<ServiceTable> findServiceTableByCoordinate(String coordinate);

    void deleteServiceTable(ServiceTableId serviceTableId);
}
