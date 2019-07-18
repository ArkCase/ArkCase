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
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.PASSWORD_UPDATED;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REGISTRATION_REQUIRED;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_ACCEPTED;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_EXISTS;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_EXPIRED;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_INVALID;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_PENDING;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.REQUEST_REQUIRED;
import static com.armedia.acm.portalgateway.model.UserResetResponse.ResetStatus.INVALID_CREDENTIALS_STATUS;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 17, 2018
 *
 */
public class UserResetResponse
{

    public static enum ResetStatus
    {
        REGISTRATION_REQUIRED, REQUEST_REQUIRED, REQUEST_EXISTS, REQUEST_PENDING, REQUEST_ACCEPTED, REQUEST_EXPIRED, PASSWORD_UPDATED, REQUEST_INVALID, INVALID_CREDENTIALS_STATUS
    }

    /**
     * @return
     */
    public static UserResetResponse reqistrationRequired()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REGISTRATION_REQUIRED.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse exists()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_EXISTS.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse pending()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_PENDING.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse passwordUpdated()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = PASSWORD_UPDATED.name();
        return response;
    }


    /**
     * @return
     */
    public static UserResetResponse invalidCredentials()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = INVALID_CREDENTIALS_STATUS.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse requestAccepted()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_ACCEPTED.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse requestRequired()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_REQUIRED.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse requestExpired()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_EXPIRED.name();
        return response;
    }

    /**
     * @return
     */
    public static UserResetResponse invalid()
    {
        UserResetResponse response = new UserResetResponse();
        response.resetStatus = REQUEST_INVALID.name();
        return response;
    }

    private String resetStatus;

    /**
     * @return the resetStatus
     */
    public String getResetStatus()
    {
        return resetStatus;
    }

    /**
     * @param resetStatus
     *            the resetStatus to set
     */
    public void setResetStatus(String resetStatus)
    {
        this.resetStatus = resetStatus;
    }

}
