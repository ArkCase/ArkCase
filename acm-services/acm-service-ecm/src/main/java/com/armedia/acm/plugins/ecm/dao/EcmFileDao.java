package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileDao extends AcmAbstractDao<EcmFile>
{
    @Override
    protected Class<EcmFile> getPersistenceClass()
    {
        return EcmFile.class;
    }
}
