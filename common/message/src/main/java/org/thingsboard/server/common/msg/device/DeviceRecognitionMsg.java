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
package org.thingsboard.server.common.msg.device;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.aware.DeviceAwareMsg;
import org.thingsboard.server.common.msg.aware.TenantAwareMsg;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/10.
 */
public class DeviceRecognitionMsg implements TenantAwareMsg,DeviceAwareMsg,Serializable{

    private final String manufacture;
    private final String deviceType;
    private final String model;
    private final Device device;

    public DeviceRecognitionMsg(String manufacture, String deviceType, String model,Device device) {
        this.manufacture = manufacture;
        this.deviceType = deviceType;
        this.model = model;
        this.device = device;
    }

    @Override
    public TenantId getTenantId() {
        return device.getTenantId();
    }

    @Override
    public DeviceId getDeviceId() {
        return device.getId();
    }

    public String getManufacture() {
        return manufacture;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getModel() {
        return model;
    }
}
