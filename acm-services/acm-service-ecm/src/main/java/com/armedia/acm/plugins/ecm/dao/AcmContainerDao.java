package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by armdev on 3/11/15.
 */
public class AcmContainerDao extends AcmAbstractDao<AcmContainer>
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public AcmContainer findFolderByObjectTypeAndId(String objectType, Long objectId) throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        try
        {
            AcmContainer found = query.getSingleResult();
            log.debug("Found existing container '{}' for object '{}' with id '{}'", found.getId(), objectType, objectId);
            return found;
        } catch (NoResultException e)
        {
            throw new AcmObjectNotFoundException(objectType, objectId, e.getMessage(), e);
        }
    }

    public AcmContainer findFolderByObjectTypeIdAndRepositoryId(String objectType, Long objectId, String cmisRepositoryId) throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CMIS_CONTAINER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        query.setParameter("cmisRepositoryId", cmisRepositoryId);

        try
        {
            AcmContainer found = query.getSingleResult();
            log.debug("Found existing container '{}' for object '{}' with id '{}' CMIS repo '{}'", found.getId(), objectType, objectId, cmisRepositoryId);
            return found;
        } catch (NoResultException e)
        {
            throw new AcmObjectNotFoundException(objectType, objectId, e.getMessage(), e);
        }
    }

    public List<AcmContainer> findFoldersByObjectTypeAndIds(String objectType, List<Long> objectIds)
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINERS_QUERY, getPersistenceClass());

        query.setParameter("objectIds", objectIds);
        query.setParameter("objectType", objectType);

        return query.getResultList();
    }

    public AcmContainer findByObjectTypeAndIdOrCreate(String objectType, Long objectId, String name, String title)
    {
        AcmContainer container = null;
        try
        {
            container = findFolderByObjectTypeAndId(objectType, objectId);
            log.info("Found existing folder " + container.getId() + " for object " + objectType + " id " + objectId);
        } catch (AcmObjectNotFoundException e)
        {
            log.debug("Container for object " + objectType + " id " + objectId + " is not found. The new object will be created.");

            container = new AcmContainer();
            container.setContainerObjectType(objectType);
            container.setContainerObjectTitle(title);
        }

        if (container.getFolder() == null)
        {
            name = name != null ? name : EcmFileConstants.CONTAINER_FOLDER_NAME;
            AcmFolder folder = new AcmFolder();
            folder.setName(name);

            container.setFolder(folder);
            container.setAttachmentFolder(folder);
        }

        return container;
    }

    public List<AcmContainer> findByCalendarFolderId(String folderId)
    {

        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINER_BY_CALENDAR_FOLDER_QUERY,
                getPersistenceClass());
        query.setParameter("folderId", folderId);

        List<AcmContainer> resultList = query.getResultList();
        return resultList;
    }

    public AcmContainer findByFolderId(Long folderId) throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINER_QUERY_BY_FOLDER_ID, getPersistenceClass());

        query.setParameter("folderId", folderId);
        try
        {
            AcmContainer found = query.getSingleResult();
            log.info("Found existing folder with folderId {}", folderId);
            return found;
        } catch (NoResultException e)
        {
            throw new AcmObjectNotFoundException(null, folderId, e.getMessage(), e);
        }
    }

    @Override
    protected Class<AcmContainer> getPersistenceClass()
    {
        return AcmContainer.class;
    }

    @Transactional
    public void delete(Long id)
    {
        AcmContainer container = getEm().find(getPersistenceClass(), id);
        getEm().remove(container);
    }
}
