package com.armedia.acm.plugins.person.service;


import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonModifiedEvent;
import com.armedia.acm.plugins.person.model.PersonPersistenceEvent;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void testAliasAdded()
    {
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias1");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        //add alias
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        //edit alias
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
        AcmMarshaller acmMarshaller = ObjectConverter.createJSONMarshaller();
        PersonAssociation personAssociation = getPersonAssociation(1L, "TestAlias1");
        String currentJsonObject = acmMarshaller.marshal(personAssociation);

        //remove alias
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
