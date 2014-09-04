package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


public class PersonDao extends AcmAbstractDao<Person>
{
        
    @PersistenceContext
    private EntityManager entityManager;
     
    @Override
    protected Class<Person> getPersistenceClass()
    {
        return Person.class;
    }
    
    public Integer deletePersonById(Long id)
    {
        Query deletePersonById = getEntityManager().createQuery(              
                    "DELETE FROM Person person " +
                            "WHERE person.id = :personId"                                    
                       );
      Integer  deletedCount =  deletePersonById.setParameter("personId", id).executeUpdate(); 
       /**
        *  this will return the count of the person deleted the association will checked after the personAssociation 
        *  table works
        */
      
        return deletedCount;
    
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
   
}
