package com.armedia.acm.services.comprehendmedical.dao;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.mediaengine.dao.MediaEngineDao;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;


public class ComprehendMedicalDao extends MediaEngineDao<ComprehendMedical>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    public ComprehendMedical findByMediaVersionId(Long mediaVersionId) throws GetMediaEngineException
    {
        String queryString = "SELECT t FROM ComprehendMedical t WHERE t.mediaEcmFileVersion.id=:mediaVersionId";

        TypedQuery<ComprehendMedical> query = getEm().createQuery(queryString, ComprehendMedical.class);
        query.setParameter("mediaVersionId", mediaVersionId);

        String reason = "";

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no ComprehendMedical for MEDIA_VERSION_ID=[{}]. REASON=[{}]", mediaVersionId, e.getMessage());
            return null;
        }
        catch (NonUniqueResultException e)
        {
            reason = String.format(
                    "There is no unique ComprehendMedical found with MEDIA_VERSION_ID=[%d]. More than one Transcribe has the same media version.",
                    mediaVersionId);
            LOG.warn(reason);
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving ComprehendMedical with MEDIA_VERSION_ID=[%d]", mediaVersionId);
            LOG.error(reason, e);
        }

        throw new GetMediaEngineException(
                String.format("ComprehendMedical for MEDIA_VERSION_ID=[%d] was not retrieved successfully. REASON=[%s]", mediaVersionId, reason));
    }

    @Override
    public List<ComprehendMedical> findAllByStatus(String status) throws GetMediaEngineException
    {
        String queryString = "SELECT t FROM ComprehendMedical t WHERE t.status=:status";

        TypedQuery<ComprehendMedical> query = getEm().createQuery(queryString, ComprehendMedical.class);
        query.setParameter("status", status);

        try
        {
            return query.getResultList();
        }
        catch (Exception e)
        {
            throw new GetMediaEngineException(String
                    .format("ComprehendMedical objects with STATUS=[%s] was not retrieved successfully. REASON=[%s]", status, e.getMessage()));
        }
    }

    @Override
    public MediaEngine findByFileId(Long fileId) throws GetMediaEngineException
    {
        throw new NotImplementedException();
    }

    @Override
    protected Class<ComprehendMedical> getPersistenceClass()
    {
        return ComprehendMedical.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return ComprehendMedicalConstants.OBJECT_TYPE;
    }
}
