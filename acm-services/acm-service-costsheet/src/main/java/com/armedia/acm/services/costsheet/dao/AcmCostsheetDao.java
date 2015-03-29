/**
 * 
 */
package com.armedia.acm.services.costsheet.dao;

import java.util.List;

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

	public AcmCostsheet findByUserIdObjectIdAndType(String userId, Long objectId, String objectType)
	{
		Query selectQuery = getEm().createQuery("SELECT costsheet "
											  + "FROM AcmCostsheet costsheet "
											  + "WHERE costsheet.user.userId = :userId "
											  + "AND costsheet.parentId = :objectId "
											  + "AND costsheet.parentType = :objectType");
		
		selectQuery.setParameter("userId", userId);
		selectQuery.setParameter("objectId", objectId);
		selectQuery.setParameter("objectType", objectType);
		
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
	
	public List<AcmCostsheet> findByObjectIdAndType(Long objectId, String objectType, int startRow, int maxRows, String sortParams)
	{
		String orderByQuery = "";
		if (sortParams != null && !"".equals(sortParams))
		{
			orderByQuery = " ORDER BY costsheet." + sortParams;
		}
		
		Query selectQuery = getEm().createQuery("SELECT costsheet "
											  + "FROM AcmCostsheet costsheet "
											  + "WHERE costsheet.parentId = :parentId "
											  + "AND costsheet.parentType = :parentType"
											  + orderByQuery);
		
		selectQuery.setParameter("parentId", objectId);
		selectQuery.setParameter("parentType", objectType);
		selectQuery.setFirstResult(startRow);
		selectQuery.setMaxResults(maxRows);
		
		@SuppressWarnings("unchecked")
		List<AcmCostsheet> costsheets = (List<AcmCostsheet>) selectQuery.getResultList();
		
		return costsheets;
	}
	
}
