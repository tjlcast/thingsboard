package org.thingsboard.server.dao.serviceTable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.common.data.id.ServiceTableId;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.DataValidator;

import java.util.Optional;

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
