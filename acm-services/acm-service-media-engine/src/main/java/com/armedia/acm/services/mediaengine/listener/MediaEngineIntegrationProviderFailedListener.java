package com.armedia.acm.services.mediaengine.listener;

/*-
 * #%L
 * ACM Service: Media engine
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineIntegrationFailedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineIntegrationProviderFailedListener implements ApplicationListener<MediaEngineIntegrationFailedEvent>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Override
    public void onApplicationEvent(MediaEngineIntegrationFailedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            getAuditPropertyEntityAdapter().setUserId(MediaEngineConstants.SYSTEM_USER);

            String serviceName = event.getServiceName();
            MediaEngineDTO mediaEngineDTO = event.getSource();

            try
            {
                MediaEngineService service = getMediaEngineServiceFactory().getService(serviceName);
                service.fail(mediaEngineDTO.getId(), event.getEventDescription());
            }
            catch (SaveMediaEngineException | MediaEngineServiceNotFoundException e)
            {
                LOG.error("Could not set as failed MediaEngine with ID=[{}] for Service=[{}].. REASON=[{}]", mediaEngineDTO.getId(),
                        serviceName, e.getMessage(), e);
            }
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
