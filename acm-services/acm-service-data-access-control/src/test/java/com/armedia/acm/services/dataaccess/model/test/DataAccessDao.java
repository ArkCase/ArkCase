package com.armedia.acm.services.dataaccess.model.test;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;

public class DataAccessDao extends AcmAbstractDao<AcmObject>
{
    @Override
    protected Class<AcmObject> getPersistenceClass()
    {
        return AcmObject.class;
    }
}
