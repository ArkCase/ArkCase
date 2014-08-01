package com.armedia.acm.plugins.person;

import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import java.util.ArrayList;
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
                                   "/spring/spring-library-person.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class SavePersonAliasIT
{
    @Autowired
    private PersonDao personDao;
    
    @PersistenceContext
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void savePersonAliasOnPersonTable() throws Exception
    {
        Person person = new Person();
        
        person.setModifier("testModifier");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");
        
        
        
        PersonAlias pa = new PersonAlias();
        
       
        pa.setAliasTypeId("Nick Name");
        pa.setAliasValue("ACM");
        pa.setModifier("testModifier");
        pa.setCreator("testCreator");
        pa.setCreated(new Date());
        pa.setModified(new Date());
        pa.setPerson(person);
                
        List<PersonAlias> personAlias = new ArrayList<>();
        personAlias.add(pa);
        
        person.setPersonAlias(personAlias);
                     
        Person saved =personDao.save(person);
        
        
        

        assertNotNull(saved.getId());

        log.info("Person ID: " + saved.getId());
        em.flush();
    }
}

