package com.armedia.acm.plugins.ecm.dao;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by armdev on 3/11/15.
 */
public class AcmContainerDao extends AcmAbstractDao<AcmContainer>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;

    public AcmContainer findFolderByObjectTypeAndId(String objectType, Long objectId, FlushModeType flushModeType)
            throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);

        try
        {
            AcmContainer found = query.getSingleResult();
            log.debug("Found existing container '{}' for object '{}' with id '{}'", found.getId(), objectType, objectId);
            return found;
        }
        catch (NoResultException e)
        {
            throw new AcmObjectNotFoundException(objectType, objectId, e.getMessage(), e);
        }
    }

    public AcmContainer findFolderByObjectTypeAndId(String objectType, Long objectId) throws AcmObjectNotFoundException
    {
        return findFolderByObjectTypeAndId(objectType, objectId, FlushModeType.AUTO);
    }

    public AcmContainer findFolderByObjectTypeIdAndRepositoryId(String objectType, Long objectId, String cmisRepositoryId)
            throws AcmObjectNotFoundException
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CMIS_CONTAINER_QUERY, getPersistenceClass());

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        query.setParameter("cmisRepositoryId", cmisRepositoryId);

        try
        {
            AcmContainer found = query.getSingleResult();
            log.debug("Found existing container '{}' for object '{}' with id '{}' CMIS repo '{}'", found.getId(), objectType, objectId,
                    cmisRepositoryId);
            return found;
        }
        catch (NoResultException e)
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
            container = findFolderByObjectTypeAndId(objectType, objectId, FlushModeType.COMMIT);
            log.info("Found existing folder " + container.getId() + " for object " + objectType + " id " + objectId);
        }
        catch (AcmObjectNotFoundException e)
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
            folder.setParticipants(getFileParticipantService().getFolderParticipantsFromParentAssignedObject(objectType, objectId));

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
        }
        catch (NoResultException e)
        {
            throw new AcmObjectNotFoundException(null, folderId, e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmContainer findByFolderIdTransactionIndependent(Long folderId) throws AcmObjectNotFoundException
    {
        return findByFolderId(folderId);
    }

    public List<AcmContainer> findByObjectType(String objectType)
    {
        TypedQuery<AcmContainer> query = getEm().createQuery(EcmFileConstants.FIND_CONTAINERS_QUERY_BY_OBJECT_TYPE, getPersistenceClass());

        query.setParameter("objectType", objectType);

        return query.getResultList();
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

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
