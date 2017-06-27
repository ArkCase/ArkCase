package com.armedia.acm.plugins.person;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
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

import static org.junit.Assert.assertEquals;
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
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-object-history.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PersonIT
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
    public void savePerson() throws Exception
    {
        Person p = new Person();

        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        Person saved = personDao.save(p);

        em.flush();

        assertNotNull(saved.getId());
        assertEquals("com.armedia.acm.plugins.person.model.Person", saved.getClassName());
        personDao.deletePersonById(saved.getId());

        em.flush();
    }


}
