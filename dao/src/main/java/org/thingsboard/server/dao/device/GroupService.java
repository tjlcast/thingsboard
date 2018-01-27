package org.thingsboard.server.dao.device;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.Group;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.GroupId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;


/**
 * Created by CZX on 2017/12/11.
 */
public interface GroupService {

    Group saveGroup( Group group);

    TextPageData<Group> findGroupsByTenantId(TenantId tenantId, TextPageLink pageLink);

    ListenableFuture<Group> findGroupByIdAsync(GroupId groupId);

    TextPageData<Group> findGroupsByCustomerId(CustomerId customerId, TextPageLink pageLink) ;

    void deleteGroup(GroupId groupId);
}
