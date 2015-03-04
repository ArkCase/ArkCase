/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public interface TimesheetService {

	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName);
	
}
