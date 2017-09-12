package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent.ObjectAssociationState;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrganizationServiceImpl implements OrganizationService, ApplicationListener<ObjectAssociationEvent>
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
    public Organization saveOrganization(Organization organization, Authentication auth, String ipAddress)
            throws PipelineProcessException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        validatePersonAssociations(organization);
        OrganizationPipelineContext pipelineContext = new OrganizationPipelineContext();
        // populate the context
        pipelineContext.setNewOrganization(organization.getId() == null);
        pipelineContext.setAuthentication(auth);
        pipelineContext.setIpAddress(ipAddress);

        return organizationPipelineManager.executeOperation(organization, pipelineContext, () -> {
            Organization saved = organizationDao.save(organization);
            log.info("Organization saved '{}'", saved);
            return saved;
        });
    }

    /**
     * Validates the {@link PersonOrganizationAssociation}.
     *
     * @param organization
     *            the {@link Organization} to validate
     * @throws AcmCreateObjectFailedException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     * @throws AcmDuplicatePersonAssociationException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     */
    private void validatePersonAssociations(Organization organization) throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        List<PersonOrganizationAssociation> missingAssocTypes = organization.getPersonAssociations().stream().filter(
                assoc -> assoc.getPersonToOrganizationAssociationType() == null || assoc.getOrganizationToPersonAssociationType() == null)
                .collect(Collectors.toList());

        if (missingAssocTypes.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Missing person to organization relation type!");
            missingAssocTypes.forEach(assoc -> {
                errorMessage.append(" [PersonId: " + assoc.getPerson().getId() + "]");
            });
            throw new AcmCreateObjectFailedException("Organization", errorMessage.toString(), null);
        }

        Map<Long, List<String>> mapped = organization.getPersonAssociations().stream()
                .collect(Collectors.toMap(d -> d.getPerson().getId(), d -> {
                    List<String> value = new ArrayList<>();
                    value.add(d.getPersonToOrganizationAssociationType());
                    return value;
                }, (oldValue, newValue) -> {
                    oldValue.addAll(newValue);
                    return oldValue;
                }));

        List<Map.Entry<Long, List<String>>> withDupes = mapped.entrySet().stream()
                .filter(ds -> ds.getValue().size() != ((new HashSet<>(ds.getValue())).size())).collect(Collectors.toList());

        if (withDupes.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Duplicate persons to organization relations!");
            withDupes.forEach(duplicate -> {
                errorMessage.append("[PersonId: " + duplicate.getKey() + " <-> Realation: " + duplicate.getValue() + "]");
            });
            throw new AcmCreateObjectFailedException("Organization", errorMessage.toString(), null);
        }
    }

    @Override
    public Organization getOrganization(Long organizationId)
    {
        return organizationDao.find(organizationId);
    }

    private Organization prepareNewOrg(String companyName, String userId)
    {
        Organization org = new Organization();
        // possible org types
        // complaint.organizationTypes=Non-profit=Non-profit,Government=Government,Corporation=Corporation
        org.setOrganizationType("Corporation");
        org.setCreator(userId);
        org.setModifier(userId);
        org.setOrganizationValue(companyName);
        return org;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ObjectAssociationEvent event)
    {
        String associationType = ((ObjectAssociation) event.getSource()).getAssociationType();
        ObjectAssociation oa = ((ObjectAssociation) event.getSource());
        Organization child = null, parent = null;
        if (ObjectAssociationState.DELETE.equals(event.getObjectAssociationState()))
        {
            if ("SubCompany".equals(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getTargetId());
            } else if ("ParentCompany".equals(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getParentId());
            }
            if (child != null && child.getParentOrganization() != null)
            {
                child.setParentOrganization(null);
                organizationDao.save(child);
            }
        } else if (ObjectAssociationState.NEW.equals(event.getObjectAssociationState())
                || ObjectAssociationState.UPDATE.equals(event.getObjectAssociationState()))
        {
            if ("SubCompany".equals(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getTargetId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getParentId());

                if (child.getParentOrganization() != null && child.getParentOrganization().equals(parent))
                {
                    return;
                }
            } else if ("ParentCompany".equals(associationType))
            {
                // child is the child in the context of the association, not the child company!
                child = organizationDao.find(oa.getParentId());
                // parent is the parent in the context of the association, not the parent company!
                parent = organizationDao.find(oa.getTargetId());
                if (parent.getParentOrganization() != null && parent.getParentOrganization().equals(child))
                {
                    return;
                }
            }

            if (child != null && parent != null && !parent.equals(child.getParentOrganization()))
            {
                child.setParentOrganization(parent);
                organizationDao.save(child);
            }
        }

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
