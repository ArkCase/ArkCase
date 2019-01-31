package com.armedia.acm.ocr.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class OCRDao extends AcmAbstractDao<OCR>
{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public OCR findByMediaVersionId(Long ecmFileVersionId) throws GetOCRException
    {
        String queryString = "SELECT o FROM OCR o WHERE o.ecmFileVersion.id=:mediaVersionId";

        TypedQuery<OCR> query = getEm().createQuery(queryString, OCR.class);
        query.setParameter("mediaVersionId", ecmFileVersionId);

        String reason = "";

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOGGER.warn("There is no OCR for MEDIA_VERSION_ID=[{}]. REASON=[{}]", ecmFileVersionId, e.getMessage());
            return null;
        }
        catch (NonUniqueResultException e)
        {
            reason = String.format(
                    "There is no unique OCR found with MEDIA_VERSION_ID=[%d]. More than one OCR has the same media version.",
                    ecmFileVersionId);
            LOGGER.warn(reason);
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving OCR with MEDIA_VERSION_ID=[%d]", ecmFileVersionId);
            LOGGER.error(reason, e);
        }

        throw new GetOCRException(
                String.format("OCR for MEDIA_VERSION_ID=[%d] was not retrieved successfully. REASON=[%s]", ecmFileVersionId,
                        reason));
    }

    public List<OCR> findAllByStatus(String status) throws GetOCRException
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
            throw new GetOCRException(String
                    .format("OCR objects with STATUS=[%s] was not retrieved successfully. REASON=[%s]", status, e.getMessage()));
        }
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
            LOGGER.warn("There is no OCR for REMOTE_ID=[{}]. REASON=[{}]", remoteId, e.getMessage());
            return null;
        }
    }

    @Override
    public String getSupportedObjectType()
    {
        return OCRConstants.OBJECT_TYPE;
    }
}
