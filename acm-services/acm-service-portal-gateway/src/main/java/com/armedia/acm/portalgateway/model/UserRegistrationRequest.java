package com.armedia.acm.portalgateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 11, 2018
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserRegistrationRequest
{

    private String emailAddress;

    /**
     * The URL is needed to construct the redirect URL in the email sent to the user that requested registration.
     */
    private String registrationUrl;

    /**
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * @param emailAddress
     *            the emailAddress to set
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the registrationUrl
     */
    public String getRegistrationUrl()
    {
        return registrationUrl;
    }

    /**
     * @param registrationUrl
     *            the registrationUrl to set
     */
    public void setRegistrationUrl(String registrationUrl)
    {
        this.registrationUrl = registrationUrl;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "UserRegistrationRequest [emailAddress=" + emailAddress + ", registrationUrl=" + registrationUrl + "]";
    }

}
