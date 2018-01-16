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

import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.ServiceTableId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by CZX on 2017/12/25.
 */
public interface ServiceTableService {

    ServiceTable saveServiceTable(ServiceTable serviceTable);

    List<ServiceTable> findServiceTables();

    Optional<ServiceTable> findServiceTableByCoordinate(String coordinate);

    void deleteServiceTable(ServiceTableId serviceTableId);

    Set<String> findAllManufactures();

    Set<String> findDeviceTypesByManufacture(String manufacture);

    Set<String> findModelsByManufactureAndDeviceType(String manufacture, String device_type);
}
