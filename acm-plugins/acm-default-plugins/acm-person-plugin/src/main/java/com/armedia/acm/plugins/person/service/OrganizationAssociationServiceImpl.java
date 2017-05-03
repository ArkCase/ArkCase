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
        Long id = organizationAssociation.getId();
        String organizationAssociationHistory = null;

        if (id != null)
        {
            OrganizationAssociation exOrganizationAssociation = getOrganizationAssociationDao().find(id);
            AcmMarshaller marshaller = ObjectConverter.createJSONMarshaller();
            // keep copy from the existing object to compare with the updated one
            // otherwise JPA will update all references and no changes can be detected
            organizationAssociationHistory = marshaller.marshal(exOrganizationAssociation);
        }

        try
        {
            OrganizationAssociation savedOrganizationAssociation = organizationAssociationDao.save(organizationAssociation);

            getOrganizationAssociationEventPublisher().publishOrganizationAssociationEvent(organizationAssociationHistory, savedOrganizationAssociation, true);
            return savedOrganizationAssociation;
        } catch (Exception e)
        {
            getOrganizationAssociationEventPublisher().publishOrganizationAssociationEvent(organizationAssociationHistory, organizationAssociation, false);
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
