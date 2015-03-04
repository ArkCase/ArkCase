/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import java.util.Map;

import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetServiceImpl implements TimesheetService {

	private AcmTimesheetDao acmTimesheetDao;
	private Map<String, String> submissionStatusesMap;
	
	@Override
	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName) 
	{
		timesheet.setStatus(getSubmissionStatusesMap().get(submissionName));
		AcmTimesheet saved = getAcmTimesheetDao().save(timesheet);
		
		return saved;
	}

	public AcmTimesheetDao getAcmTimesheetDao() {
		return acmTimesheetDao;
	}

	public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao) {
		this.acmTimesheetDao = acmTimesheetDao;
	}

	public Map<String, String> getSubmissionStatusesMap() {
		return submissionStatusesMap;
	}

	public void setSubmissionStatusesMap(Map<String, String> submissionStatusesMap) {
		this.submissionStatusesMap = submissionStatusesMap;
	}

}
