package com.armedia.acm.auth.web;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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
import com.armedia.acm.services.users.model.AcmUser;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;

public class ForgotUsernameEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.forgotusername";

    public ForgotUsernameEvent(AcmUser acmUser, String ipAddress)
    {
        super(acmUser);
        setUserId(acmUser.getUserId());
        setEventDate(new Date());
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public AcmUser getAcmUser()
    {
        return (AcmUser) getSource();
    }
}
