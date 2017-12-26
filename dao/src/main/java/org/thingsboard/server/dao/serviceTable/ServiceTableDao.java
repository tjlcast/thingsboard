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
