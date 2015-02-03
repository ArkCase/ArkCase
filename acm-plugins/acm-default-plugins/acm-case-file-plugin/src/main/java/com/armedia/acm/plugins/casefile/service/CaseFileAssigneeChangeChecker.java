/**
 * 
 */
package com.armedia.acm.plugins.casefile.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.objectonverter.AcmUnmarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {

	private static final String CASE_FILE_OBJECT_TYPE = "CASE_FILE";
	
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		if (event != null)
		{		
			AcmObjectHistory acmObjectHistory = (AcmObjectHistory) event.getSource();
			
			if (acmObjectHistory != null &&  CASE_FILE_OBJECT_TYPE.equals(acmObjectHistory.getObjectType()))
			{
				// Create JSON unmarshaller
				AcmUnmarshaller unmarshaller = ObjectConverter.createJSONUnmarshaller();
				
				// Init properties and unarshall current case file
				CaseFile currentCaseFile = (CaseFile) unmarshaller.unmarshall(acmObjectHistory.getObjectString(), CaseFile.class);
				CaseFile previousCaseFile = null;
				AcmObjectHistory previousObjectHistory = null;
				
				AcmAssignment assignment = new AcmAssignment();
				
				// If current case file is not null (check just to be sure), get needed information
				if (currentCaseFile != null)
				{
					previousObjectHistory = getPreviousObjectHistory(currentCaseFile.getId(), acmObjectHistory.getObjectType());
					
					assignment.setObjectId(currentCaseFile.getId());
					assignment.setObjectNumber(currentCaseFile.getCaseNumber());
					assignment.setObjectName(currentCaseFile.getTitle());
					
					assignment.setNewAssignee(ParticipantUtils.getAssigneeIdFromParticipants(currentCaseFile.getParticipants()));
				}
				
				// Unmarshall previous case file (if there is some history in database)
				if (previousObjectHistory != null)
				{
					previousCaseFile = (CaseFile) unmarshaller.unmarshall(previousObjectHistory.getObjectString(), CaseFile.class);	
				}
				
				// if previous case file is not null, get needed information
				if (previousCaseFile != null)
				{
					assignment.setOldAssignee(ParticipantUtils.getAssigneeIdFromParticipants(previousCaseFile.getParticipants()));
				}
				
				assignment.setObjectType(CASE_FILE_OBJECT_TYPE);
				
				// Raise Event (if assignee is changed)
				raiseEvent(assignment, event.getUserId(), event.getIpAddress());
			}
		}
	}
	
}
