package org.thingsboard.server.actors.tenant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.thingsboard.server.common.data.id.TenantId;

/**
 * Created by Administrator on 2018/1/8.
 */
@AllArgsConstructor
public class ServiceGroupUpdateMsg {
    @Getter @Setter
    private TenantId tenantId;
    @Getter @Setter
    private String manufacture;//厂商
    @Getter @Setter
    private String deviceType;//设备
    @Getter @Setter
    private String model;//型号

}
