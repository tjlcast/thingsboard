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
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;

import com.fasterxml.jackson.databind.JsonNode;

@EqualsAndHashCode(callSuper = true)
public class Device extends SearchTextBased<DeviceId> implements HasName {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String name;
    private String type;
    private String manufacture;//厂商
    private String deviceType;//设备
    private String model;//型号
    private String parentDeviceId;//父设备id
    private String method; //通信方式
    private String status; //运行状态
    private transient JsonNode additionalInfo;

    public Device() {
        super();
    }

    public Device(DeviceId id) {
        super(id);
    }

    public Device(Device device) {
        super(device);
        this.tenantId = device.getTenantId();
        this.customerId = device.getCustomerId();
        this.name = device.getName();
        this.type = device.getType();
        this.additionalInfo = device.getAdditionalInfo();
        this.manufacture = device.getManufacture();
        this.model = device.getModel();
        this.parentDeviceId = device.getParentDeviceId();
        this.deviceType = device.getDeviceType();
        this.method = device.getMethod();
        this.status = device.getStatus();
    }


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(JsonNode additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(String parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getSearchText() {
        return getName();
    }

    @Override
    public String toString() {
        return "Device{" +
                "tenantId=" + tenantId +
                ", customerId=" + customerId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", manufacture='" + manufacture + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", model='" + model + '\'' +
                ", parentDeviceId='" + parentDeviceId + '\'' +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
