package com.armedia.acm.services.transcribe.pipline.presave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeInitHandler implements PipelineHandler<Transcribe, TranscribePipelineContext>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        LOG.debug("Transcribe entering TranscribeInitHandler : [{}]", entity);

        entity.setMediaEcmFileVersion(pipelineContext.getEcmFileVersion());
        entity.setType(pipelineContext.getType().toString());

        LOG.debug("Transcribe leaving TranscribeInitHandler : [{}]", entity);
    }

    @Override
    public void rollback(Transcribe entity, TranscribePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
