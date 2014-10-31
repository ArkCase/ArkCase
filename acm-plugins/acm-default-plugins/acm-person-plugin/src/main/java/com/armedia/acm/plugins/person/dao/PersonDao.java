package com.armedia.acm.plugins.person.dao;

import java.util.ArrayList;
import java.util.List;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


public class PersonDao extends AcmAbstractDao<Person>
{
        
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
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
    
    public List<Person> findByNameOrContactValue(String name, String contactValue)
    {
    	List<Person> result = new ArrayList<Person>();
    	
    	CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	CriteriaQuery<Person> query = builder.createQuery(Person.class);
    	Root<Person> person = query.from(Person.class);
    	
    	query.select(person);
    	
    	query.where(
    			builder.or(
    					builder.like(builder.lower(person.<String>get("givenName")), "%" + name.toLowerCase() + "%"),
    					builder.like(builder.lower(person.<String>get("familyName")), "%" + name.toLowerCase() + "%")	
    			),
    			builder.and(
    					builder.like(builder.lower(person.get("contactMethods").<String>get("value")), "%" + contactValue.toLowerCase() + "%")
    			),
    			builder.and(
    					builder.equal(person.get("contactMethods").<String>get("status"), "ACTIVE")
    			),
    			builder.and(
    					builder.equal(person.<String>get("status"), "ACTIVE")
    			)
    	);
    	
    	TypedQuery<Person> dbQuery = getEm().createQuery(query);
    	
    	try
    	{
    		result = dbQuery.getResultList();
    	}
    	catch(Exception e)
    	{
    		LOG.info("There is no any results.");
    	}
    	
    	return result;
    }
     
    public EntityManager getEntityManager() 
    {
        return entityManager;
    }
      
}
