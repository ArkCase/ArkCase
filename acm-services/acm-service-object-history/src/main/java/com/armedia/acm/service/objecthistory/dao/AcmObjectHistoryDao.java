/**
 * 
 */
package com.armedia.acm.service.objecthistory.dao;

import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryDao extends AcmAbstractDao<AcmObjectHistory> {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Class<AcmObjectHistory> getPersistenceClass() {
		return AcmObjectHistory.class;
	}
	
	public AcmObjectHistory safeFindLastInsertedByObjectIdAndObjectType(Long objectId, String objectType)
	{				
		Query select = getEm().createQuery("SELECT objectHistory "
				 + "FROM AcmObjectHistory objectHistory "
				 + "WHERE objectHistory.objectId=:objectId "
				 + "AND objectHistory.objectType=:objectType "
				 + "ORDER BY objectHistory.modified DESC");
		
		select.setParameter("objectId", objectId);
		select.setParameter("objectType", objectType);
		
		// Set first result to 1 because the current object history is saved first and after that this 
		// method is called, so we need second row from the query (which is previous)
		select.setFirstResult(1);
		select.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<AcmObjectHistory> results = (List<AcmObjectHistory>) select.getResultList();
		
		if (results != null && results.size() == 1)
		{
			return results.get(0);
		}
				
		return null;
	}

}
