package com.armedia.acm.form.closecomplaint.service;

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

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

        // TODO: support multiple approvers
        req.setApprovers(Arrays.asList(form.getApprover().getApproverId()));
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
}
