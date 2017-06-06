package com.armedia.acm.plugins.person.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.dao.OrganizationAssociationDao;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import org.springframework.security.core.Authentication;

public class OrganizationAssociationServiceImpl implements OrganizationAssociationService
{

    private OrganizationAssociationDao organizationAssociationDao;

    private OrganizationAssociationEventPublisher organizationAssociationEventPublisher;

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
}
