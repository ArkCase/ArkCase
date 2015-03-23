package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

/**
 * Created by armdev on 3/20/15.
 */
@Repository
public class AcmFolderDao extends AcmAbstractDao<AcmFolder>
{
    @Override
    protected Class<AcmFolder> getPersistenceClass()
    {
        return AcmFolder.class;
    }

    public AcmFolder findByCmisFolderId(String cmisFolderId)
    {
        String jpql = "SELECT e FROM AcmFolder e WHERE e.cmisFolderId = :cmisFolderId";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFolderId", cmisFolderId);

        AcmFolder folder = query.getSingleResult();

        return folder;
    }
}
