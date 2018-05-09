package com.armedia.acm.plugins.casefile.model;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.core.model.AcmEvent;

import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 9/4/14.
 */
public class CaseEvent extends AcmEvent
{
    private Authentication eventUser;
    private CaseFile caseFile;

    public CaseEvent(CaseFile source, String ipAddress, String user, String eventType, Date eventDate, boolean userActionSucceeded,
            Authentication eventUser)
    {
        super(source);

        setSucceeded(userActionSucceeded);
        setUserId(user);
        setObjectId(source.getId());
        setEventDate(eventDate);
        setIpAddress(ipAddress);
        setEventType(eventType);
        setObjectType("CASE_FILE");
        setEventUser(eventUser);
        setCaseFile(source);
    }

    public CaseEvent(CaseFile source, String ipAddress, String user, String eventType, String eventDescription, Date eventDate,
            boolean userActionSucceeded, Authentication eventUser)
    {
        this(source, ipAddress, user, eventType, eventDate, userActionSucceeded, eventUser);
        setEventDescription(eventDescription);
    }

    public Authentication getEventUser()
    {
        return eventUser;
    }

    public void setEventUser(Authentication eventUser)
    {
        this.eventUser = eventUser;
    }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }
}
