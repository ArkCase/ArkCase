package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class PersonAssociationDao extends AcmAbstractDao<PersonAssociation>
{
	private transient final Logger LOG = LoggerFactory.getLogger(getClass());
    
     @PersistenceContext
    private EntityManager entityManager;
     
    @Override
    protected Class<PersonAssociation> getPersistenceClass()
    {
        return PersonAssociation.class;
    }

    public List<Person> findPersonByParentIdAndParentType(String parentType, Long parentId)
    {
         
        Query personInAssociation = getEntityManager().createQuery(           
                      "SELECT person " + "FROM PersonAssociation personAssociation, " + "Person person "+
                           "WHERE personAssociation.parentType = :parentType " +
                            "AND personAssociation.parentId = :parentId " +
                            "AND personAssociation.person.id = person.id"
                            );
                 
        personInAssociation.setParameter("parentType", parentType.toUpperCase());
        personInAssociation.setParameter("parentId", parentId);
       
    
    List<Person> retrival = ( List<Person> ) personInAssociation.getResultList();
  
     return retrival;
        
    }  

    public EntityManager getEntityManager() {
        return entityManager;
    }


    public Person findPersonByPersonAssociationId(Long personAssociationId)
    {
        Query personInAssociation = getEntityManager().createQuery(
            "SELECT person " +
                    "FROM  PersonAssociation personAssociation, " +
                    "      Person person " +
                    "WHERE personAssociation.id = :personAssociationId " +
                    "AND   personAssociation.person.id = person.id"
        );

        personInAssociation.setParameter("personAssociationId", personAssociationId);

        Person found = (Person) personInAssociation.getSingleResult();

        return found;
    }
    
    public PersonAssociation findByPersonIdPersonTypeParentIdParentTypeSilent(Long personId, String personType, Long parentId, String parentType)
    {
    	Query select = getEntityManager().createQuery(
                "SELECT personAssociation " +
                        "FROM  PersonAssociation personAssociation " +
                        "WHERE personAssociation.person.id = :personId " + 
                        "AND personAssociation.personType = :personType " +
                        "AND personAssociation.parentId = :parentId " +
                        "AND personAssociation.parentType = :parentType"
            );
    	
    	select.setParameter("personId", personId);
    	select.setParameter("personType", personType);
    	select.setParameter("parentId", parentId);
    	select.setParameter("parentType", parentType);
    	
    	PersonAssociation retval = null;

    	try
    	{
    		retval = (PersonAssociation) select.getSingleResult();
    	}
    	catch(NoResultException e1)
    	{
    		LOG.debug("There is no any PersonAssociation result for personId=" + personId + ", personType=" + personType + ", parentId=" + parentId + ", parentType=" + parentType);
    	}
    	catch (Exception e2) 
    	{
    		LOG.debug("Cannot take PersonAssociation result for personId=" + personId + ", personType=" + personType + ", parentId=" + parentId + ", parentType=" + parentType);
		}
    	
    	return retval;
    }

    @Transactional
    public void deletePersonAssociationById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT personAssociation " +"FROM  PersonAssociation personAssociation " +
                        "WHERE personAssociation.id = :personAssociationId ");

        queryToDelete.setParameter("personAssociationId", id);

        PersonAssociation personAssociationToBeDeleted = (PersonAssociation) queryToDelete.getSingleResult();
        entityManager.remove(personAssociationToBeDeleted);

    }
}


