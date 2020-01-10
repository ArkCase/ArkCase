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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.addressable.exceptions.AcmContactMethodValidationException;
import com.armedia.acm.plugins.addressable.service.ContactMethodsUtil;
import com.armedia.acm.plugins.addressable.service.PhoneRegexConfig;
import com.armedia.acm.plugins.person.dao.OrganizationDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrganizationServiceImpl implements OrganizationService
{
    private OrganizationDao organizationDao;
    private PhoneRegexConfig phoneRegexConfig;

    private PipelineManager<Organization, OrganizationPipelineContext> organizationPipelineManager;


    private Logger log = LogManager.getLogger(getClass());

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
        try
        {
            ContactMethodsUtil.validateContactMethodFields(organization.getContactMethods(), phoneRegexConfig);
        }
        catch (AcmContactMethodValidationException e)
        {
            if (organization.getId() == null)
            {
                throw new AcmCreateObjectFailedException("Organization", e.toString(), null);
            }
            else
            {
                throw new AcmUpdateObjectFailedException("Organization", organization.getId(), e.toString(), null);
            }
        }
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
     * @throws AcmUpdateObjectFailedException
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

    public PhoneRegexConfig getPhoneRegexConfig()
    {
        return phoneRegexConfig;
    }

    public void setPhoneRegexConfig(PhoneRegexConfig phoneRegexConfig)
    {
        this.phoneRegexConfig = phoneRegexConfig;
    }
}
