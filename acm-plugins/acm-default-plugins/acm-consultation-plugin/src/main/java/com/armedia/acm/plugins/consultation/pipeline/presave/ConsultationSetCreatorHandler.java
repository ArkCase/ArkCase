package com.armedia.acm.plugins.consultation.pipeline.presave;

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Set creator so assignment rules can use it to set participants
 */
public class ConsultationSetCreatorHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (pipelineContext.isNewConsultation())
        {
            entity.setCreator(pipelineContext.getAuthentication().getName());
        }
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
