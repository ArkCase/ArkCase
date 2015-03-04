/**
 * 
 */
package com.armedia.acm.services.timesheet.dao;

import java.util.Date;

import javax.persistence.Query;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetDao extends AcmAbstractDao<AcmTimesheet> {

	@Override
	protected Class<AcmTimesheet> getPersistenceClass() 
	{
		return AcmTimesheet.class;
	}
	
	public AcmTimesheet findByUserIdStartAndEndDate(String userId, Date startDate, Date endDate)
	{
		Query selectQuery = getEm().createQuery("SELECT timesheet "
											  + "FROM AcmTimesheet timesheet "
											  + "WHERE timesheet.userId = :userId "
											  + "AND timesheet.startDate = :startDate "
											  + "AND timesheet.endDate = :endDate");
		
		selectQuery.setParameter("userId", userId);
		selectQuery.setParameter("startDate", startDate);
		selectQuery.setParameter("endDate", endDate);
		
		AcmTimesheet timesheet = (AcmTimesheet) selectQuery.getSingleResult();
		
		return timesheet;
	}

}
