/**
 * 
 */
package com.armedia.acm.service.objecthistory.dao;

import javax.persistence.Query;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryDao extends AcmAbstractDao<AcmObjectHistory> {
	
	@Override
	protected Class<AcmObjectHistory> getPersistenceClass() {
		return AcmObjectHistory.class;
	}
	
	public AcmObjectHistory findLastInsertedByObjectType(String objectType)
	{				
		Query select = getEm().createQuery("SELECT objectHistory "
										 + "FROM AcmObjectHistory objectHistory "
										 + "WHERE objectHistory.modified=("
										 		+ "SELECT MAX(objectHistory.modified) "
										 		+ "FROM AcmObjectHistory objectHistory "
										 		+ "WHERE objectHistory.modified<(SELECT MAX(objectHistory.modified) FROM AcmObjectHistory objectHistory)"
										 + ") "
										 + "AND objectHistory.objectType=:objectType");
		
		select.setParameter("objectType", objectType);

		AcmObjectHistory retval = (AcmObjectHistory) select.getSingleResult();			
				
		return retval;
	}

}
