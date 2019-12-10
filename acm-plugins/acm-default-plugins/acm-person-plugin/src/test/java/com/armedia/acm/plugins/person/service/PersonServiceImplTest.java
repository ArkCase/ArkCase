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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.service.PhoneRegexConfig;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.pipeline.PersonPipelineContext;
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
public class PersonServiceImplTest extends EasyMockSupport
{
    PersonServiceImpl personService;
    PipelineManager<Person, PersonPipelineContext> mockPersonPipelineManager;
    PhoneRegexConfig mockPhoneRegexConfig;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
        personService = new PersonServiceImpl();
        mockPersonPipelineManager = createMock(PipelineManager.class);
        personService.setPersonPipelineManager(mockPersonPipelineManager);
        mockPhoneRegexConfig = createMock(PhoneRegexConfig.class);
        personService.setPhoneRegexConfig(mockPhoneRegexConfig);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessfullySavePersonWithValidOrganizationAssociations() throws AcmObjectNotFoundException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, PipelineProcessException
    {
        // given
        Person person = new Person();
        List<PersonOrganizationAssociation> organizationAssociations = new ArrayList<>();

        PersonOrganizationAssociation organizationAssociation1 = new PersonOrganizationAssociation();
        Organization organization1 = new Organization();
        organization1.setOrganizationId(1l);
        organizationAssociation1.setOrganization(organization1);
        organizationAssociation1.setPerson(person);
        organizationAssociation1.setPersonToOrganizationAssociationType("owner");
        organizationAssociation1.setOrganizationToPersonAssociationType("owned");
        organizationAssociations.add(organizationAssociation1);

        PersonOrganizationAssociation organizationAssociation2 = new PersonOrganizationAssociation();
        Organization organization2 = new Organization();
        organization2.setOrganizationId(2l);
        organizationAssociation2.setOrganization(organization2);
        organizationAssociation2.setPerson(person);
        organizationAssociation2.setPersonToOrganizationAssociationType("employee");
        organizationAssociation2.setOrganizationToPersonAssociationType("employer");
        organizationAssociations.add(organizationAssociation2);

        PersonOrganizationAssociation organizationAssociation3 = new PersonOrganizationAssociation();
        organizationAssociation3.setOrganization(organization2);
        organizationAssociation3.setPerson(person);
        organizationAssociation3.setPersonToOrganizationAssociationType("director");
        organizationAssociation3.setOrganizationToPersonAssociationType("director");
        organizationAssociations.add(organizationAssociation3);

        person.setOrganizationAssociations(organizationAssociations);

        String phoneRegex = "/^\\d{3}[\\-]\\d{3}[\\-]\\d{4}$/";

        expect(mockPersonPipelineManager.executeOperation(anyObject(Person.class), anyObject(PersonPipelineContext.class),
                anyObject(PipelineManagerOperation.class))).andReturn(person);
        expect(mockPhoneRegexConfig.getPhoneRegex()).andReturn(phoneRegex);

        replayAll();

        // when
        Person result = personService.savePerson(person, null);

        // then
        assertEquals(person, result);
        verifyAll();
    }

    @Test(expected = AcmCreateObjectFailedException.class)
    public void testFailSavePersonWithMissingOrganizationAssociationTypes() throws AcmObjectNotFoundException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, PipelineProcessException
    {
        // given
        Person person = new Person();
        List<PersonOrganizationAssociation> organizationAssociations = new ArrayList<>();

        PersonOrganizationAssociation organizationAssociation1 = new PersonOrganizationAssociation();
        Organization organization1 = new Organization();
        organization1.setOrganizationId(1l);
        organizationAssociation1.setOrganization(organization1);
        organizationAssociation1.setPerson(person);
        organizationAssociation1.setOrganizationToPersonAssociationType("owned");
        organizationAssociations.add(organizationAssociation1);

        PersonOrganizationAssociation organizationAssociation2 = new PersonOrganizationAssociation();
        Organization organization2 = new Organization();
        organization2.setOrganizationId(2l);
        organizationAssociation2.setOrganization(organization2);
        organizationAssociation2.setPerson(person);
        organizationAssociation2.setPersonToOrganizationAssociationType("employee");
        organizationAssociations.add(organizationAssociation2);

        person.setOrganizationAssociations(organizationAssociations);

        // when
        personService.savePerson(person, null);

        // then
        fail("AcmCreateObjectFailedException expected!");
    }

    @Test(expected = AcmCreateObjectFailedException.class)
    public void testFailSavePersonWithDuplicateOrganizationAssociations() throws AcmObjectNotFoundException, AcmCreateObjectFailedException,
            AcmUpdateObjectFailedException, AcmUserActionFailedException, PipelineProcessException
    {
        // given
        Person person = new Person();
        List<PersonOrganizationAssociation> organizationAssociations = new ArrayList<>();

        PersonOrganizationAssociation organizationAssociation1 = new PersonOrganizationAssociation();
        Organization organization1 = new Organization();
        organization1.setOrganizationId(1l);
        organizationAssociation1.setOrganization(organization1);
        organizationAssociation1.setPerson(person);
        organizationAssociation1.setPersonToOrganizationAssociationType("owner");
        organizationAssociation1.setOrganizationToPersonAssociationType("owned");
        organizationAssociations.add(organizationAssociation1);

        PersonOrganizationAssociation organizationAssociation2 = new PersonOrganizationAssociation();
        Organization organization2 = new Organization();
        organization2.setOrganizationId(2l);
        organizationAssociation2.setOrganization(organization2);
        organizationAssociation2.setPerson(person);
        organizationAssociation2.setPersonToOrganizationAssociationType("employee");
        organizationAssociation2.setOrganizationToPersonAssociationType("employer");
        organizationAssociations.add(organizationAssociation2);

        PersonOrganizationAssociation organizationAssociation3 = new PersonOrganizationAssociation();
        organizationAssociation3.setOrganization(organization2);
        organizationAssociation3.setPerson(person);
        organizationAssociation3.setPersonToOrganizationAssociationType("employee");
        organizationAssociation3.setOrganizationToPersonAssociationType("employer");
        organizationAssociations.add(organizationAssociation3);

        PersonOrganizationAssociation organizationAssociation4 = new PersonOrganizationAssociation();
        organizationAssociation4.setOrganization(organization1);
        organizationAssociation4.setPerson(person);
        organizationAssociation4.setPersonToOrganizationAssociationType("owner");
        organizationAssociation4.setOrganizationToPersonAssociationType("owned");
        organizationAssociations.add(organizationAssociation4);

        person.setOrganizationAssociations(organizationAssociations);

        // when
        personService.savePerson(person, null);

        // then
        fail("AcmCreateObjectFailedException expected!");
    }
}
