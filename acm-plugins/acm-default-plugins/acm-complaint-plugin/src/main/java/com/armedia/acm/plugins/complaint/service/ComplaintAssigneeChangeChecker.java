/**
 * 
 */
package com.armedia.acm.plugins.complaint.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {

	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		super.onApplicationEvent(event);
	}

	@Override
	public Class<?> getTargetClass() 
	{
		return Complaint.class;
	}
	
	@Override
	public String getObjectType(Object in) 
	{
		Complaint complaint = (Complaint) in;
		
		if (complaint != null)
		{
			return complaint.getObjectType();
		}
		
		return null;
	}

	@Override
	public Long getObjectId(Object in) 
	{
		Complaint complaint = (Complaint) in;
		
		if (complaint != null)
		{
			return complaint.getComplaintId();
		}
		
		return null;
	}

	@Override
	public String getObjectTitle(Object in) 
	{
		Complaint complaint = (Complaint) in;
		
		if (complaint != null)
		{
			return complaint.getComplaintTitle();
		}
		
		return null;
	}

	@Override
	public String getObjectName(Object in) 
	{
		Complaint complaint = (Complaint) in;
		
		if (complaint != null)
		{
			return complaint.getComplaintNumber();
		}
		
		return null;
	}

	@Override
	public String getAssignee(Object in) 
	{
		Complaint complaint = (Complaint) in;
		
		if (complaint != null)
		{
			return ParticipantUtils.getAssigneeIdFromParticipants(complaint.getParticipants());
		}
		
		return null;
	}

}
