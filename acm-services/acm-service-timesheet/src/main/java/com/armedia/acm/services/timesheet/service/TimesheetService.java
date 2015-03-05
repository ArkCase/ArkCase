/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;

/**
 * @author riste.tutureski
 *
 */
public interface TimesheetService {

	public AcmTimesheet save(AcmTimesheet timesheet, String submissionName);
	public JSONObject getObjectsFromSolr(String objectType, Authentication authentication, int startRow, int maxRows, String sortParams);
	
}
