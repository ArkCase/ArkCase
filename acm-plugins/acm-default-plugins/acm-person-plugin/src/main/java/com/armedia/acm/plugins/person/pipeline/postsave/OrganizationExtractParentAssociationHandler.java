package com.armedia.acm.plugins.person.pipeline.postsave;

import static com.armedia.acm.plugins.person.model.OrganizationConstants.PARENT_COMPANY;
import static com.armedia.acm.plugins.person.model.OrganizationConstants.SUB_COMPANY;
import static com.armedia.acm.plugins.person.model.PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE;

import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 7, 2017
 *
 */
public class OrganizationExtractParentAssociationHandler implements PipelineHandler<Organization, OrganizationPipelineContext>
{

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

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
        log.debug("Extracting parent-child organization associations for organization with id [{}].", entity.getId());

        if (pipelineContext.isNewOrganization() && entity.getParentOrganization() != null)
        {
            Organization parent = entity.getParentOrganization();

            ObjectAssociation parentAssociation = new ObjectAssociation();
            parentAssociation.setParentId(parent.getId());
            parentAssociation.setParentType(ORGANIZATION_OBJECT_TYPE);
            parentAssociation.setTargetId(entity.getId());
            parentAssociation.setTargetType(ORGANIZATION_OBJECT_TYPE);
            parentAssociation.setAssociationType(SUB_COMPANY);

            ObjectAssociation childAssociation = new ObjectAssociation();
            childAssociation.setParentId(entity.getId());
            childAssociation.setParentType(ORGANIZATION_OBJECT_TYPE);
            childAssociation.setTargetId(parent.getId());
            childAssociation.setTargetType(ORGANIZATION_OBJECT_TYPE);
            childAssociation.setAssociationType(PARENT_COMPANY);

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

        log.debug("Rolling back organization associations for organization with id [{}].", entity.getId());

        if (entity.getParentOrganization() != null)
        {
            Organization parent = entity.getParentOrganization();
            List<ObjectAssociation> associations = associationService.findByParentTypeAndId(ORGANIZATION_OBJECT_TYPE, parent.getId());
            for (ObjectAssociation association : associations)
            {
                if (association.getTargetId().equals(entity.getId()))
                {
                    associationService.delete(association.getAssociationId());
                    break;
                }
            }
            associations = associationService.findByParentTypeAndId(ORGANIZATION_OBJECT_TYPE, entity.getId());
            for (ObjectAssociation association : associations)
            {
                if (association.getTargetId().equals(parent.getId()))
                {
                    associationService.delete(association.getAssociationId());
                    break;
                }
            }
        }
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
