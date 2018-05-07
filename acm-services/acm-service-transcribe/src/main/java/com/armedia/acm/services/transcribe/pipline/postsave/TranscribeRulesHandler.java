package com.armedia.acm.services.transcribe.pipline.postsave;

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
