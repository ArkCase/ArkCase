/**
 * 
 */
package com.armedia.acm.services.timesheet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/timesheet", "/api/latest/service/timesheet" })
public class GetTimesheetsAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private TimesheetService timesheetService;
	
	@RequestMapping(value="", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getTimesheets(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws AcmListObjectsFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all timesheets.");
		}
		
		String jsonResponse = getTimesheetService().getObjectsFromSolr(TimesheetConstants.OBJECT_TYPE, auth, startRow, maxRows, sort, null);
		
		if (jsonResponse == null)
		{
			throw new AcmListObjectsFailedException(TimesheetConstants.OBJECT_TYPE, "Could not retrieve list of Timesheets.", new Throwable());
		}
		
		return jsonResponse;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
}
