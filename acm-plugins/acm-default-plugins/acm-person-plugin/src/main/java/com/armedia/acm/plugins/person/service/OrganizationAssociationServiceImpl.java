package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.services.search.service.SolrJoinDocumentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class OrganizationAssociationServiceImpl implements OrganizationAssociationService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private OrganizationAssociationDao organizationAssociationDao;
    private OrganizationAssociationEventPublisher organizationAssociationEventPublisher;
    private SolrJoinDocumentsService solrJoinDocumentsService;

    @Override
    public OrganizationAssociation saveOrganizationAssociation(OrganizationAssociation organizationAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException
    {
        try
        {
            OrganizationAssociation savedOrganizationAssociation = organizationAssociationDao.save(organizationAssociation);
            return savedOrganizationAssociation;
        } catch (Exception e)
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
    public String getOrganizationAssociations(Long organizationId, String parentType, int start, int limit, String sort, Authentication auth) throws AcmObjectNotFoundException
    {
        return solrJoinDocumentsService.getJoinedDocuments(
                organizationId, "child_id_s",
                PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE, "child_type_s",
                PersonOrganizationConstants.ORGANIZATION_ASSOCIATION_OBJECT_TYPE,
                parentType, "parent_type_s",
                "parent_object",
                start, limit, sort, auth,
                "parent_ref_s", "id");
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
