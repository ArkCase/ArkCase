package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
                                   "/spring/spring-library-person.xml",
                                   "/spring/spring-library-person-plugin-test.xml",
                                   "/spring/spring-library-mule-context-manager.xml",
                                   "/spring/spring-library-activiti-actions.xml",
                                   "/spring/spring-library-activemq.xml",
                                   "/spring/spring-library-activiti-configuration.xml",
                                   "/spring/spring-library-folder-watcher.xml",
                                   "/spring/spring-library-cmis-configuration.xml",
                                   "/spring/spring-library-drools-monitor.xml",
                                   "/spring/spring-library-ecm-file.xml"
                                   })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class PersonIT
{
    @Autowired
    private PersonDao personDao;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void savePerson() throws Exception
    {
        Person p = new Person();
        p.setModifier("testModifier");
        p.setCreator("testCreator");
        p.setCreated(new Date());
        p.setModified(new Date());
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        Person saved = personDao.save(p);

        assertNotNull(saved.getId());

        personDao.deletePersonById(saved.getId());
        

        log.info("Person ID: " + saved.getId());        
    }
 
    
}
