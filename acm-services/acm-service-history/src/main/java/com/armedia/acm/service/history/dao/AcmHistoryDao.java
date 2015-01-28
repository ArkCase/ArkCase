/**
 * 
 */
package com.armedia.acm.service.history.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.history.model.AcmHistory;
import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author riste.tutureski
 *
 */
public class AcmHistoryDao extends AcmAbstractDao<AcmHistory> {

	@Override
	protected Class<AcmHistory> getPersistenceClass() {
		return AcmHistory.class;
	}
	
	@Transactional
	public List<AcmHistory> findByPersonId(Long personId)
	{
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
    	CriteriaQuery<AcmHistory> query = builder.createQuery(AcmHistory.class);
    	Root<AcmHistory> history = query.from(AcmHistory.class);
    	
    	query.select(history);
    	
    	query.where(
    			builder.and(
    					builder.equal(history.<Long>get("personId"), personId)
    			)
    	);
    	
    	TypedQuery<AcmHistory> dbQuery = getEm().createQuery(query);
    	List<AcmHistory> results = dbQuery.getResultList();
    	
    	return results;
	}
	
	@Transactional
    public int deleteByPersonIdAndObjectType(Long personId, String objectType)
    {
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
   	 
    	CriteriaDelete<AcmHistory> delete = builder.createCriteriaDelete(AcmHistory.class);
    	Root<AcmHistory> acmHistory = delete.from(AcmHistory.class);
    	
    	delete.where(
    			builder.and(
    					builder.equal(acmHistory.<Long>get("personId"), personId)
    			),
				builder.and(
    					builder.equal(acmHistory.<String>get("objectType"), objectType)
    			)	
    	);
    	
    	Query query = getEm().createQuery(delete);
    	
    	return query.executeUpdate();
    }

}
