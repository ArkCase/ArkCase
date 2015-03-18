/**
 * 
 */
package com.armedia.acm.services.timesheet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/timesheet", "/api/latest/timesheet" })
public class SaveTimesheetDetailsAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private TimesheetService timesheetService;
	
	@RequestMapping(value="/{id}/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AcmTimesheet getTimesheet(@PathVariable("id") Long id, @RequestBody String details,
            Authentication auth) throws AcmObjectNotFoundException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving details for timesheet id=" + id);
			LOG.info("details=" + details);
		}
		
		AcmTimesheet timesheet = getTimesheetService().get(id);
		
		if (timesheet == null)
		{
			throw new AcmObjectNotFoundException(TimesheetConstants.OBJECT_TYPE, id, "Could not retrieve Timesheet.", new Throwable());
		}
		
		timesheet.setDetails(details);
		
		AcmTimesheet saved = getTimesheetService().save(timesheet);
		
		return saved;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
}
