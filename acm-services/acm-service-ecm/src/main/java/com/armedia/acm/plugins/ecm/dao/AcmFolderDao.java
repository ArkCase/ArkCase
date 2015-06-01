package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
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
        String jpql = "SELECT e FROM AcmFolder e WHERE e.cmisFolderId =:cmisFolderId";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFolderId", cmisFolderId);

        AcmFolder folder = query.getSingleResult();

        return folder;
    }

    public AcmFolder findFolderByNameInTheGivenParentFolder(String folderName, Long parentFolderId) throws  NoResultException {

        String jpql = "SELECT e FROM AcmFolder e WHERE e.name=:folderName AND e.parentFolderId=:parentFolderId";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("folderName",folderName);
        query.setParameter("parentFolderId",parentFolderId);

        AcmFolder folder = query.getSingleResult();

        return folder;

    }

    @Transactional
    public void deleteFolder(Long id) {
        AcmFolder folder = getEm().find(getPersistenceClass(),id);
        getEm().remove(folder);
    }
}
