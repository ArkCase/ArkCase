/**
 * 
 */
package com.armedia.acm.services.users.dao.group;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
public class AcmGroupDao extends AcmAbstractDao<AcmGroup>{
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Transactional
	public AcmGroup findByName(String name)
	{
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	CriteriaQuery<AcmGroup> query = builder.createQuery(AcmGroup.class);
    	Root<AcmGroup> group = query.from(AcmGroup.class);
    	
    	query.select(group);
    	
    	query.where(
    			builder.and(
    					builder.equal(group.<String>get("name"), name)
    			)
    	);
    	
    	TypedQuery<AcmGroup> dbQuery = getEm().createQuery(query);
    	
    	AcmGroup retval = null;
    	
    	try
    	{
    		retval = dbQuery.getSingleResult();
    	}
    	catch(Exception e)
    	{
    		if (e instanceof NoResultException)
    		{
    			LOG.info("There is no any group with name = " + name);
    		}
    		else if (e instanceof NonUniqueResultException)
    		{
    			LOG.info("There is no unique group found with name = " + name +". More than one group has this name.");
    		}
    		else
    		{
    			LOG.error("Error while retrieving group by group name = " + name, e);
    		}
    	}
    	
    	return retval;
	}
	
	@Transactional
    public boolean deleteAcmGroupByName(String name)
    {           
        Query queryToDelete = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.name = :groupName");
        queryToDelete.setParameter("groupName", name); 
        
       AcmGroup groupToBeDeleted = (AcmGroup) queryToDelete.getSingleResult();
       if (groupToBeDeleted != null)
       {
    	   getEm().remove(groupToBeDeleted);
    	   return true;
       }
       else
       {
    	   return false;
       }
    } 
	
	@Transactional
	public void markAllGroupsInactive(String groupType)
	{
		 Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.type = :groupType");
		 query.setParameter("groupType", groupType); 
     
		 List<AcmGroup> groups =  query.getResultList();
		 
		 if (groups != null && groups.size() > 0)
		 {
			 for (AcmGroup group : groups)
			 {
				 group.setStatus("INACTIVE");
				 save(group);
			 }
		 }
	}
	
	@Transactional
	public List<AcmGroup> findByUserMember(AcmUser user)
	{		
		Query query = getEm().createQuery("SELECT group FROM AcmGroup group WHERE group.members = :user");
		query.setParameter("user", user);
		
		List<AcmGroup> groups =  query.getResultList();
		
		return groups;
	}
	
	@Override
	protected Class<AcmGroup> getPersistenceClass() {
		return AcmGroup.class;
	}

}
