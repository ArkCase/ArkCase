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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.addressable.service.PhoneRegexConfig;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.pipeline.OrganizationPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.PipelineManager.PipelineManagerOperation;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan.milenkoski on 10.8.2017
 */
public class OrganizationServiceImplTest extends EasyMockSupport
{
    OrganizationServiceImpl organizationService;
    PipelineManager<Organization, OrganizationPipelineContext> mockOrganizationPipelineManager;
    PhoneRegexConfig mockPhoneRegexConfig;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
        organizationService = new OrganizationServiceImpl();
        mockOrganizationPipelineManager = createMock(PipelineManager.class);
        organizationService.setOrganizationPipelineManager(mockOrganizationPipelineManager);
        mockPhoneRegexConfig = createMock(PhoneRegexConfig.class);
        organizationService.setPhoneRegexConfig(mockPhoneRegexConfig);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessfullySaveOrganizationWithValidPersonAssociations()
            throws PipelineProcessException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        // given
        Organization organization = new Organization();
        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();

        PersonOrganizationAssociation personAssociation1 = new PersonOrganizationAssociation();
        Person person1 = new Person();
        person1.setId(1l);
        personAssociation1.setPerson(person1);
        personAssociation1.setOrganization(organization);
        personAssociation1.setPersonToOrganizationAssociationType("owner");
        personAssociation1.setOrganizationToPersonAssociationType("owned");
        personAssociations.add(personAssociation1);

        PersonOrganizationAssociation personAssociation2 = new PersonOrganizationAssociation();
        Person person2 = new Person();
        person2.setId(2l);
        personAssociation2.setPerson(person2);
        personAssociation2.setOrganization(organization);
        personAssociation2.setPersonToOrganizationAssociationType("employee");
        personAssociation2.setOrganizationToPersonAssociationType("employer");
        personAssociations.add(personAssociation2);

        PersonOrganizationAssociation personAssociation3 = new PersonOrganizationAssociation();
        personAssociation3.setPerson(person2);
        personAssociation3.setOrganization(organization);
        personAssociation3.setPersonToOrganizationAssociationType("director");
        personAssociation3.setOrganizationToPersonAssociationType("director");
        personAssociations.add(personAssociation3);
        String phoneRegex = "/^\\d{3}[\\-]\\d{3}[\\-]\\d{4}$/";

        organization.setPersonAssociations(personAssociations);

        expect(mockOrganizationPipelineManager.executeOperation(anyObject(Organization.class), anyObject(OrganizationPipelineContext.class),
                anyObject(PipelineManagerOperation.class))).andReturn(organization);
        expect(mockPhoneRegexConfig.getPhoneRegex()).andReturn(phoneRegex);

        replayAll();

        // when
        Organization result = organizationService.saveOrganization(organization, null, null);

        // then
        assertEquals(organization, result);
        verifyAll();
    }

    @Test(expected = AcmCreateObjectFailedException.class)
    public void testFailSaveOrganizationWithMissingPersonAssociationTypes()
            throws PipelineProcessException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        // given
        Organization organization = new Organization();
        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();

        PersonOrganizationAssociation personAssociation1 = new PersonOrganizationAssociation();
        Person person1 = new Person();
        person1.setId(1l);
        personAssociation1.setPerson(person1);
        personAssociation1.setOrganization(organization);
        personAssociation1.setOrganizationToPersonAssociationType("owned");
        personAssociations.add(personAssociation1);

        PersonOrganizationAssociation personAssociation2 = new PersonOrganizationAssociation();
        Person person2 = new Person();
        person2.setId(2l);
        personAssociation2.setPerson(person2);
        personAssociation2.setOrganization(organization);
        personAssociation2.setPersonToOrganizationAssociationType("employee");
        personAssociations.add(personAssociation2);

        organization.setPersonAssociations(personAssociations);

        // when
        organizationService.saveOrganization(organization, null, null);

        // then
        fail("AcmCreateObjectFailedException expected!");
    }

    @Test(expected = AcmCreateObjectFailedException.class)
    public void testFailSaveOrganizationWithDuplicatePersonAssociations()
            throws PipelineProcessException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        // given
        Organization organization = new Organization();
        List<PersonOrganizationAssociation> personAssociations = new ArrayList<>();

        PersonOrganizationAssociation personAssociation1 = new PersonOrganizationAssociation();
        Person person1 = new Person();
        person1.setId(1l);
        personAssociation1.setPerson(person1);
        personAssociation1.setOrganization(organization);
        personAssociation1.setPersonToOrganizationAssociationType("owner");
        personAssociation1.setOrganizationToPersonAssociationType("owned");
        personAssociations.add(personAssociation1);

        PersonOrganizationAssociation personAssociation2 = new PersonOrganizationAssociation();
        Person person2 = new Person();
        person2.setId(2l);
        personAssociation2.setPerson(person2);
        personAssociation2.setOrganization(organization);
        personAssociation2.setPersonToOrganizationAssociationType("employee");
        personAssociation2.setOrganizationToPersonAssociationType("employer");
        personAssociations.add(personAssociation2);

        PersonOrganizationAssociation personAssociation3 = new PersonOrganizationAssociation();
        personAssociation3.setPerson(person2);
        personAssociation3.setOrganization(organization);
        personAssociation3.setPersonToOrganizationAssociationType("employee");
        personAssociation3.setOrganizationToPersonAssociationType("employer");
        personAssociations.add(personAssociation3);

        PersonOrganizationAssociation personAssociation4 = new PersonOrganizationAssociation();
        personAssociation4.setPerson(person1);
        personAssociation4.setOrganization(organization);
        personAssociation4.setPersonToOrganizationAssociationType("owner");
        personAssociation4.setOrganizationToPersonAssociationType("owned");
        personAssociations.add(personAssociation4);

        organization.setPersonAssociations(personAssociations);

        // when
        organizationService.saveOrganization(organization, null, null);

        // then
        fail("AcmCreateObjectFailedException expected!");
    }
}
