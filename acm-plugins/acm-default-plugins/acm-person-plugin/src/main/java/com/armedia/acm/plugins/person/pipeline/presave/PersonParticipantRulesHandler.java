package com.armedia.acm.plugins.person.pipeline.presave;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.pipeline.PersonPipelineContext;
import com.armedia.acm.services.participants.model.CheckParticipantListModel;
import com.armedia.acm.services.participants.service.ParticipantsBusinessRule;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bojan.milenkoski on 08.8.2017
 */
public class PersonParticipantRulesHandler implements PipelineHandler<Person, PersonPipelineContext>
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
    public void execute(Person entity, PersonPipelineContext pipelineContext) throws PipelineProcessException
    {

        log.debug("Person entering PersonPipelineContext : [{}]", entity);

        CheckParticipantListModel model = new CheckParticipantListModel();
        model.setParticipantList(entity.getParticipants());
        model.setObjectType(entity.getObjectType());

        model = participantsRule.applyRules(model);

        log.debug("Person exiting PersonPipelineContext : [{}]", entity);
        if (model.getErrorsList() != null && !model.getErrorsList().isEmpty())
        {
            throw new PipelineProcessException(new AcmAccessControlException(model.getErrorsList(),
                    "Conflict permissions combination has occurred for the chosen participants"));
        }
    }

    @Override
    public void rollback(Person entity, PersonPipelineContext pipelineContext) throws PipelineProcessException
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