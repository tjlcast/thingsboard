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
