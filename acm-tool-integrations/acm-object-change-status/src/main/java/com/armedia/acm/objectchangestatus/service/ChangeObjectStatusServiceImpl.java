/**
 * 
 */
package com.armedia.acm.objectchangestatus.service;

import com.armedia.acm.objectchangestatus.model.AcmObjectStatus;
import com.armedia.acm.objectchangestatus.model.AcmObjectStatusEvent;

/**
 * @author riste.tutureski
 *
 */
public class ChangeObjectStatusServiceImpl implements ChangeObjectStatusService {

	private ChangeObjectStatusEventPublisher changeObjectStatusEventPublisher;
	
	@Override
	public void change(Long objectId, String objectType, String status, String userId) 
	{
		AcmObjectStatus acmObjectStatus = new AcmObjectStatus(objectId, objectType, status);
		
		getChangeObjectStatusEventPublisher().publishEvent(acmObjectStatus, userId, true);
	}
	
	@Override
	public boolean isRequiredObject(AcmObjectStatusEvent event, String objectType)
	{
		if (event != null && event.getSource() != null && objectType != null)
		{
			AcmObjectStatus acmObjectStatus = (AcmObjectStatus) event.getSource();
			
			if (objectType.equals(acmObjectStatus.getObjectType()))
			{
				return true;
			}
		}
		
		return false;
	}

	public ChangeObjectStatusEventPublisher getChangeObjectStatusEventPublisher() {
		return changeObjectStatusEventPublisher;
	}

	public void setChangeObjectStatusEventPublisher(
			ChangeObjectStatusEventPublisher changeObjectStatusEventPublisher) {
		this.changeObjectStatusEventPublisher = changeObjectStatusEventPublisher;
	}
	
	

}
