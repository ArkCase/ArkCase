package com.armedia.acm.plugins.person.service;

import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;


public class OrganizationServiceImpl implements OrganizationService
{
    private OrganizationDao organizationDao;

    private PipelineManager<Organization, OrganizationPipelineContext> organizationPipelineManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Organization findOrCreateOrganization(String organizationName, String userId)
    {
        Organization organization = organizationDao.findByOrganizationName(organizationName);
        if (organization == null)
        {
            organization = prepareNewOrg(organizationName, userId);
            log.debug("Saving organization with name: [{}]", organizationName);
            organization = organizationDao.save(organization);
        }
        return organization;
    }

    @Override
    @Transactional
    public Organization saveOrganization(Organization organization, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        OrganizationPipelineContext pipelineContext = new OrganizationPipelineContext();
        // populate the context
        pipelineContext.setNewOrganization(organization.getId() == null);
        pipelineContext.setAuthentication(auth);
        pipelineContext.setIpAddress(ipAddress);

        return organizationPipelineManager.executeOperation(organization, pipelineContext, () ->
        {
            Organization saved = organizationDao.save(organization);
            log.info("Organization saved '{}'", saved);
            return saved;
        });
    }

    @Override
    public Organization getOrganization(Long organizationId)
    {
        return organizationDao.find(organizationId);
    }

    private Organization prepareNewOrg(String companyName, String userId)
    {
        Organization org = new Organization();
        //possible org types
        //complaint.organizationTypes=Non-profit=Non-profit,Government=Government,Corporation=Corporation
        org.setOrganizationType("Corporation");
        org.setCreator(userId);
        org.setModifier(userId);
        org.setOrganizationValue(companyName);
        return org;
    }

    public OrganizationDao getOrganizationDao()
    {
        return organizationDao;
    }

    public void setOrganizationDao(OrganizationDao organizationDao)
    {
        this.organizationDao = organizationDao;
    }

    public PipelineManager<Organization, OrganizationPipelineContext> getOrganizationPipelineManager()
    {
        return organizationPipelineManager;
    }

    public void setOrganizationPipelineManager(PipelineManager<Organization, OrganizationPipelineContext> organizationPipelineManager)
    {
        this.organizationPipelineManager = organizationPipelineManager;
    }

}
