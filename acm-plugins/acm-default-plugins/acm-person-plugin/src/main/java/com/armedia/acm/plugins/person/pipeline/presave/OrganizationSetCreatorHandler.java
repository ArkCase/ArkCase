package com.armedia.acm.plugins.person.pipeline.presave;

import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * Created by bojan.milenkoski on 04.8.2017
 */
public class OrganizationSetCreatorHandler implements PipelineHandler<Organization, OrganizationPipelineContext>
{
    @Override
    public void execute(Organization entity, OrganizationPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (pipelineContext.isNewOrganization())
        {
            entity.setCreator(pipelineContext.getAuthentication().getName());
        }
    }

    @Override
    public void rollback(Organization entity, OrganizationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
