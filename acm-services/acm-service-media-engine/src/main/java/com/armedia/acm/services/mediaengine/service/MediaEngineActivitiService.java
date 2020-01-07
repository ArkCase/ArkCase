package com.armedia.acm.services.mediaengine.service;

/*-
 * #%L
 * ACM Service: Media Engine
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngine;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineActivitiService
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    public List<MediaEngine> changeStatusMultiple(List<Long> ids, String status, String serviceName)
            throws MediaEngineServiceNotFoundException, SaveMediaEngineException
    {
        try
        {
            return getMediaEngineServiceFactory().getService(serviceName).changeStatusMultiple(ids, status);
        }
        catch (MediaEngineServiceNotFoundException | SaveMediaEngineException e)
        {
            LOG.warn("Changing status for [{}] in bulk operation failed. REASON=[{}]", serviceName, e.getMessage(), e);
            throw e;
        }
    }

    public void notifyMultiple(List<Long> ids, String action, String serviceName)
    {
        try
        {
            getMediaEngineServiceFactory().getService(serviceName).notifyMultiple(ids, action);
        }
        catch (MediaEngineServiceNotFoundException e)
        {
            LOG.error("Provider {} not found. REASON={}", serviceName, e.getMessage(), e);
        }
    }

    public void auditMultiple(List<Long> ids, String action, String serviceName, String message)
    {
        try
        {
            getMediaEngineServiceFactory().getService(serviceName).auditMultiple(ids, action, message);
        }
        catch (MediaEngineServiceNotFoundException e)
        {
            LOG.error("Provider {} not found. REASON={}", serviceName, e.getMessage(), e);
        }
    }

    public MediaEngineServiceFactory getMediaEngineServiceFactory()
    {
        return mediaEngineServiceFactory;
    }

    public void setMediaEngineServiceFactory(MediaEngineServiceFactory mediaEngineServiceFactory)
    {
        this.mediaEngineServiceFactory = mediaEngineServiceFactory;
    }
}
