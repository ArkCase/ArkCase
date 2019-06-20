package com.armedia.acm.form.closecomplaint.service;

/*-
 * #%L
 * ACM Forms: Close Complaint
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.form.closecomplaint.model.CloseComplaintForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 10/17/14.
 */
public class CloseComplaintRequestFactory
{
    /**
     * Assumes each CloseComplaintRequest is new. Must be updated when we support editing the form.
     *
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

        req.setDescription(form.getDescription());

        return req;
    }

    private void populateDisposition(CloseComplaintForm form, Authentication auth, CloseComplaintRequest req)
    {
        Disposition disposition = new Disposition();
        req.setDisposition(disposition);

        if (form.getInformation() != null)
        {
            // convert java.util.Date to LocalDate
            if (form.getInformation().getDate() != null && form.getInformation().getDate().toInstant() != null)
            {
                disposition.setCloseDate(form.getInformation().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            disposition.setDispositionType(form.getInformation().getOption());
        }

        if (form.getExistingCase() != null)
        {
            disposition.setExistingCaseNumber(form.getExistingCase().getCaseNumber());
        }

        if (form.getReferExternal() != null)
        {
            disposition.setReferExternalContactPersonName(form.getReferExternal().getPerson());
            disposition.setReferExternalOrganizationName(form.getReferExternal().getAgency());
            disposition.setReferExternalContactMethod(form.getReferExternal().getContact().returnBase());
        }
    }

    private List<AcmParticipant> convertItemsToParticipants(List<Item> items)
    {
        List<AcmParticipant> participants = new ArrayList<>();
        Logger log = LogManager.getLogger(getClass());
        log.debug("# of incoming approvers: " + items.size());
        if (items != null)
        {
            for (Item item : items)
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
