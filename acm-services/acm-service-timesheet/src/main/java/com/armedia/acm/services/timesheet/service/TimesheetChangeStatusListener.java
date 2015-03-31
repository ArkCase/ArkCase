/**
 * 
 */
package com.armedia.acm.services.timesheet.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectchangestatus.model.AcmObjectStatus;
import com.armedia.acm.objectchangestatus.model.AcmObjectStatusEvent;
import com.armedia.acm.objectchangestatus.service.ChangeObjectStatusService;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetChangeStatusListener implements ApplicationListener<AcmObjectStatusEvent>{

	private ChangeObjectStatusService changeObjectStatusService;
	private TimesheetService timesheetService;
	
	@Override
	public void onApplicationEvent(AcmObjectStatusEvent event) 
	{
		if (getChangeObjectStatusService().isRequiredObject(event, TimesheetConstants.OBJECT_TYPE))
		{
			if (event != null && event.getSource() != null)
			{
				AcmObjectStatus acmObjectStatus = (AcmObjectStatus) event.getSource();
				
				AcmTimesheet timesheet = getTimesheetService().get(acmObjectStatus.getObjectId());
				
				if (timesheet != null)
				{
					timesheet.setStatus(acmObjectStatus.getStatus());
					getTimesheetService().save(timesheet);
				}
			}
		}		
	}

	public ChangeObjectStatusService getChangeObjectStatusService() {
		return changeObjectStatusService;
	}

	public void setChangeObjectStatusService(
			ChangeObjectStatusService changeObjectStatusService) {
		this.changeObjectStatusService = changeObjectStatusService;
	}

	public TimesheetService getTimesheetService() {
		return timesheetService;
	}

	public void setTimesheetService(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
}
