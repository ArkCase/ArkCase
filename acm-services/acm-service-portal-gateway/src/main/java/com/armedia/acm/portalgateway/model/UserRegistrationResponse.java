package com.armedia.acm.portalgateway.model;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_ACCEPTED;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_EXISTS;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_INVALID;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_PENDING;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_REJECTED;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_REQUEST_ACCEPTED;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_REQUEST_EXPIRED;
import static com.armedia.acm.portalgateway.model.UserRegistrationResponse.RegistrationStatus.REGISTRATION_REQUEST_REQUIRED;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 11, 2018
 *
 */
public class UserRegistrationResponse
{
    public static enum RegistrationStatus
    {
        REGISTRATION_REQUEST_ACCEPTED, REGISTRATION_EXISTS, REGISTRATION_ACCEPTED, REGISTRATION_PENDING, REGISTRATION_REQUEST_REQUIRED, REGISTRATION_REQUEST_EXPIRED, REGISTRATION_REJECTED, REGISTRATION_INVALID;
    }

    public static UserRegistrationResponse exists()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_EXISTS.name();
        return result;
    }

    public static UserRegistrationResponse requestAccepted()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_REQUEST_ACCEPTED.name();
        return result;
    }

    public static UserRegistrationResponse accepted()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_ACCEPTED.name();
        return result;
    }

    public static UserRegistrationResponse pending(String emailAddress)
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_PENDING.name();
        result.emailAddress = emailAddress;
        return result;
    }

    public static UserRegistrationResponse requestRequired()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_REQUEST_REQUIRED.name();
        return result;
    }

    public static UserRegistrationResponse requestExpired()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_REQUEST_EXPIRED.name();
        return result;
    }

    public static UserRegistrationResponse rejected()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_REJECTED.name();
        return result;
    }

    /**
     * @return
     */
    public static UserRegistrationResponse invalid()
    {
        UserRegistrationResponse result = new UserRegistrationResponse();
        result.registrationStatus = REGISTRATION_INVALID.name();
        return result;
    }

    private String registrationStatus;

    private String emailAddress;

    /**
     * @return the registrationStatus
     */
    public String getRegistrationStatus()
    {
        return registrationStatus;
    }

    /**
     * @param registrationStatus
     *            the registrationStatus to set
     */
    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }
}
