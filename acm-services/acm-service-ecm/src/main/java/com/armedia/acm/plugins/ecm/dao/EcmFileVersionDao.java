package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import javax.persistence.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

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

    public Long getTotalSizeOfFiles(LocalDateTime until)
    {
        String queryText = "SELECT SUM(fileVersion.fileSizeBytes) FROM EcmFileVersion fileVersion WHERE fileVersion.created <= :until";
        Query query = getEm().createQuery(queryText);
        query.setParameter("until", Date.from(ZonedDateTime.of(until, ZoneId.systemDefault()).toInstant()));
        return (Long) query.getSingleResult();

    }
}
