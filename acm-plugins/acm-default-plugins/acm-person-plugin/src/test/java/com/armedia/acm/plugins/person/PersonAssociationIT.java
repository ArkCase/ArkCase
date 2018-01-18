package com.armedia.acm.plugins.person;

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-plugin-test-mule.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-converter.xml" })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PersonAssociationIT
{

    @Autowired
    private PersonAssociationDao personAssocDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void savePersonAssociation() throws Exception
    {

        Person person = new Person();

        person.setId(952L);
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");
        person.setCreator("creator");

        Person per = new Person();

        per.setId(950L);
        per.setFamilyName("Person");
        per.setGivenName("ACM");
        per.setStatus("testStatus");
        per.setCreator("creator");

        PersonAssociation perAssoc = new PersonAssociation();

        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPerson(person);
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setNotes("here a we can write our note");
        perAssoc.setTags(Arrays.asList("tag 1", "tag 2"));

        PersonAssociation personAssoc = new PersonAssociation();

        personAssoc.setParentId(999L);
        personAssoc.setParentType("COMPLAINT");
        personAssoc.setPerson(per);
        personAssoc.setPersonType("Subject");
        personAssoc.setPersonDescription("long and athletic");

        personAssoc.setTags(Arrays.asList("tag 3", "tag 4"));

        PersonAssociation saved = personAssocDao.save(perAssoc);
        personAssocDao.save(personAssoc);

        List<Person> personList = personAssocDao.findPersonByParentIdAndParentType("COMPLAINT", 999L);

        log.debug(" the size of list returned: " + personList.size());

        for (Person pn : personList)
        {
            log.debug("person id " + pn.getId());
        }

        assertNotNull(saved.getId());

        personAssocDao.deletePersonAssociationById(saved.getId());

        em.flush();

    }
}
