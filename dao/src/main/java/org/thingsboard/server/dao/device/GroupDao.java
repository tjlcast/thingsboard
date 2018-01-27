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
    //List<Group> findGroupsByCustomerId(UUID customerId, TextPageLink pageLink);
}
