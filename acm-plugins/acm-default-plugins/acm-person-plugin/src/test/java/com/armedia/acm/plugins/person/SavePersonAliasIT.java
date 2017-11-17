package com.armedia.acm.plugins.person;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-converter.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class SavePersonAliasIT
{
    @Autowired
    private PersonDao personDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void savePersonAliasOnPersonTable() throws Exception
    {
        Person person = new Person();

        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");

        PersonAlias pa = new PersonAlias();

        pa.setAliasType("Nick Name");
        pa.setAliasValue("ACM");
        pa.setPerson(person);

        List<PersonAlias> personAlias = new ArrayList<>();
        personAlias.add(pa);

        person.setPersonAliases(personAlias);
        person.setCreator("creator");

        Person saved = personDao.save(person);

        em.flush();

        assertNotNull(saved.getId());

        log.info("Person ID: " + saved.getId());

    }
}
