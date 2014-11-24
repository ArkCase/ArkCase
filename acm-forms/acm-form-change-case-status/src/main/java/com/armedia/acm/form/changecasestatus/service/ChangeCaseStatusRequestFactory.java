/**
 * 
 */
package com.armedia.acm.form.changecasestatus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.services.users.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusRequestFactory 
{

	public ChangeCaseStatus formFromXml(ChangeCaseStatusForm form, Authentication auth)
	{
		ChangeCaseStatus req = new ChangeCaseStatus();
		
		List<AcmParticipant> participants = convertItemsToParticipants(form.getApprovers());
		req.setParticipants(participants);
		req.setCaseId(form.getInformation().getId());
		req.setStatus(form.getInformation().getOption());

		return req;
	}
	
	private List<AcmParticipant> convertItemsToParticipants(List<Item> items)
    {
    	List<AcmParticipant> participants = new ArrayList<>();
    	Logger log = LoggerFactory.getLogger(getClass());
        log.debug("# of incoming approvers: " + items.size());
    	if ( items != null )
    	{
    		for ( Item item : items )
    		{

                AcmParticipant participant = new AcmParticipant();
                participant.setParticipantLdapId(item.getValue());
                participant.setParticipantType("approver");
                participants.add(participant);
    		}
    	}
    	
    	return participants;
    }
	
}
