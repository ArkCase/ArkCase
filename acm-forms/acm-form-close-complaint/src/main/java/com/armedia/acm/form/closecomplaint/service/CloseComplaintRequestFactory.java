package com.armedia.acm.form.closecomplaint.service;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import com.armedia.acm.services.participants.model.AcmParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 10/17/14.
 */
public class CloseComplaintRequestFactory
{
    /**
     * Assumes each CloseComplaintRequest is new. Must be updated when we support editing the form.
     * @param form
     * @return
     */
    public CloseComplaintRequest fromFormXml(
            CloseComplaintForm form,
            Authentication auth)
    {
        CloseComplaintRequest req = new CloseComplaintRequest();

        List<AcmParticipant> participants = convertItemsToParticipants(form.getApprovers());
        req.setParticipants(participants);
        req.setComplaintId(form.getInformation().getId());

        populateDisposition(form, auth, req);

        return req;
    }

    private void populateDisposition(CloseComplaintForm form, Authentication auth, CloseComplaintRequest req)
    {
        Disposition disposition = new Disposition();
        req.setDisposition(disposition);

        if ( form.getInformation() != null )
        {
            disposition.setCloseDate(form.getInformation().getDate());
            disposition.setDispositionType(form.getInformation().getOption());
        }

        if ( form.getExistingCase() != null )
        {
            disposition.setExistingCaseNumber(form.getExistingCase().getCaseNumber());
        }

        if ( form.getReferExternal() != null )
        {
            disposition.setReferExternalContactPersonName(form.getReferExternal().getPerson());
            disposition.setReferExternalOrganizationName(form.getReferExternal().getAgency());
            disposition.setReferExternalContactMethod(form.getReferExternal().getContact());
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
