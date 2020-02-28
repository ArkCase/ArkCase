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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.List;

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
        String jpql = "SELECT e FROM AcmFolder e WHERE e.cmisFolderId =:cmisFolderId AND e.link = false";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFolderId", cmisFolderId);

        AcmFolder folder = query.getSingleResult();

        return folder;
    }

    public AcmFolder findFolderByNameInTheGivenParentFolder(String folderName, Long parentFolderId) throws NoResultException
    {
        AcmFolder parentFolder = find(parentFolderId);
        if (parentFolder.isLink()) {
            parentFolderId = findByCmisFolderId(parentFolder.getCmisFolderId()).getId();
        }
        String jpql = "SELECT e FROM AcmFolder e WHERE e.name=:folderName AND e.parentFolder.id = :parentFolderId";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("folderName", folderName);
        query.setParameter("parentFolderId", parentFolderId);

        AcmFolder folder = query.getSingleResult();

        return folder;

    }

    @Transactional
    public void deleteFolder(Long id)
    {
        AcmFolder folder = getEm().find(getPersistenceClass(), id);
        getEm().remove(folder);
    }

    public List<AcmFolder> findSubFolders(Long parentFolderId)
    {
        return findSubFolders(parentFolderId, FlushModeType.AUTO);
    }

    public List<AcmFolder> findSubFolders(Long parentFolderId, FlushModeType flushModeType)
    {
        String jpql = "SELECT e FROM AcmFolder e WHERE e.parentFolder.id = :parentFolderId";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("parentFolderId", parentFolderId);

        query.setFlushMode(flushModeType);

        return query.getResultList();

    }

    public List<AcmFolder> getFoldersWithoutParticipants()
    {
        String jpql = "SELECT f FROM AcmFolder f WHERE f.id  NOT IN " +
                "(SELECT p.objectId FROM AcmParticipant p WHERE p.objectType ='FOLDER')";

        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());

        return query.getResultList();
    }

    public List <AcmFolder> getFolderLinks(String cmisFolderId)
    {
        String jpql = "SELECT f FROM AcmFolder f WHERE f.cmisFolderId = :cmisFolderId AND f.link = true";
        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("cmisFolderId", cmisFolderId);
        return query.getResultList();
    }

    public AcmFolder findAnyFolderByName(String name)
    {
        String jpql = "SELECT f FROM AcmFolder f WHERE f.name = :name";
        TypedQuery<AcmFolder> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("name", name);

        List <AcmFolder> folderList = query.getResultList();
        if(!folderList.isEmpty())
        {
            return folderList.get(0);
        }
        else
        {
            return null;
        }

    }

    @Override
    public String getSupportedObjectType()
    {
        return AcmFolderConstants.OBJECT_FOLDER_TYPE;
    }
}
