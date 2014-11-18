/**
 * 
 */
package com.armedia.acm.form.closecase.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.form.closecase.model.CloseCaseForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.CloseCaseRequest;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.services.users.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
public class CloseCaseRequestFactory 
{

	public CloseCaseRequest formFromXml(CloseCaseForm form, Authentication auth)
	{
		CloseCaseRequest req = new CloseCaseRequest();
		
		List<AcmParticipant> participants = convertItemsToParticipants(form.getApprovers());
		req.setParticipants(participants);
		req.setCaseId(form.getInformation().getId());
		
		populateDisposition(form, auth, req);
		
		return req;
	}
	
	private void populateDisposition(CloseCaseForm form, Authentication auth, CloseCaseRequest req)
    {
        Disposition disposition = new Disposition();
        req.setDisposition(disposition);

        if ( form.getInformation() != null )
        {
            disposition.setCloseDate(form.getInformation().getCloseDate());
            disposition.setDispositionType(form.getInformation().getDisposition());
        }
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
