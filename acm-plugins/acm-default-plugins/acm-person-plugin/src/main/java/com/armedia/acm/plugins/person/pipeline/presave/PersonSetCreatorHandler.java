package com.armedia.acm.plugins.person.pipeline.presave;

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.pipeline.PersonPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by bojan.milenkoski on 08.8.2017
 */
public class PersonSetCreatorHandler implements PipelineHandler<Person, PersonPipelineContext>
{
    @Override
    public void execute(Person entity, PersonPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (pipelineContext.isNewPerson())
        {
            entity.setCreator(pipelineContext.getAuthentication().getName());
        }
    }

    @Override
    public void rollback(Person entity, PersonPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
