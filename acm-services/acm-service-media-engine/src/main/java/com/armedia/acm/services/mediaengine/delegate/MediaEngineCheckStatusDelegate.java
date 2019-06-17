package com.armedia.acm.services.mediaengine.delegate;

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

import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineCheckStatusDelegate implements JavaDelegate
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Override
    public void execute(DelegateExecution delegateExecution)
    {
        LOG.debug("Check status delegate execution for PROCESS_ID=[{}]", delegateExecution.getProcessInstanceId());

        String serviceName = null;
        try
        {
            serviceName = (String) delegateExecution.getVariable(MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString());
            getMediaEngineServiceFactory().getService(serviceName).checkStatus(delegateExecution);
        }
        catch (Exception e)
        {
            LOG.error("Unable to check status, service [{}] not found", serviceName, e.getMessage(), e);
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
