/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public interface TimesheetService {

	public AcmTimesheet save(AcmTimesheet timesheet);
	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName);
	public AcmTimesheet get(Long id);
	public List<AcmTimesheet> getByObjectId(Long objectId);
	public String getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams);
	
}
