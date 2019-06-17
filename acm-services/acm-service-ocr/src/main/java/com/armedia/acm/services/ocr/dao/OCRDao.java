package com.armedia.acm.services.ocr.dao;

/*-
 * #%L
 * ACM Service: OCR
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

import com.armedia.acm.services.mediaengine.dao.MediaEngineDao;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.ocr.model.OCR;
import com.armedia.acm.services.ocr.model.OCRConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRDao extends MediaEngineDao<OCR>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    public OCR findByMediaVersionId(Long ecmFileVersionId) throws GetMediaEngineException
    {
        String queryString = "SELECT o FROM OCR o WHERE o.mediaEcmFileVersion.id=:mediaVersionId";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("mediaVersionId", ecmFileVersionId);

        String reason = "";

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no OCR for MEDIA_VERSION_ID=[{}]. REASON=[{}]", ecmFileVersionId, e.getMessage());
            return null;
        }
        catch (NonUniqueResultException e)
        {
            reason = String.format(
                    "There is no unique OCR found with MEDIA_VERSION_ID=[%d]. More than one OCR has the same media version.",
                    ecmFileVersionId);
            LOG.warn(reason);
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving OCR with MEDIA_VERSION_ID=[%d]", ecmFileVersionId);
            LOG.error(reason, e);
        }

        throw new GetMediaEngineException(
                String.format("OCR for MEDIA_VERSION_ID=[%d] was not retrieved successfully. REASON=[%s]", ecmFileVersionId,
                        reason));
    }

    @Override
    public List<OCR> findAllByStatus(String status) throws GetMediaEngineException
    {
        String queryString = "SELECT o FROM OCR o WHERE o.status=:status";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("status", status);

        try
        {
            return query.getResultList();
        }
        catch (Exception e)
        {
            throw new GetMediaEngineException(String
                    .format("OCR objects with STATUS=[%s] was not retrieved successfully. REASON=[%s]", status, e.getMessage()));
        }
    }

    /**
     *
     * @param fileId
     * @returs the last modified OCR object for given fileId. We need the last one, because the file can have more then
     *         one OCR objects associated with it. The last is the active one or recently finished OCR process.
     * @throws GetMediaEngineException
     */
    public OCR findByFileId(Long fileId) throws GetMediaEngineException
    {
        String queryString = "SELECT o FROM OCR o WHERE o.mediaEcmFileVersion.id IN " +
                "(SELECT version.id from EcmFileVersion version WHERE version.file.fileId=:fileId)" +
                "ORDER BY o.modified desc ";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("fileId", fileId);

        String reason;

        try
        {
            List<OCR> resultList = query.getResultList();
            if (!resultList.isEmpty())
            {
                return resultList.get(0);
            }
            else
                throw new NoResultException();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no OCR for FILE_ID=[{}]. REASON=[{}]", fileId, e.getMessage());
            return null;
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving OCR with FILE_ID=[%d]", fileId);
            LOG.error(reason, e);
        }
        throw new GetMediaEngineException(
                String.format("OCR for FILE_ID=[%d] was not retrieved successfully. REASON=[%s]", fileId,
                        reason));
    }

    /**
     *
     * @param fileId
     * @param statusType
     *            ocr process status
     * @return QUEUED OCR process, if there is none return null so new ocr process will be created. We need this that so
     *         we can update existing process from queue instead of creating new one for every new file version.
     * @throws GetMediaEngineException
     */
    public OCR findByFileIdAndStatus(Long fileId, MediaEngineStatusType statusType) throws GetMediaEngineException
    {
        String queryString = "SELECT o FROM OCR o WHERE o.mediaEcmFileVersion.id IN " +
                "(SELECT version.id from EcmFileVersion version WHERE version.file.fileId=:fileId)" +
                "AND o.status=:statusType";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("fileId", fileId);
        if (statusType == null)
        {
            throw new GetMediaEngineException(
                    String.format("OCR status type for FILE_ID=[%d] cannot be null. OCR was not retrieved successfully.", fileId));
        }
        query.setParameter("statusType", statusType.toString());

        String reason;

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no OCR for FILE_ID=[{}] and STATUS=[{}]. REASON=[{}]", fileId, statusType, e.getMessage());
            return null;
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving OCR with FILE_ID=[%d] and STATUS=[%s]", fileId, statusType);
            LOG.error(reason, e);
        }
        throw new GetMediaEngineException(
                String.format("OCR for FILE_ID=[%d] and STATUS=[%s] was not retrieved successfully. REASON=[%s] ", fileId, statusType,
                        reason));
    }

    @Override
    protected Class<OCR> getPersistenceClass()
    {
        return OCR.class;
    }

    public OCR findByRemoteId(String remoteId)
    {
        String queryString = "SELECT o FROM OCR o WHERE o.remoteId =:remoteId";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("remoteId", remoteId);

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no OCR for REMOTE_ID=[{}]. REASON=[{}]", remoteId, e.getMessage());
            return null;
        }
    }

    @Override
    public String getSupportedObjectType()
    {
        return OCRConstants.OBJECT_TYPE;
    }
}
