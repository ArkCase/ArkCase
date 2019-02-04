package com.armedia.acm.services.dataaccess.model;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.MDC;

import java.util.Date;
import java.util.List;

public class AcmEntityParticipantsChangedEvent extends AcmEvent
{
    public static final String EVENT_TYPE = "com.armedia.acm.object.participants.change";
    private static final long serialVersionUID = 1L;
    private List<AcmParticipant> originalParticipants;

    public AcmEntityParticipantsChangedEvent(AcmObject source, List<AcmParticipant> originalParticipants, String ipAddress)
    {
        super(source);
        setObjectId(source.getId());
        setObjectType(source.getObjectType());
        setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) == null ? "SYSTEM"
                : MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
        setSucceeded(true);
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setOriginalParticipants(originalParticipants);
        setIpAddress(ipAddress);
    }

    public List<AcmParticipant> getOriginalParticipants()
    {
        return originalParticipants;
    }

    public void setOriginalParticipants(List<AcmParticipant> originalParticipants)
    {
        this.originalParticipants = originalParticipants;
    }
}
