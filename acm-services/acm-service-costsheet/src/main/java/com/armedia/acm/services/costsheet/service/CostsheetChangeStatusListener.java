/**
 * 
 */
package com.armedia.acm.services.costsheet.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectchangestatus.model.AcmObjectStatus;
import com.armedia.acm.objectchangestatus.model.AcmObjectStatusEvent;
import com.armedia.acm.objectchangestatus.service.ChangeObjectStatusService;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;

/**
 * @author riste.tutureski
 *
 */
public class CostsheetChangeStatusListener implements ApplicationListener<AcmObjectStatusEvent>{

	private ChangeObjectStatusService changeObjectStatusService;
	private CostsheetService costsheetService;
	
	@Override
	public void onApplicationEvent(AcmObjectStatusEvent event) 
	{
		if (getChangeObjectStatusService().isRequiredObject(event, CostsheetConstants.OBJECT_TYPE))
		{
			if (event != null && event.getSource() != null)
			{
				AcmObjectStatus acmObjectStatus = (AcmObjectStatus) event.getSource();
				
				AcmCostsheet costsheet = getCostsheetService().get(acmObjectStatus.getObjectId());
				
				if (costsheet != null)
				{
					costsheet.setStatus(acmObjectStatus.getStatus());
					getCostsheetService().save(costsheet);
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

	public CostsheetService getCostsheetService() {
		return costsheetService;
	}

	public void setCostsheetService(CostsheetService costsheetService) {
		this.costsheetService = costsheetService;
	}
}
