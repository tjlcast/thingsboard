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
package org.thingsboard.server.dao.device;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.DeviceByGroupId;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.nosql.CassandraAbstractAsyncDao;
import org.thingsboard.server.dao.util.NoSqlDao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by CZX on 2018/1/9.
 */
@Component
@Slf4j
@NoSqlDao
public class CassandraDeviceByGroupIdDao extends CassandraAbstractAsyncDao implements DeviceByGroupIdDao{

    private PreparedStatement saveStmt;
    private PreparedStatement findAllByGroupIdStmt;
    private PreparedStatement findAllByDeviceIdStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement deleteAllStmt;

    @Override
    public boolean save(DeviceByGroupId deviceByGroupId){
        BoundStatement stmt = getSaveStatement(deviceByGroupId);
        ResultSet rs = executeWrite(stmt);
        return rs.wasApplied();
    }

    @Override
    public boolean delete(DeviceByGroupId deviceByGroupId){
        BoundStatement stmt = getDeleteStatement(deviceByGroupId);
        ResultSet rs = executeWrite(stmt);
        return rs.wasApplied();
    }

    @Override
    public boolean deleteAllByGroupId(UUID groupId){
        BoundStatement stmt = getDeleteAllStatement(groupId);
        ResultSet rs = executeWrite(stmt);
        return rs.wasApplied();
    }

    @Override
    public List<UUID> findDevicesByGroupId(UUID groupId){
        BoundStatement stmt = getFindAllByGroupIdStmt().bind()
                .setUUID(0, groupId);
        ResultSet resultSet = executeRead(stmt);
        return getDeviceId(resultSet);
    }

    @Override
    public List<UUID> findGroupsByDeviceId(UUID deviceId){
        BoundStatement stmt = getFindAllByDeviceIdStmt().bind()
                .setUUID(0, deviceId);
        ResultSet resultSet = executeRead(stmt);
        return getGroupId(resultSet);
    }

    private BoundStatement getSaveStatement(DeviceByGroupId deviceByGroupId) {
        BoundStatement stmt = getSaveStmt().bind()
                .setUUID(0, deviceByGroupId.getGroupId().getId())
                .setUUID(1, deviceByGroupId.getDeviceId().getId());
        return stmt;
    }

    private BoundStatement getDeleteStatement(DeviceByGroupId deviceByGroupId) {
        BoundStatement stmt = getDeleteStmt().bind()
                .setUUID(0, deviceByGroupId.getGroupId().getId())
                .setUUID(1,deviceByGroupId.getDeviceId().getId());
        return stmt;
    }

    private BoundStatement getDeleteAllStatement(UUID groupId) {
        BoundStatement stmt = getDeleteAllStmt().bind()
                .setUUID(0, groupId);
        return stmt;
    }

    private PreparedStatement getSaveStmt() {
        if (saveStmt == null) {
            saveStmt = getSession().prepare("INSERT INTO " + ModelConstants.DEVICE_BY_GROUP_ID_COLUMN_FAMILY_NAME + " " +
                    "(" + ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY +
                    "," + ModelConstants.DEVICE_BY_GROUP_ID_DEVICE_ID_PROPERTY + ")" +
                    " VALUES(?, ?)");
        }
        return saveStmt;
    }

    private PreparedStatement getFindAllByGroupIdStmt() {
        if (findAllByGroupIdStmt == null) {
            findAllByGroupIdStmt = getSession().prepare("SELECT "+ ModelConstants.DEVICE_BY_GROUP_ID_DEVICE_ID_PROPERTY +" FROM "+ ModelConstants.DEVICE_BY_GROUP_ID_COLUMN_FAMILY_NAME+ " where "
                    + ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY +" = ?" );
        }
        return findAllByGroupIdStmt;
    }

    private PreparedStatement getFindAllByDeviceIdStmt() {
        if (findAllByDeviceIdStmt == null) {
            findAllByDeviceIdStmt = getSession().prepare("SELECT "+ ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY +" FROM "+ ModelConstants.GROUP_BY_DEVICE_ID_COLUMN_FAMILY_NAME+ " where "
                    + ModelConstants.DEVICE_BY_GROUP_ID_DEVICE_ID_PROPERTY +" = ?" );
        }
        return findAllByDeviceIdStmt;
    }

    private PreparedStatement getDeleteStmt() {
        if (deleteStmt == null) {
            deleteStmt = getSession().prepare("DELETE FROM " + ModelConstants.DEVICE_BY_GROUP_ID_COLUMN_FAMILY_NAME +
                    " WHERE " + ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY + " = ?" +
                    " AND " + ModelConstants.DEVICE_BY_GROUP_ID_DEVICE_ID_PROPERTY + " = ?" );
        }
        return deleteStmt;
    }

    private PreparedStatement getDeleteAllStmt() {
        if (deleteAllStmt == null) {
            deleteAllStmt = getSession().prepare("DELETE FROM " + ModelConstants.DEVICE_BY_GROUP_ID_COLUMN_FAMILY_NAME +
                    " WHERE " + ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY + " = ?" );
        }
        return deleteAllStmt;
    }

    private List<UUID> getDeviceId(ResultSet rs) {
        List<Row> rows = rs.all();
        List<UUID> entries = new ArrayList<>(rows.size());
        if (!rows.isEmpty()) {
            rows.forEach(row -> {
                entries.add(row.getUUID(ModelConstants.DEVICE_BY_GROUP_ID_DEVICE_ID_PROPERTY));
            });
        }
        log.debug("Generated deviceId [{}] by [{}] and rowSize is [{}]", entries , rs , rows.size());
        return entries;
    }

    private List<UUID> getGroupId(ResultSet rs) {
        List<Row> rows = rs.all();
        List<UUID> entries = new ArrayList<>(rows.size());
        if (!rows.isEmpty()) {
            rows.forEach(row -> {
                entries.add(row.getUUID(ModelConstants.DEVICE_BY_GROUP_ID_GROUP_ID_PROPERTY));
            });
        }
        log.debug("Generated groupId [{}] by [{}] and rowSize is [{}]", entries , rs , rows.size());
        return entries;
    }
}
