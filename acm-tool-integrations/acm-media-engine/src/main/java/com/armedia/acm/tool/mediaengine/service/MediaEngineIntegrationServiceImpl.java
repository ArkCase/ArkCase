package com.armedia.acm.tool.mediaengine.service;

/*-
 * #%L
 * acm-media-engine
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

import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class MediaEngineIntegrationServiceImpl implements MediaEngineIntegrationService
{
    private MediaEngineServiceFactory mediaEngineToolServiceFactory;

    @Override
    public MediaEngineDTO create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        return getMediaEngineToolServiceFactory().getMediaEngineProviderFactory(mediaEngineDTO.getServiceName())
                .getProvider(mediaEngineDTO.getProviderName()).create(mediaEngineDTO);
    }

    @Override
    public MediaEngineDTO get(String remoteId, String serviceName, String providerName) throws GetMediaEngineToolException
    {
        return getMediaEngineToolServiceFactory().getMediaEngineProviderFactory(serviceName)
                .getProvider(providerName).get(remoteId, serviceName, providerName);
    }

    @Override
    public boolean purge(MediaEngineDTO mediaEngineDTO)
    {

        return getMediaEngineToolServiceFactory().getMediaEngineProviderFactory(mediaEngineDTO.getServiceName())
                .getProvider(mediaEngineDTO.getProviderName()).purge(mediaEngineDTO);
    }

    public MediaEngineServiceFactory getMediaEngineToolServiceFactory()
    {
        return mediaEngineToolServiceFactory;
    }

    public void setMediaEngineToolServiceFactory(MediaEngineServiceFactory mediaEngineToolServiceFactory)
    {
        this.mediaEngineToolServiceFactory = mediaEngineToolServiceFactory;
    }
}
