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
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Created by CZX on 2017/12/11.
 */

@EqualsAndHashCode(callSuper = true)
public class Group extends SearchTextBased<GroupId> implements HasName {

    private static final long serialVersionUID = 1598632990298929745L;

    private String name;
    private TenantId tenantId;
    private CustomerId customerId;

    public Group() {
        super();
    }

    public Group(GroupId id) {
        super(id);
    }

    public Group(Group group) {
        super(group);
        this.name = group.getName();
        this.tenantId = group.getTenantId();
        this.customerId = group.getCustomerId();
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @Override
    public String getSearchText() {
        return getName();
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", tenantId=" + tenantId +
                ", customerId=" + customerId +
                '}';
    }

}
