package com.armedia.acm.services.mediaengine.pipeline.postsave;

/*-
 * #%L
 * ACM Service: MediaEngine
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

import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.mediaengine.service.MediaEngineEventPublisher;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineBusinessProcessHandler implements PipelineHandler<MediaEngine, MediaEnginePipelineContext>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private RuntimeService activitiRuntimeService;
    private MediaEngineEventPublisher mediaEngineEventPublisher;
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Override
    public void execute(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {

        LOG.debug("MediaEngine entering MediaEngineBusinessProcessHandler : [{}]", entity);

        String serviceName = null;
        ProcessInstance processInstance = null;
        try
        {
            serviceName = pipelineContext.getServiceName();

            processInstance = getMediaEngineServiceFactory().getService(serviceName).startBusinessProcess(entity, serviceName);
        }
        catch (MediaEngineServiceNotFoundException e)
        {
            LOG.info("Provider {} not found.", serviceName);
        }
        if (processInstance != null)
        {
            pipelineContext.setProcessId(processInstance.getId());
        }

        LOG.debug("MediaEngine leaving MediaEngineBusinessProcessHandler : [{}]", entity);
    }

    @Override
    public void rollback(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {
        String serviceName = null;
        // Stop the process is started during execute
        if (pipelineContext != null && StringUtils.isNotEmpty(pipelineContext.getProcessId()))
        {
            serviceName = (String) activitiRuntimeService.getVariable(pipelineContext.getProcessId(),
                    MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString());
            getActivitiRuntimeService().deleteProcessInstance(pipelineContext.getProcessId(), "Pipeline rollback action.");
        }

        getMediaEngineEventPublisher().publish(entity, MediaEngineActionType.ROLLBACK.toString(), serviceName, "");

    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public MediaEngineEventPublisher getMediaEngineEventPublisher()
    {
        return mediaEngineEventPublisher;
    }

    public void setMediaEngineEventPublisher(MediaEngineEventPublisher mediaEngineEventPublisher)
    {
        this.mediaEngineEventPublisher = mediaEngineEventPublisher;
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
