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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonPersistenceEvent;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class PersonAssociationEventPublisherTest extends EasyMockSupport
{
    PersonAssociationEventPublisher personAssociationEventPublisher;
    ApplicationEventPublisher mockEventPublisher;

    @Before
    public void setUp()
    {
        personAssociationEventPublisher = new PersonAssociationEventPublisher();
        mockEventPublisher = createMock(ApplicationEventPublisher.class);
        personAssociationEventPublisher.setApplicationEventPublisher(mockEventPublisher);
        personAssociationEventPublisher.setObjectConverter(ObjectConverter.createObjectConverterForTests());
    }

    @Test
    public void testAliasAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias1");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        // add alias
        PersonAlias newPersonAlias = getPersonAlias(2L, "TestAlias2");
        List<PersonAlias> personAliases = personAssociation.getPerson().getPersonAliases();
        personAliases.add(newPersonAlias);
        personAssociation.getPerson().setPersonAliases(personAliases);

        Capture<PersonPersistenceEvent> eventCapture = Capture.newInstance();
        mockEventPublisher.publishEvent(capture(eventCapture));
        expectLastCall().once();

        replayAll();
        personAssociationEventPublisher.publishPersonAssociationEvent(currentJsonObject, personAssociation, true);

        verifyAll();
        assertEquals(eventCapture.getValue().getEventType(), "com.armedia.acm.person.personAlias.added");
    }

    @Test
    public void testAliasEdited()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        // edit alias
        List<PersonAlias> personAliases = personAssociation.getPerson().getPersonAliases();
        PersonAlias personAlias = personAliases.get(0);
        personAlias.setAliasValue("TestAliasEdited");

        Capture<PersonPersistenceEvent> eventCapture = Capture.newInstance();
        mockEventPublisher.publishEvent(capture(eventCapture));
        expectLastCall().once();

        replayAll();
        personAssociationEventPublisher.publishPersonAssociationEvent(currentJsonObject, personAssociation, true);

        verifyAll();
        assertEquals(eventCapture.getValue().getEventType(), "com.armedia.acm.person.personAlias.updated");
    }

    @Test
    public void testAliasRemoved()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshallerForTests();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias1");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        // remove alias
        List<PersonAlias> personAliases = personAssociation.getPerson().getPersonAliases();
        personAliases.remove(0);

        Capture<PersonPersistenceEvent> eventCapture = Capture.newInstance();
        mockEventPublisher.publishEvent(capture(eventCapture));
        expectLastCall().once();

        replayAll();
        personAssociationEventPublisher.publishPersonAssociationEvent(currentJsonObject, personAssociation, true);

        verifyAll();
        assertEquals(eventCapture.getValue().getEventType(), "com.armedia.acm.person.personAlias.deleted");
    }

    PersonAssociation getPersonAssociation(Long id, String name)
    {
        PersonAlias personAlias = getPersonAlias(id, name);
        List<PersonAlias> existingAliases = new ArrayList<>();
        existingAliases.add(personAlias);
        Person person = new Person();
        person.setId(id);
        person.setPersonAliases(existingAliases);
        PersonAssociation personAssociation = new PersonAssociation();
        personAssociation.setId(id);
        personAssociation.setPerson(person);
        return personAssociation;
    }

    PersonAlias getPersonAlias(Long id, String name)
    {
        PersonAlias personAlias = new PersonAlias();
        personAlias.setId(id);
        personAlias.setAliasValue(name);
        return personAlias;
    }
}
