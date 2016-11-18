package com.armedia.acm.service.objecthistory.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objecthistory.model.AcmOwningGroup;

/**
 * Created by teng.wang on 11/16/2016.
 */
public class AcmOwningGroupDao extends AcmAbstractDao<AcmOwningGroup>
{
    @Override
    protected Class<AcmOwningGroup> getPersistenceClass()
    {
        return AcmOwningGroup.class;
    }
}
