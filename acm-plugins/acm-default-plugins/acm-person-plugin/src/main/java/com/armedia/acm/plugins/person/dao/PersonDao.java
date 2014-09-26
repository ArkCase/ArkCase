package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;


public class PersonDao extends AcmAbstractDao<Person>
{
        
    @PersistenceContext
    private EntityManager entityManager;
    
    
     
    @Override
    protected Class<Person> getPersistenceClass()
    {
        return Person.class;
    }
    
    @Transactional
    public void deletePersonById(Long id)
    {           
        Query queryToDelete = getEntityManager().createQuery(              
                    "SELECT person " +"FROM Person person " +
                            "WHERE person.id = :personId"                                    
                       );
        queryToDelete.setParameter("personId", id); 
        
       Person personToBeDeleted = (Person) queryToDelete.getSingleResult();
        entityManager.remove(personToBeDeleted);
        
    }  
     
    public EntityManager getEntityManager() 
    {
        return entityManager;
    }
      
}
