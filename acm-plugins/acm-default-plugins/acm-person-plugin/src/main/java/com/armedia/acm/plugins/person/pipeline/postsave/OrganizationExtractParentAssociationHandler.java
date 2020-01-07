package com.armedia.acm.plugins.person.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.armedia.acm.plugins.person.model.OrganizationConstants.PARENT_COMPANY;
import static com.armedia.acm.plugins.person.model.OrganizationConstants.SUB_COMPANY;
import static com.armedia.acm.plugins.person.model.PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE;

import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private final Logger log = LogManager.getLogger(getClass());

    private ObjectAssociationService associationService;

    /*
     * (non-Javadoc)
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
