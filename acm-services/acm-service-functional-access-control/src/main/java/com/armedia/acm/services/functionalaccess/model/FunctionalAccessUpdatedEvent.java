package com.armedia.acm.services.functionalaccess.model;

/*-
 * #%L
 * ACM Service: Functional Access Control
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
 * @author riste.tutureski
 *
 */
public class FunctionalAccessUpdatedEvent extends AcmEvent
{

    private static final long serialVersionUID = 2731024788487648140L;

    private static final String EVENT_TYPE = "com.armedia.acm.functionalaccess.updated";

    public FunctionalAccessUpdatedEvent(Object source, Authentication auth, String ipAddress)
    {
        super(source);
        setEventDate(new Date());
        setUserId(auth.getName());
        setIpAddress(ipAddress);
    }

    public FunctionalAccessUpdatedEvent(Object source, String userId, String ipAddress)
    {
        super(source);
        setEventDate(new Date());
        setUserId(userId);
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
