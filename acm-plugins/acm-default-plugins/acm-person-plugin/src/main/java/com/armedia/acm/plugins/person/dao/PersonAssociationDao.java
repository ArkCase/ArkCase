package com.armedia.acm.plugins.person.dao;



import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import java.util.ArrayList;
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
       
        "SELECT person FROM PersonAssociation personAssociation, Person person "+
                   "WHERE personAssociation.parentId = :parentId " +
                   "AND personAssociation.parentType= :parentType " +
                   "AND personAssociation.person.id= person.id"
                       ) ;
          
        personInAssociation.setParameter("parentId" ,parentId);
       personInAssociation.setParameter("parentType" ,parentType); 
       
    
    List<Person> retrival = personInAssociation.getResultList();
  
     return retrival;
        
    }
   
   public Integer deletePersonByIdFromPersonAssociation(Long personId) 
   {
       Query deletePerson = getEntityManager().createQuery(
                           "DELETE FROM PersonAssociation personAssociation "+
                                   "WHERE personAssociation.person.id= :personId"        
                            );
            Integer deletedCount = deletePerson.setParameter("personId", personId).executeUpdate();
      
       return deletedCount;
   }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    

}


