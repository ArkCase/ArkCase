package com.armedia.acm.plugins.complaint.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

/**
 * Created by armdev on 10/17/14.
 */
public class CloseComplaintRequestDao extends AcmAbstractDao<CloseComplaintRequest>
{
    @Override
    protected Class<CloseComplaintRequest> getPersistenceClass()
    {
        return CloseComplaintRequest.class;
    }
}
