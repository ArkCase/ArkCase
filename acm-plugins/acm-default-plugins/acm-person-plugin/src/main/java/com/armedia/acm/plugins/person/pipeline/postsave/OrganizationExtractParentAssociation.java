package com.armedia.acm.plugins.person.pipeline.postsave;

import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 7, 2017
 *
 */
public class OrganizationExtractParentAssociation implements PipelineHandler<Organization, OrganizationPipelineContext>
{

    private ObjectAssociationService associationService;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.pipeline.handler.PipelineHandler#execute(java.lang.Object,
     * com.armedia.acm.services.pipeline.AbstractPipelineContext)
     */
    @Override
    public void execute(Organization entity, OrganizationPipelineContext pipelineContext) throws PipelineProcessException
    {
        if (pipelineContext.isNewOrganization() && entity.getParentOrganization() != null)
        {
            Organization parent = entity.getParentOrganization();

            ObjectAssociation parentAssociation = new ObjectAssociation();
            parentAssociation.setParentId(parent.getId());
            parentAssociation.setParentType("ORGANIZATION");
            parentAssociation.setTargetId(entity.getId());
            parentAssociation.setTargetType("ORGANIZATION");
            parentAssociation.setAssociationType("SubCompany");

            ObjectAssociation childAssociation = new ObjectAssociation();
            childAssociation.setParentId(entity.getId());
            childAssociation.setParentType("ORGANIZATION");
            childAssociation.setTargetId(parent.getId());
            childAssociation.setTargetType("ORGANIZATION");
            childAssociation.setAssociationType("ParentCompany");

            parentAssociation.setInverseAssociation(childAssociation);
            childAssociation.setInverseAssociation(parentAssociation);

            associationService.saveObjectAssociation(parentAssociation);

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.pipeline.handler.PipelineHandler#rollback(java.lang.Object,
     * com.armedia.acm.services.pipeline.AbstractPipelineContext)
     */
    @Override
    public void rollback(Organization entity, OrganizationPipelineContext pipelineContext) throws PipelineProcessException
    {
        System.out.println(entity.getId());
    }

    /**
     * @param associationService
     *            the associationService to set
     */
    public void setAssociationService(ObjectAssociationService associationService)
    {
        this.associationService = associationService;
    }

}
