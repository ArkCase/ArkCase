package com.armedia.acm.services.mediaengine.pipeline.presave;

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
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineInitHandler implements PipelineHandler<MediaEngine, MediaEnginePipelineContext>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    public void execute(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {
        LOG.debug("MediaEngine entering MediaEngineInitHandler for FILE_ID: [{}]", pipelineContext.getEcmFileVersion().getFile().getId());

        entity.setMediaEcmFileVersion(pipelineContext.getEcmFileVersion());
        entity.setType(pipelineContext.getType().toString());

        LOG.debug("MediaEngine leaving MediaEngineInitHandler FILE_ID: [{}]", pipelineContext.getEcmFileVersion().getFile().getId());
    }

    @Override
    public void rollback(MediaEngine entity, MediaEnginePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
