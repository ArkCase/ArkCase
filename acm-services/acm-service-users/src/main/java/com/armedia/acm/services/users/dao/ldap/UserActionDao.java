/**
 * 
 */
package com.armedia.acm.services.users.dao.ldap;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.users.model.AcmUserAction;

/**
 * @author riste.tutureski
 *
 */
public class UserActionDao extends AcmAbstractDao<AcmUserAction>{
	
	private Logger LOG = LoggerFactory.getLogger(getClass());

	public List<AcmUserAction> findByUserId(String userId)
	{
		List<AcmUserAction> results = null;
		
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery<AcmUserAction> query = builder.createQuery(AcmUserAction.class);
		Root<AcmUserAction> userAction = query.from(AcmUserAction.class);
		
		query.select(userAction);
		
		query.where(
    			builder.and(
    					builder.equal(userAction.<String>get("userId"), userId)
    			)
    	);
		
		TypedQuery<AcmUserAction> dbQuery = getEm().createQuery(query);
		
		try
    	{
    		results = dbQuery.getResultList();
    	}
    	catch(Exception e)
    	{
    		LOG.info("There is no any User Actions connected with user id " + userId);
    	}
		
		return results;
	}
	
	public AcmUserAction findByUserIdAndName(String userId, String name)
	{
		AcmUserAction result = null;
		
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery<AcmUserAction> query = builder.createQuery(AcmUserAction.class);
		Root<AcmUserAction> userAction = query.from(AcmUserAction.class);
		
		query.select(userAction);
		
		query.where(
    			builder.and(
    					builder.equal(userAction.<String>get("userId"), userId)
    			),
    			builder.and(
    					builder.equal(userAction.<String>get("name"), name)
    			)
    	);
		
		TypedQuery<AcmUserAction> dbQuery = getEm().createQuery(query);
		
		try
    	{
    		result = dbQuery.getSingleResult();
    	}
    	catch(Exception e)
    	{
    		LOG.info("There is no any User Action connected with user id " + userId + " with action name " + name);
    	}
		
		return result;
	}
	
	@Override
	protected Class<AcmUserAction> getPersistenceClass() {
		return AcmUserAction.class;
	}

}
