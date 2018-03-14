package com.armedia.acm.services.transcribe.pipline.postsave;

import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessRulesExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class TranscribeRulesHandler implements PipelineHandler<Transcribe, TranscribePipelineContext>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private TranscribeBusinessRulesExecutor transcribeBusinessRulesExecutor;

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
        // nothing to do here, there is no rollback action to be executed
    }

    public TranscribeBusinessRulesExecutor getTranscribeBusinessRulesExecutor()
    {
        return transcribeBusinessRulesExecutor;
    }

    public void setTranscribeBusinessRulesExecutor(TranscribeBusinessRulesExecutor transcribeBusinessRulesExecutor)
    {
        this.transcribeBusinessRulesExecutor = transcribeBusinessRulesExecutor;
    }
}
