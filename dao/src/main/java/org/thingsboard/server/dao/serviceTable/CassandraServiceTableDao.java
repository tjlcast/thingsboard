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
}
