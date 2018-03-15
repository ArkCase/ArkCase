package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class EcmFileVersionDao extends AcmAbstractDao<EcmFileVersion>
{
    @Override
    protected Class<EcmFileVersion> getPersistenceClass()
    {
        return EcmFileVersion.class;
    }
}
