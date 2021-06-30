package com.armedia.acm.plugins.person.service;

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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.services.search.service.SolrJoinDocumentsService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;

public class OrganizationAssociationServiceImpl implements OrganizationAssociationService
{
    private Logger log = LogManager.getLogger(getClass());
    private OrganizationAssociationDao organizationAssociationDao;
    private OrganizationAssociationEventPublisher organizationAssociationEventPublisher;
    private SolrJoinDocumentsService solrJoinDocumentsService;

    @Override
    public OrganizationAssociation saveOrganizationAssociation(OrganizationAssociation organizationAssociation,
            Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        try
        {
            OrganizationAssociation savedOrganizationAssociation = organizationAssociationDao.save(organizationAssociation);
            return savedOrganizationAssociation;
        }
        catch (Exception e)
        {
            throw new AcmCreateObjectFailedException("organizationAssociation", e.getMessage(), e);
        }
    }

    @Override
    public OrganizationAssociation getOrganizationAssociation(Long id, Authentication auth)
    {
        return organizationAssociationDao.find(id);
    }

    @Override
    public void deleteOrganizationAssociation(Long id, Authentication auth)
    {
        OrganizationAssociation organizationAssociation = organizationAssociationDao.find(id);
        organizationAssociationDao.deleteOrganizationAssociationById(id);
        getOrganizationAssociationEventPublisher().publishOrganizationAssociationDeletedEvent(organizationAssociation);
    }

    @Override
    public String getOrganizationAssociations(Long organizationId, String parentType, int start, int limit, String sort,
            Authentication auth) throws AcmObjectNotFoundException
    {
        return solrJoinDocumentsService.getJoinedDocuments(
                auth, organizationId, CHILD_ID_S,
                PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE, CHILD_TYPE_S,
                PersonOrganizationConstants.ORGANIZATION_ASSOCIATION_OBJECT_TYPE,
                parentType, PARENT_TYPE_S,
                "parent_object",
                PARENT_REF_S, "id", start, limit, sort);
    }

    public OrganizationAssociationDao getOrganizationAssociationDao()
    {
        return organizationAssociationDao;
    }

    public void setOrganizationAssociationDao(OrganizationAssociationDao organizationAssociationDao)
    {
        this.organizationAssociationDao = organizationAssociationDao;
    }

    public OrganizationAssociationEventPublisher getOrganizationAssociationEventPublisher()
    {
        return organizationAssociationEventPublisher;
    }

    public void setOrganizationAssociationEventPublisher(OrganizationAssociationEventPublisher organizationAssociationEventPublisher)
    {
        this.organizationAssociationEventPublisher = organizationAssociationEventPublisher;
    }

    public void setSolrJoinDocumentsService(SolrJoinDocumentsService solrJoinDocumentsService)
    {
        this.solrJoinDocumentsService = solrJoinDocumentsService;
    }
}
