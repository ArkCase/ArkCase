package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.service.SaveConsultationBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Apply business rules to a Consultation.
 */
public class ConsultationRulesHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Business rule manager.
     */
    private SaveConsultationBusinessRule saveRule;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Consultation entering ConsultationRulesHandler : [{}]", entity);

        entity = saveRule.applyRules(entity);

        log.info("Consultation exiting ConsultationRulesHandler : [{}]", entity);

    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public SaveConsultationBusinessRule getSaveRule() {
        return saveRule;
    }

    public void setSaveRule(SaveConsultationBusinessRule saveRule) {
        this.saveRule = saveRule;
    }
}
