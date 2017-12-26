package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Created by CZX on 2017/12/25.
 */
public class ServiceTableId extends UUIDBased {
    @JsonCreator
    public ServiceTableId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static ServiceTableId fromString(String serviceTableId) {
        return new ServiceTableId(UUID.fromString(serviceTableId));
    }
}
