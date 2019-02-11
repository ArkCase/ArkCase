package com.armedia.acm.services.transcribe.factory;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.services.mediaengine.exception.MediaEngineProviderNotFound;
import com.armedia.acm.tool.transcribe.service.TranscribeIntegrationService;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TranscribeProviderFactory
{

    private Map<String, TranscribeIntegrationService> providers;

    public TranscribeIntegrationService getProvider(String name) throws MediaEngineProviderNotFound
    {
        if (providers != null && providers.containsKey(name))
        {
            return providers.get(name);
        }

        throw new MediaEngineProviderNotFound(
                String.format("Provider [%s] not found.", name));
    }

    public Map<String, TranscribeIntegrationService> getProviders()
    {
        return providers;
    }

    public void setProviders(Map<String, TranscribeIntegrationService> providers)
    {
        this.providers = providers;
    }

}
