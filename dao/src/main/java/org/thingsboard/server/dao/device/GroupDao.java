package org.thingsboard.server.dao.device;

import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by CZX on 2017/12/11.
 */
public interface GroupDao  extends Dao<Group> {
    /**
     * Save or update group object
     *
     * @param group the group object
     * @return saved group object
     */
    Group save(Group group);

    /**
     * Find groups by tenant id and page link.
     *
     * @param tenantId the tenant id
     * @param pageLink the page link
     * @return the list of group objects
     */
    List<Group> findGroupsByTenantId(UUID tenantId, TextPageLink pageLink);

    /**
     * Find groups by customerId and page link.
     *
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of group object
     */
    List<Group> findGroupsByCustomerId(UUID customerId, TextPageLink pageLink) ;
}
