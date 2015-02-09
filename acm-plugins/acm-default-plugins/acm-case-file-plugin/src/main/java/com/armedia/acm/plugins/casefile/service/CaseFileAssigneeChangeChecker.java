/**
 * 
 */
package com.armedia.acm.plugins.casefile.service;

import org.springframework.context.ApplicationListener;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChacker;
import com.armedia.acm.services.participants.utils.ParticipantUtils;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileAssigneeChangeChecker extends AcmAssigneeChangeChacker implements ApplicationListener<AcmObjectHistoryEvent> {
	
	@Override
	public void onApplicationEvent(AcmObjectHistoryEvent event) 
	{
		super.onApplicationEvent(event);
	}

	@Override
	public Class<?> getTargetClass() 
	{
		return CaseFile.class;
	}
	
	@Override
	public String getObjectType(Object in) 
	{
		CaseFile caseFile = (CaseFile) in;
		
		if (caseFile != null)
		{
			return caseFile.getObjectType();
		}
		
		return null;
	}

	@Override
	public Long getObjectId(Object in) 
	{
		CaseFile caseFile = (CaseFile) in;
		
		if (caseFile != null)
		{
			return caseFile.getId();
		}
		
		return null;
	}

	@Override
	public String getObjectTitle(Object in) 
	{
		CaseFile caseFile = (CaseFile) in;
		
		if (caseFile != null)
		{
			return caseFile.getTitle();
		}
		
		return null;
	}

	@Override
	public String getObjectName(Object in) 
	{
		CaseFile caseFile = (CaseFile) in;
		
		if (caseFile != null)
		{
			return caseFile.getCaseNumber();
		}
		
		return null;
	}

	@Override
	public String getAssignee(Object in) 
	{
		CaseFile caseFile = (CaseFile) in;
		
		if (caseFile != null)
		{
			return ParticipantUtils.getAssigneeIdFromParticipants(caseFile.getParticipants());
		}
		
		return null;
	}
	
}
