package com.armedia.acm.plugins.casefile.pipeline.presave;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseFileParticipantRulesHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Business rule manager.
     */
    private ParticipantsBusinessRule participantsRule;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {

        log.info("CaseFile entering CaseFileParticipantRulesHandler : [{}]", entity);

        CheckParticipantListModel model = new CheckParticipantListModel();
        model.setParticipantList(entity.getParticipants());
        model.setObjectType(entity.getObjectType());

        model = participantsRule.applyRules(model);

        log.info("CaseFile exiting CaseFileParticipantRulesHandler : [{}]", entity);
        if (model.getErrorsList() != null && !model.getErrorsList().isEmpty())
        {
            throw new PipelineProcessException(new AcmAccessControlException(model.getErrorsList(), "Conflict permissions combination has occurred for the chosen participants"));
        }
    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }


    public void setParticipantsRule(ParticipantsBusinessRule participantsRule)
    {
        this.participantsRule = participantsRule;
    }

    public ParticipantsBusinessRule getParticipantsRule()
    {
        return participantsRule;
    }
}
