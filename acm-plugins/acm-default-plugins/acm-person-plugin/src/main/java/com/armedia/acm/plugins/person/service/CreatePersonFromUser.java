package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.UserPersistenceEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ana Serafimoska <ana.serafimoska@armedia.com> on 5/19/2021
 */
public class CreatePersonFromUser implements ApplicationListener<UserPersistenceEvent>
{
    private transient Logger log = LogManager.getLogger(getClass());
    private PersonService personService;
    private PersonDao personDao;

    @Override
    public void onApplicationEvent(UserPersistenceEvent event)
    {
        addOrUpdatePerson(event);
    }

    void addOrUpdatePerson(Object object)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((AcmUser) ((UserPersistenceEvent) object).getSource()).getUserId();
        if (((UserPersistenceEvent) object).getSource() instanceof AcmUser)
        {
            if (!userId.equals("OCR_SERVICE") && !userId.equals("TRANSCRIBE_SERVICE"))
            {
                Person existingPerson = getPersonDao()
                        .findByLdapUserId(((AcmUser) ((UserPersistenceEvent) object).getSource()).getUserId());
                Optional<Person> existingPersonWithoutLdapId = getPersonDao()
                        .findByEmail(((AcmUser) ((UserPersistenceEvent) object).getSource()).getMail());
                if (existingPerson != null)
                {
                    addOrUpdatePerson((UserPersistenceEvent) object, auth, existingPerson);
                }
                else if (existingPersonWithoutLdapId.isPresent())
                {
                    addOrUpdatePerson((UserPersistenceEvent) object, auth, existingPersonWithoutLdapId.get());
                }
                else
                {
                    Person person = new Person();
                    addOrUpdatePerson((UserPersistenceEvent) object, auth, person);
                }
            }
        }
    }

    private void addOrUpdatePerson(UserPersistenceEvent object, Authentication auth, Person person)
    {
        person.setLdapUserId(((AcmUser) object.getSource()).getUserId());
        person.setGivenName(
                ((AcmUser) object.getSource()).getFirstName() != null ? ((AcmUser) object.getSource()).getFirstName() : "Unknown");
        person.setFamilyName(
                ((AcmUser) object.getSource()).getLastName() != null ? ((AcmUser) object.getSource()).getLastName() : "Unknown");
        person.setTitle("-");

        List<ContactMethod> contactMethods = new ArrayList<>();
        ContactMethod contactMethodEmail = new ContactMethod();
        contactMethodEmail.setType("email");
        contactMethodEmail.setSubType("Business");
        contactMethodEmail.setValue(((AcmUser) object.getSource()).getMail());
        contactMethods.add(contactMethodEmail);

        person.setContactMethods(contactMethods);
        person.setDefaultEmail(contactMethodEmail);

        try
        {
            log.debug("Persist a Person: [{}];", person);
            personService.savePerson(person, auth);
        }
        catch (AcmObjectNotFoundException | AcmCreateObjectFailedException | AcmUpdateObjectFailedException | AcmUserActionFailedException
                | PipelineProcessException e)
        {
            log.error("Unable to save a person object", e);
        }
    }

    public PersonService getPersonService()
    {
        return personService;
    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }
}
