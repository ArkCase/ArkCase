/**
 * 
 */
package com.armedia.acm.services.timesheet.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
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
public class GetTimesheetAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private TimesheetService timesheetService;
	
	@RequestMapping(value="/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAllTimesheets(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws AcmListObjectsFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all timesheets.");
		}
		
		String jsonResponse = getTimesheetService().getObjectsFromSolr(TimesheetConstants.OBJECT_TYPE, auth, startRow, maxRows, sort);
		
		if (jsonResponse == null)
		{
			throw new AcmListObjectsFailedException(TimesheetConstants.OBJECT_TYPE, "Could not retrieve list of Timesheets.", new Throwable());
		}
		
		return jsonResponse;
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AcmTimesheet getTimesheet(@PathVariable("id") Long id,
            Authentication auth) throws AcmObjectNotFoundException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking timesheet id=" + id);
		}
		
		AcmTimesheet timesheet = getTimesheetService().get(id);
		
		if (timesheet == null)
		{
			throw new AcmObjectNotFoundException(TimesheetConstants.OBJECT_TYPE, id, "Could not retrieve Timesheet.", new Throwable());
		}
		
		return timesheet;
	}
	
	@RequestMapping(value="/objectId/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AcmTimesheet> getAllTimesheetsForObject(@PathVariable("objectId") Long objectId, @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,			
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            Authentication auth) throws AcmListObjectsFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Taking all timesheets.");
		}
		
		List<AcmTimesheet> timesheets = getTimesheetService().getByObjectId(objectId);
		
		if (timesheets == null)
		{
			throw new AcmListObjectsFailedException(TimesheetConstants.OBJECT_TYPE, "Could not retrieve list of Timesheets.", new Throwable());
		}
		
		return timesheets;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
}
