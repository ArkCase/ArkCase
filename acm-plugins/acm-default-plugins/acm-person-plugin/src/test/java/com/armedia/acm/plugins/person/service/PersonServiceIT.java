package com.armedia.acm.plugins.person.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-plugin-test-mule.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-search.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class PersonServiceIT
{

    @Autowired
    PersonService personService;

    @Autowired
    PersonDao personDao;

    @Autowired
    AuditPropertyEntityAdapter adapter;

    @Test
    public void addNewPersonTest()
    {
        Person person = new Person();
        String auth = "ann";
        adapter.setUserId(auth);


        person.setCompany("Company");
        person.setFamilyName("Family name");
        person.setGivenName("Name");

        personService.addPersonIdentification("key", "value", person);

        person = personDao.save(person);


        Person foundedPerson = personService.get(person.getId());

        assertNotNull(foundedPerson.getId());
        assertEquals(1, foundedPerson.getIdentifications().size());

        personDao.deletePersonById(person.getId());


        foundedPerson = personService.get(person.getId());
        assertNull(foundedPerson);
    }

}