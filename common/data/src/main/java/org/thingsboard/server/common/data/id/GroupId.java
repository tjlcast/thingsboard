package org.thingsboard.server.common.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.thingsboard.server.common.data.EntityType;
/**
 * Created by CZX on 2017/12/11.
 */
public class GroupId extends UUIDBased implements EntityId{

    @JsonCreator
    public GroupId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static GroupId fromString(String groupId) {
        return new GroupId(UUID.fromString(groupId));
    }

    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.GROUP;
    }
}
