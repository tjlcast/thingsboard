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

import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.GroupId;

/**
 * Created by CZX on 2018/1/9.
 */
public class DeviceByGroupId {

    private GroupId groupId;
    private DeviceId deviceId;

    public DeviceByGroupId(GroupId groupId, DeviceId deviceId) {
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    public DeviceByGroupId(DeviceByGroupId deviceByGroupId) {
        this.groupId = deviceByGroupId.getGroupId();
        this.deviceId = deviceByGroupId.getDeviceId();
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceByGroupId that = (DeviceByGroupId) o;

        if (!groupId.equals(that.groupId)) return false;
        return deviceId.equals(that.deviceId);
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + deviceId.hashCode();
        return result;
    }
}
