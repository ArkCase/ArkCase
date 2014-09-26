package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class PersonAssociationDao extends AcmAbstractDao<PersonAssociation>
{
    
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
}


