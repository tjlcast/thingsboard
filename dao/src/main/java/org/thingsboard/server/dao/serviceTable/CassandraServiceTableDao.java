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

import com.datastax.driver.core.querybuilder.Select;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.ServiceTable;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.nosql.ServiceTableEntity;
import org.thingsboard.server.dao.nosql.CassandraAbstractModelDao;
import org.thingsboard.server.dao.util.NoSqlDao;

import java.util.List;
import java.util.Optional;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.thingsboard.server.dao.model.ModelConstants.*;

/**
 * Created by CZX on 2017/12/25.
 */
@Component
@Slf4j
@NoSqlDao
public class CassandraServiceTableDao extends CassandraAbstractModelDao<ServiceTableEntity, ServiceTable> implements ServiceTableDao{
    @Override
    protected Class<ServiceTableEntity> getColumnFamilyClass() {
        return ServiceTableEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return ModelConstants.SERVICE_COLUMN_FAMILY_NAME;
    }

    @Override
    public Optional<ServiceTable> findServiceTableByCoordinate(String coordinate){
        Select select = select().from(SERVICE_BY_COORDINATE_COLUMN_FAMILY_NAME);
        Select.Where query = select.where();
        query.and(eq(SERVICE_COORDINATE_PROPERTY, coordinate));
        return Optional.ofNullable(DaoUtil.getData(findOneByStatement(query)));
    }

    @Override
    public List<ServiceTable> findServiceTablesByManufacture(String manufacture){
        Select select = select().from(SERVICE_BY_MANUFACTURE_COLUMN_FAMILY_NAME);
        Select.Where query = select.where();
        query.and(eq(SERVICE_MANUFACTURE_PROPERTY, manufacture));
        return DaoUtil.convertDataList(findListByStatement(query));
    }

    @Override
    public List<ServiceTable> findServiceTablesByManufactureAndDeviceType(String manufacture, String device_type){
        Select select = select().from(SERVICE_BY_MANUFACTURE_COLUMN_FAMILY_NAME);
        select.allowFiltering();
        Select.Where query = select.where();
        query.and(eq(SERVICE_MANUFACTURE_PROPERTY, manufacture));
        query.and(eq(SERVICE_DEVICE_TYPE_PROPERTY, device_type));
        return DaoUtil.convertDataList(findListByStatement(query));
    }

}
