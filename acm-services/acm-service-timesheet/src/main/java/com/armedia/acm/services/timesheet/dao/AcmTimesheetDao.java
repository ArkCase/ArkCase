/**
 * 
 */
package com.armedia.acm.services.timesheet.dao;

import java.util.Date;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetDao extends AcmAbstractDao<AcmTimesheet> {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
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
		
		AcmTimesheet timesheet = null;
		try
		{
			timesheet = (AcmTimesheet) selectQuery.getSingleResult();
		}
		catch (Exception e)
		{
			LOG.warn("Timesheet for period of " + startDate + " to " + endDate + " is not found.");
		}
		
		return timesheet;
	}

}
