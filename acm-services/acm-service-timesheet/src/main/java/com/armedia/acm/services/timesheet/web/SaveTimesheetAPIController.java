/**
 * 
 */
package com.armedia.acm.services.timesheet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/timesheet", "/api/latest/service/timesheet" })
public class SaveTimesheetAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	private TimesheetService timesheetService;
	
	@RequestMapping(value="", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AcmTimesheet getTimesheet(@RequestBody AcmTimesheet timesheet,
            Authentication auth) throws AcmCreateObjectFailedException
	{
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving timesheet " + timesheet);
		}
		
		try
        {
			AcmTimesheet saved = getTimesheetService().save(timesheet);	
			
			return saved;
		}
	    catch (RuntimeException e)
	    {
	        throw new AcmCreateObjectFailedException(TimesheetConstants.OBJECT_TYPE, e.getMessage(), e);
	    }
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
}
