/**
 * 
 */
package com.armedia.acm.form.changecasestatus.service;

/*-
 * #%L
 * ACM Forms: Change Case Status
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

import com.armedia.acm.form.changecasestatus.model.ChangeCaseStatusForm;
import com.armedia.acm.form.config.Item;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

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
