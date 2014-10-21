package com.armedia.acm.form.closecomplaint.service;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

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

        req.setModifier(auth.getName());
        req.setCreator(auth.getName());

        req.setApprovers(convertItemsToList(form.getApprovers()));
        req.setComplaintId(form.getInformation().getComplaintId());

        populateDisposition(form, auth, req);

        return req;
    }

    private void populateDisposition(CloseComplaintForm form, Authentication auth, CloseComplaintRequest req)
    {
        Disposition disposition = new Disposition();
        req.setDisposition(disposition);

        disposition.setCreator(auth.getName());
        disposition.setModifier(auth.getName());

        if ( form.getInformation() != null )
        {
            disposition.setCloseDate(form.getInformation().getCloseDate());
            disposition.setDispositionType(form.getInformation().getDisposition());
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

            if ( disposition.getReferExternalContactMethod() != null )
            {
                disposition.getReferExternalContactMethod().setCreator(auth.getName());
                disposition.getReferExternalContactMethod().setModifier(auth.getName());
            }
        }
    }
    
    private List<String> convertItemsToList(List<Item> items)
    {
    	List<String> itemsString = new ArrayList<String>();
    	
    	if (items != null && items.size() > 0)
    	{
    		for (int i = 0; i < items.size(); i++) 
    		{
    			itemsString.add(items.get(i).getValue());
    		}
    	}
    	
    	return itemsString;
    }
}
