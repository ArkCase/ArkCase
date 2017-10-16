package com.armedia.acm.plugins.complaint.pipeline.presave;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplaintParticipantRulesHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
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
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Complaint entering ComplaintParticipantRulesHandler : [{}]", entity);

        CheckParticipantListModel model = new CheckParticipantListModel();
        model.setParticipantList(entity.getParticipants());
        model.setObjectType(entity.getObjectType());

        model = participantsRule.applyRules(model);

        log.info("Complaint exiting ComplaintParticipantRulesHandler : [{}]", entity);
        if (model.getErrorsList() != null && !model.getErrorsList().isEmpty())
        {
            throw new PipelineProcessException(new AcmAccessControlException(model.getErrorsList(), "Conflict permissions combination has occurred for the chosen participants"));
        }
    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public ParticipantsBusinessRule getParticipantsRule()
    {
        return participantsRule;
    }

    public void setParticipantsRule(ParticipantsBusinessRule participantsRule)
    {
        this.participantsRule = participantsRule;
    }

}
