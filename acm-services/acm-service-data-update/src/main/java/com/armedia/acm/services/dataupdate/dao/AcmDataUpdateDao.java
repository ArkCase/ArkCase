package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.dataupdate.model.AcmDataUpdateExecutorLog;

public class AcmDataUpdateDao extends AcmAbstractDao<AcmDataUpdateExecutorLog>
{
    @Override
    protected Class<AcmDataUpdateExecutorLog> getPersistenceClass()
    {
        return AcmDataUpdateExecutorLog.class;
    }
}
