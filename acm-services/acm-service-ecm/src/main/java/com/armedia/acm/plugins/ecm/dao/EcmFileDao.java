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
import com.armedia.acm.plugins.ecm.exception.EcmFileLinkException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.LinkTargetFileDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileDao extends AcmAbstractDao<EcmFile>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<EcmFile> getPersistenceClass()
    {
        return EcmFile.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return EcmFileConstants.OBJECT_FILE_TYPE;
    }

    public List<EcmFile> findForContainer(Long containerId)
    {
        return findForContainer(containerId, FlushModeType.AUTO);
    }

    public List<EcmFile> findForContainer(Long containerId, FlushModeType flushModeType)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId";
        Query query = getEm().createQuery(jpql);
        query.setParameter("containerId", containerId);
        query.setFlushMode(flushModeType);

        List<EcmFile> results = query.getResultList();

        return results;
    }

    public int changeContainer(AcmContainer containerFrom, AcmContainer containerTo, List<String> excludeDocumentTypes)
    {
        if (excludeDocumentTypes == null)
            excludeDocumentTypes = new LinkedList<>();
        String jpql;
        if (excludeDocumentTypes.isEmpty())
        {
            jpql = "UPDATE EcmFile e SET e.container=:containerTo, e.modified=:modifiedDate " +
                    "WHERE e.container = :containerFrom";
        }
        else
        {
            jpql = "UPDATE EcmFile e SET e.container=:containerTo, e.modified=:modifiedDate " +
                    "WHERE e.container = :containerFrom" +
                    " AND e.fileType NOT IN :fileTypes";
        }
        Query query = getEm().createQuery(jpql);
        query.setParameter("containerFrom", containerFrom);
        query.setParameter("containerTo", containerTo);
        query.setParameter("modifiedDate", new Date());
        if (!excludeDocumentTypes.isEmpty())
            query.setParameter("fileTypes", excludeDocumentTypes);

        int retval = query.executeUpdate();

        return retval;
    }

    public List<EcmFile> findForContainerAndFileType(Long containerId, String fileType)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId " +
                "AND e.fileType = :fileType " +
                "ORDER BY e.created ASC";

        Query query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("containerId", containerId);
        query.setParameter("fileType", fileType);

        List<EcmFile> result = null;

        try
        {
            result = query.getResultList();
        }
        catch (NoResultException e)
        {
            LOG.debug("Cannot find EcmFile for containerId=[{}] and fileType=[{}]", containerId, fileType, e);
        }

        return result;
    }

    public EcmFile findForContainerAttachmentFolderAndFileType(Long containerId, Long folderId, String fileType)
    {
        String jpql = "SELECT e " +
                "FROM EcmFile e " +
                "WHERE e.container.id = :containerId " +
                "AND e.container.attachmentFolder.id = :folderId " +
                "AND e.fileType = :fileType";

        return executeJpqlForContainerIdFolderIdAndFileType(jpql, containerId, folderId, fileType);
    }

    private EcmFile executeJpqlForContainerIdFolderIdAndFileType(String jpql, Long containerId, Long folderId, String fileType)
    {
        Query query = getEm().createQuery(jpql);

        query.setParameter("containerId", containerId);
        query.setParameter("folderId", folderId);
        query.setParameter("fileType", fileType);

        EcmFile result = null;

        try
        {
            result = (EcmFile) query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.debug("Cannot find EcmFile for containerId=[{}], folderId=[{}] and fileType=[{}]. {}", containerId, folderId, fileType,
                    e.getMessage());
        }
        catch (NonUniqueResultException e1)
        {
            LOG.error("Cannot find unique EcmFile for containerId=[{}], folderId=[{}] and fileType=[{}]. Multiple files found ...",
                    containerId, folderId, fileType, e1);
        }

        return result;
    }

    public EcmFile findByCmisFileIdAndFolderId(String cmisFileId, Long folderId)
    {

        String jpql = "SELECT e FROM EcmFile e WHERE e.versionSeriesId = :cmisFileId and e.folder.id=:folderId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFileId", cmisFileId);
        query.setParameter("folderId", folderId);

        EcmFile file = query.getSingleResult();

        return file;
    }

    /**
     * Returns the single EcmFile that exist in selected folder. If none or more than one file exists, null value is
     * returned.
     *
     * @param parentObjectType
     *            type of parent object (ex. CASE_FILE)
     * @param parentObjectId
     *            id of parent object (ex. 101)
     * @param targetFolderCmisId
     *            cmisId string of selected or targeted folder (ex.
     *            workspace://SpacesStore/a9715212-d6df-4dbf-933c-111cf31a5c8c)
     * @param fileType
     *            type of template file used for correspondence (ex. Denial Letter, Request Form, etc.)
     * @return returns the file for selected criteria
     */
    public EcmFile findSingleFileByParentObjectAndFolderCmisIdAndFileType(String parentObjectType, Long parentObjectId,
            String targetFolderCmisId,
            String fileType)
    {

        String jpql = "SELECT e FROM EcmFile e " +
                "WHERE e.container.containerObjectType = :parentObjectType " +
                "AND e.container.containerObjectId = :parentObjectId " +
                "AND e.folder.cmisFolderId = :targetFolderCmisId " +
                "AND e.fileType = :fileType";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("parentObjectType", parentObjectType);
        query.setParameter("parentObjectId", parentObjectId);
        query.setParameter("targetFolderCmisId", targetFolderCmisId);
        query.setParameter("fileType", fileType);

        EcmFile file = null;

        try
        {
            file = query.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.debug(
                    "Cannot find single EcmFile for containerObjectType=[{}], containerObjectId=[{}], cmisRepositoryId=[{}] and fileType=[{}]",
                    parentObjectType, parentObjectId, targetFolderCmisId, fileType);
        }

        return file;
    }

    public List<EcmFile> findByCmisFileId(String cmisFileId)
    {
        String jpql = "SELECT e FROM EcmFile e WHERE e.versionSeriesId = :cmisFileId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("cmisFileId", cmisFileId);

        return query.getResultList();
    }

    @Transactional
    public void deleteFile(Long id)
    {
        EcmFile file = getEm().find(getPersistenceClass(), id);
        getEm().remove(file);
    }

    public List<EcmFile> findByFolderId(Long folderId)
    {
        return findByFolderId(folderId, FlushModeType.AUTO);
    }

    public List<EcmFile> findByFolderId(Long folderId, FlushModeType flushModeType)
    {
        String jpql = "SELECT e FROM EcmFile e WHERE e.folder.id=:folderId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        query.setParameter("folderId", folderId);

        query.setFlushMode(flushModeType);

        return query.getResultList();

    }

    public List<EcmFile> getFilesWithoutParticipants()
    {
        String jpql = "SELECT e FROM EcmFile e WHERE e.fileId  NOT IN " +
                "(SELECT p.objectId FROM AcmParticipant p WHERE p.objectType ='FILE')";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());

        return query.getResultList();
    }

    public Long getFilesCount(LocalDateTime createdUntil)
    {
        String queryText = "SELECT COUNT(ecmFile) FROM EcmFile ecmFile WHERE ecmFile.created < :until";

        Query query = getEm().createQuery(queryText);
        query.setParameter("until", Date.from(ZonedDateTime.of(createdUntil, ZoneId.systemDefault()).toInstant()));
        return (Long) query.getSingleResult();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<EcmFile> findByIds(List<Long> fileIds)
    {
        TypedQuery<EcmFile> allRecords = getEm().createQuery(
                "SELECT e FROM " + getPersistenceClass().getSimpleName() + " e WHERE e.fileId IN :ids",
                getPersistenceClass());
        allRecords.setParameter("ids", fileIds);
        List<EcmFile> retval = allRecords.getResultList();
        return retval;
    }

    public List<EcmFile> getFileLinks(String versionSeriesId)
    {
        String jpql = "SELECT e FROM EcmFile e WHERE e.link=true AND e.versionSeriesId =:versionSeriesId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("versionSeriesId", versionSeriesId);
        return query.getResultList();
    }

    public EcmFile getFileLinkInCurrentDirectory(String versionSeriesId, Long folderId)
    {
        String jpql = "SELECT e FROM EcmFile e WHERE e.link=true AND e.versionSeriesId =:versionSeriesId AND e.folder.id =:folderId";

        TypedQuery<EcmFile> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("versionSeriesId", versionSeriesId);
        query.setParameter("folderId", folderId);
        try
        {
            return query.getSingleResult();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Transactional
    public void deleteFileAndHisLinks(String versionSeriesId)
    {
        String jpql = "DELETE FROM EcmFile e WHERE e.link=true AND e.versionSeriesId =:versionSeriesId";

        Query deleteQuery = getEm().createQuery(jpql);
        deleteQuery.setParameter("versionSeriesId", versionSeriesId);
        deleteQuery.executeUpdate();
    }

    public LinkTargetFileDTO getLinkTargetFileInfo(EcmFile ecmFile) throws EcmFileLinkException
    {
        String queryText = "SELECT ecmFile.fileId, container.containerObjectType, container.containerObjectId " +
                "FROM EcmFile ecmFile JOIN " +
                "AcmContainer container ON container.id = ecmFile.container.id " +
                "WHERE ecmFile.versionSeriesId = :versionSeriesId " +
                "AND ecmFile.link <> TRUE";

        TypedQuery<Object[]> query = getEm().createQuery(queryText, Object[].class);
        query.setParameter("versionSeriesId", ecmFile.getVersionSeriesId());

        LinkTargetFileDTO linkTargetFileDTO = new LinkTargetFileDTO();
        try
        {
            Object[] result = query.getSingleResult();
            linkTargetFileDTO.setOriginalFileId((Long) result[0]);
            linkTargetFileDTO.setParentObjectType((String) result[1]);
            linkTargetFileDTO.setParentObjectId((Long) result[2]);

        }
        catch (NoResultException e)
        {
            LOG.debug("Cannot find target EcmFile for linked document with id: [{}]", ecmFile.getId());
            throw new EcmFileLinkException("Cannot find target EcmFile for linked document with id: [{}]" + ecmFile.getId());

        }
        return linkTargetFileDTO;
    }

}
