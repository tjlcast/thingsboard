package org.thingsboard.server.dao.serviceTable;

import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.dao.Dao;

import java.util.Optional;

/**
 * Created by CZX on 2017/12/25.
 */
public interface ServiceTableDao extends Dao<ServiceTable> {
    /**
     * Save or update service object
     *
     * @param serviceTable the service object
     * @return saved service object
     */
    ServiceTable save(ServiceTable serviceTable);

    /**
     * Find service by coordinate.
     *
     * @param coordinate the coordinate
     * @return found service
     */
    Optional<ServiceTable> findServiceTableByCoordinate(String coordinate);
}
