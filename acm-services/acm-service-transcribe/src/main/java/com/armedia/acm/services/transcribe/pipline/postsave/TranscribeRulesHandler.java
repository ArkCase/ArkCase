package com.armedia.acm.services.transcribe.pipline.postsave;

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

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessRulesExecutor;
import com.armedia.acm.services.transcribe.service.TranscribeEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeRulesHandler implements PipelineHandler<Transcribe, TranscribePipelineContext>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private TranscribeBusinessRulesExecutor transcribeBusinessRulesExecutor;
    private TranscribeEventPublisher transcribeEventPublisher;

    @Override
    public void execute(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        LOG.debug("Transcribe entering TranscribeRulesHandler : [{}]", entity);

        getTranscribeBusinessRulesExecutor().applyRules(entity);

        LOG.debug("Transcribe leaving TranscribeRulesHandler : [{}]", entity);
    }

    @Override
    public void rollback(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        getTranscribeEventPublisher().publish(entity, TranscribeActionType.ROLLBACK.toString());
    }

    public TranscribeBusinessRulesExecutor getTranscribeBusinessRulesExecutor()
    {
        return transcribeBusinessRulesExecutor;
    }

    public void setTranscribeBusinessRulesExecutor(TranscribeBusinessRulesExecutor transcribeBusinessRulesExecutor)
    {
        this.transcribeBusinessRulesExecutor = transcribeBusinessRulesExecutor;
    }

    public TranscribeEventPublisher getTranscribeEventPublisher()
    {
        return transcribeEventPublisher;
    }

    public void setTranscribeEventPublisher(TranscribeEventPublisher transcribeEventPublisher)
    {
        this.transcribeEventPublisher = transcribeEventPublisher;
    }
}
