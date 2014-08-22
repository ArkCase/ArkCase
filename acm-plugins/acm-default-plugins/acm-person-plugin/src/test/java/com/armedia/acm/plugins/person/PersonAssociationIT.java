package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.Date;
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
                                   "/spring/spring-library-person.xml"})
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
        
      
        person.setModifier("testModifier");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");
        

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

        PersonAssociation saved = personAssocDao.save(perAssoc);

        assertNotNull(saved.getId());

        em.flush();
    }
}
