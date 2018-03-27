package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import javax.persistence.Query;

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

    public Long getTotalSizeOfFiles()
    {
        String queryText = "SELECT SUM(fileVersion.fileSizeBytes) FROM EcmFileVersion fileVersion";
        Query query = getEm().createQuery(queryText);
        return (Long) query.getSingleResult();

    }
}
