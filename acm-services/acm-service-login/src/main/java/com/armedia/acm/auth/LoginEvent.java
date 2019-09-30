package com.armedia.acm.auth;

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

import org.springframework.security.core.Authentication;

public class LoginEvent extends AcmAbstractAuthenticationEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.login";
    private Throwable cause;

    public LoginEvent(Authentication authentication, String ipAddress)
    {
        super(authentication);
        setIpAddress(ipAddress);
        this.cause = null;
    }

    public LoginEvent(Authentication authentication, String ipAddress, Throwable cause)
    {
        super(authentication);
        setIpAddress(ipAddress);
        this.cause = cause;
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public Authentication getAuthentication()
    {
        return (Authentication) source;
    }

    public Throwable getCause()
    {
        return cause;
    }
}
