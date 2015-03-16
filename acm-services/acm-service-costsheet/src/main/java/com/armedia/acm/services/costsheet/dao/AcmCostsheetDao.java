/**
 * 
 */
package com.armedia.acm.services.costsheet.dao;

import java.util.Date;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;

/**
 * @author riste.tutureski
 *
 */
public class AcmCostsheetDao extends AcmAbstractDao<AcmCostsheet> {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Override
	protected Class<AcmCostsheet> getPersistenceClass() 
	{
		return AcmCostsheet.class;
	}

	public AcmCostsheet findByUserIdAndObjectId(String userId, Long objectId)
	{
		Query selectQuery = getEm().createQuery("SELECT costsheet "
											  + "FROM AcmCostsheet costsheet "
											  + "WHERE costsheet.userId = :userId "
											  + "AND costsheet.parentId = :objectId");
		
		selectQuery.setParameter("userId", userId);
		selectQuery.setParameter("objectId", objectId);
		
		AcmCostsheet costsheet = null;
		try
		{
			costsheet = (AcmCostsheet) selectQuery.getSingleResult();
		}
		catch (Exception e)
		{
			LOG.warn("Costsheet for objectId " + objectId + " is not found.");
		}
		
		return costsheet;
	}
	
}
