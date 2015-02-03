/**
 * 
 */
package com.armedia.acm.plugins.complaint.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

/**
 * @author riste.tutureski
 *
 */
public class ComplaintAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {

	private static final String COMPLAINT_OBJECT_TYPE = "COMPLAINT";
	
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		if (event != null)
		{		
			AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
			
			if (acmObjectHistory != null &&  COMPLAINT_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{
				// Create JSON unmarshaller
				AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
				
				// Init properties and unarshall current complaint
				Complaint currentComplaint = (Complaint) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), Complaint.class);
				Complaint previousComplaint = null;
				AcmObjectHistory previousObjectHistory = null;
				
				AcmAssignment assignment = new AcmAssignment();
				
				// If current complaint is not null (check just to be sure), get needed information
				if (currentComplaint != null)
				{
					previousObjectHistory = getPreviousObjectHistory(currentComplaint.getComplaintId(), acmObjectHistory.getObjectType());
					
					assignment.setObjectId(currentComplaint.getComplaintId());
					assignment.setObjectNumber(currentComplaint.getComplaintNumber());
					assignment.setObjectName(currentComplaint.getComplaintTitle());
					
					assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(currentComplaint.getParticipants()));
				}
				
				// Unmarshall previous complaint (if there is some history in database)
				if (previousObjectHistory != null)
				{
					previousComplaint = (Complaint) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), Complaint.class);	
				}
				
				// if previous complaint is not null, get needed information
				if (previousComplaint != null)
				{
					assignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(previousComplaint.getParticipants()));
				}
				
				assignment.setObjectType(COMPLAINT_OBJECT_TYPE);
				
				// Raise Event (if assignee is changed)
				raiseEvent(assignment, event.getUserId(), event.getIpAddress());
			}
		}
	}

}
