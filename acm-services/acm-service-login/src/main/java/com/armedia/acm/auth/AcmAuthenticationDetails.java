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

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class AcmAuthenticationDetails extends WebAuthenticationDetails
{
    private String userIpAddress;
    private String cmisUserId;

    /**
     * Records the remote address and will also set the session id if a session
     * already exists (it won't create one).
     *
     * @param request
     *            that the authentication request was received from
     */
    public AcmAuthenticationDetails(HttpServletRequest request)
    {
        super(request);
    }

    /**
     * Override this to return the user's actual IP address if behind a proxy server or load balancer. Default
     * Spring Security behavior will show the proxy server / load balancer IP address, not the user's real IP
     * address.
     * 
     * @return the user's actual IP address, even when they are behind a proxy or load balancer
     */
    @Override
    public String getRemoteAddress()
    {
        return userIpAddress == null ? super.getRemoteAddress() : userIpAddress;
    }

    public String getUserIpAddress()
    {
        return userIpAddress;
    }

    public void setUserIpAddress(String userIpAddress)
    {
        this.userIpAddress = userIpAddress;
    }

    public String getCmisUserId()
    {
        return cmisUserId;
    }

    public void setCmisUserId(String cmisUserId)
    {
        this.cmisUserId = cmisUserId;
    }
}
