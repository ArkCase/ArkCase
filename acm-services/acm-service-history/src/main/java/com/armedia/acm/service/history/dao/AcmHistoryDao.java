/**
 * 
 */
package com.armedia.acm.service.history.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.history.model.AcmHistory;

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
