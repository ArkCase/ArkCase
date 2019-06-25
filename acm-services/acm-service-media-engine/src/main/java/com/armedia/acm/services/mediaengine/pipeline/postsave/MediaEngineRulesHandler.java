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

import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.mediaengine.rules.MediaEngineBusinessRulesExecutor;
import com.armedia.acm.services.mediaengine.service.MediaEngineEventPublisher;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.activiti.engine.RuntimeService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineRulesHandler implements PipelineHandler<MediaEngine, MediaEnginePipelineContext>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private RuntimeService activitiRuntimeService;
    private MediaEngineEventPublisher mediaEngineEventPublisher;
    private Map<String, MediaEngineBusinessRulesExecutor> rules;

    @Override
    public void execute(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {
        String serviceName = pipelineContext.getServiceName();

        LOG.debug("{} entering MediaEngineRulesHandler for MediaEngine_ID : [{}]", serviceName, entity.getId());

        rules.get(serviceName).applyRules(entity);

        LOG.debug("{} leaving MediaEngineRulesHandler for MediaEngine_ID : [{}]", serviceName, entity.getId());
    }

    @Override
    public void rollback(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {
        String serviceName = (String) getActivitiRuntimeService().getVariable(pipelineContext.getProcessId(),
                MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString());
        getMediaEngineEventPublisher().publish(entity, MediaEngineActionType.ROLLBACK.toString(), serviceName, "");
    }

    public MediaEngineEventPublisher getMediaEngineEventPublisher()
    {
        return mediaEngineEventPublisher;
    }

    public void setMediaEngineEventPublisher(MediaEngineEventPublisher mediaEngineEventPublisher)
    {
        this.mediaEngineEventPublisher = mediaEngineEventPublisher;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public Map<String, MediaEngineBusinessRulesExecutor> getRules()
    {
        return rules;
    }

    public void setRules(Map<String, MediaEngineBusinessRulesExecutor> rules)
    {
        this.rules = rules;
    }
}
