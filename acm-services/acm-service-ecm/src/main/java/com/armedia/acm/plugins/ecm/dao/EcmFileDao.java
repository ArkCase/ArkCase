package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import javax.persistence.Query;
import java.util.List;

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

    public List<EcmFile> findForContainer(Long containerId)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId";
        Query query = getEm().createQuery(jpql);
        query.setParameter("containerId", containerId);

        List<EcmFile> results = query.getResultList();

        return results;
    }
}
