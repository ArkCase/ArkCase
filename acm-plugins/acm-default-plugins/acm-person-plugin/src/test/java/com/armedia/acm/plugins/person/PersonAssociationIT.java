package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
public class PersonAssociationIT 
{

    @Autowired
    private PersonAssociationDao personAssocDao;

    private Logger log = LoggerFactory.getLogger(getClass());

     @PersistenceContext
    private EntityManager em;
     
    @Test
    @Transactional
    public void savePersonAssociation() throws Exception 
    {
       

        Person person = new Person();
        
        person.setId(952L);
        person.setModifier("testModifier");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");
        
         Person per = new Person();
        
        per.setId(950L);
        per.setModifier("testModifier");
        per.setCreator("testCreator");
        per.setCreated(new Date());
        per.setModified(new Date());
        per.setFamilyName("Person");
        per.setGivenName("ACM");
        per.setStatus("testStatus");
        

        PersonAssociation perAssoc = new PersonAssociation();

        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPerson(person);
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setModifier("testModifier");
        perAssoc.setCreator("testCreator");
        perAssoc.setCreated(new Date());
        perAssoc.setModified(new Date());
        
        PersonAssociation personAssoc = new PersonAssociation();

        personAssoc.setParentId(999L);
        personAssoc.setParentType("COMPLAINT");
        personAssoc.setPerson(per);
        personAssoc.setPersonType("Subject");
        personAssoc.setPersonDescription("long and athletic");
        personAssoc.setModifier("testModifier");
        personAssoc.setCreator("testCreator");
        personAssoc.setCreated(new Date());
        personAssoc.setModified(new Date());

        PersonAssociation saved = personAssocDao.save(perAssoc);
                                  personAssocDao.save(personAssoc);
        
        List<Person> personList = personAssocDao.findPersonByParentIdAndParentType("COMPLAINT", 999L);
        
        log.debug(" the size of list returned: " + personList.size());     
        
        for ( Person pn : personList )
        {
            log.debug("person id " + pn.getId());           
        }
                
        assertNotNull(saved.getId());
        em.flush();
        
    }
}
