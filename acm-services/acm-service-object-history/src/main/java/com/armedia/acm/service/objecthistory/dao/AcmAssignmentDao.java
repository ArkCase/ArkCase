/**
 * 
 */
package com.armedia.acm.service.objecthistory.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;

/**
 * @author riste.tutureski
 *
 */
public class AcmAssignmentDao extends AcmAbstractDao<AcmAssignment> {


	@Override
	protected Class<AcmAssignment> getPersistenceClass() 
	{
		return AcmAssignment.class;
	}
	
	public List<AcmAssignment> executeQuery(Date lastRunDate, int currentBatchSize, int batchSize, String query)
	{
		Query select = getEm().createQuery(query);
		
		select.setParameter("lastRunDate", lastRunDate);
		select.setFirstResult(currentBatchSize);
		select.setMaxResults(batchSize);
		
		List<AcmAssignment> retval = select.getResultList();
		
		return retval;
	}

}
