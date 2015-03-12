package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;

/**
 * Created by armdev on 3/11/15.
 */
public class AcmContainerFolderDao extends AcmAbstractDao<AcmContainerFolder>
{
    @Override
    protected Class<AcmContainerFolder> getPersistenceClass()
    {
        return AcmContainerFolder.class;
    }
}
