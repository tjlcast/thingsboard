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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.ServiceTableId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.thingsboard.server.dao.service.Validator.validateId;

/**
 * Created by CZX on 2017/12/25.
 */
@Service
@Slf4j
public class ServiceTableServiceImpl implements ServiceTableService{

    @Autowired
    private ServiceTableDao serviceTableDao;

    @Override
    public ServiceTable saveServiceTable(ServiceTable serviceTable){
        log.trace("Executing saveServiceTable [{}]", serviceTable);
        serviceTableDataValidator.validate(serviceTable);
        return serviceTableDao.save(serviceTable);
    }

    @Override
    public List<ServiceTable> findServiceTables() {
        log.trace("Executing findServiceTables");
        return serviceTableDao.find();
    }

    @Override
    public Set<String> findAllManufactures(){
        log.trace("Executing findAllManufactures");
        List<ServiceTable> serviceTables = serviceTableDao.find();
        Set<String> manufactures = new HashSet<>();
        for(ServiceTable serviceTable : serviceTables){
            manufactures.add(serviceTable.getManufacture());
        }
        log.trace("found Manufactures [{}]", manufactures);
        return manufactures;
    }

    @Override
    public Set<String> findDeviceTypesByManufacture(String manufacture){
        log.trace("Executing findDeviceTypesByManufacture");
        List<ServiceTable> serviceTables = serviceTableDao.findServiceTablesByManufacture(manufacture);
        Set<String> deviceTypes = new HashSet<>();
        for(ServiceTable serviceTable : serviceTables){
            deviceTypes.add(serviceTable.getDevice_type());
        }
        log.trace("found DeviceTypes [{}]", deviceTypes);
        return deviceTypes;
    }

    @Override
    public Set<String> findModelsByManufactureAndDeviceType(String manufacture, String device_type){
        log.trace("Executing findModelsByManufactureAndDeviceType");
        List<ServiceTable> serviceTables = serviceTableDao.findServiceTablesByManufactureAndDeviceType(manufacture,device_type);
        Set<String> models = new HashSet<>();
        for(ServiceTable serviceTable : serviceTables){
            models.add(serviceTable.getModel());
        }
        log.trace("found Models [{}]", models);
        return models;
    }

    @Override
    public Optional<ServiceTable> findServiceTableByCoordinate(String coordinate){
        log.trace("Executing findServiceTableByCoordinate [{}]", coordinate);
        return serviceTableDao.findServiceTableByCoordinate(coordinate);
    }

    @Override
    public void deleteServiceTable(ServiceTableId serviceTableId){
        log.trace("Executing deleteServiceTable [{}]", serviceTableId);
        validateId(serviceTableId, "Incorrect serviceTableId " + serviceTableId);
        serviceTableDao.removeById(serviceTableId.getId());
    }

    private DataValidator<ServiceTable> serviceTableDataValidator =
            new DataValidator<ServiceTable>() {

                @Override
                protected void validateCreate(ServiceTable serviceTable) {
                    serviceTableDao.findServiceTableByCoordinate(serviceTable.getCoordinate()).ifPresent(
                            c -> {
                                throw new DataValidationException("Service with such coordinate already exists!");
                            }
                    );
                }

                @Override
                protected void validateUpdate(ServiceTable serviceTable) {
                    serviceTableDao.findServiceTableByCoordinate(serviceTable.getCoordinate()).ifPresent(
                            c -> {
                                if (!c.getId().equals(serviceTable.getId())) {
                                    throw new DataValidationException("Service with such coordinate already exists!");
                                }
                            }
                    );
                }

                @Override
                protected void validateDataImpl(ServiceTable serviceTable) {
                    if (StringUtils.isEmpty(serviceTable.getCoordinate())) {
                        throw new DataValidationException("Service coordinate should be specified!");
                    }
                }
            };
}
