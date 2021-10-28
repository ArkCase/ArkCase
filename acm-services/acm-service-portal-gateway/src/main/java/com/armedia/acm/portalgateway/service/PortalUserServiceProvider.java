package com.armedia.acm.portalgateway.service;

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

import com.armedia.acm.portalgateway.model.PortalUser;
import com.armedia.acm.portalgateway.model.PortalUserCredentials;
import com.armedia.acm.portalgateway.model.UserRegistrationRequest;
import com.armedia.acm.portalgateway.model.UserRegistrationResponse;
import com.armedia.acm.portalgateway.model.UserResetRequest;
import com.armedia.acm.portalgateway.model.UserResetResponse;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 11, 2018
 *
 */
public interface PortalUserServiceProvider
{

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#requestRegistration(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserRegistrationRequest)
     */
    UserRegistrationResponse regenerateRegistrationRequest(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException;

    /**
     * @param portalId
     * @param registrationRequest
     * @return
     */
    UserRegistrationResponse requestRegistration(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException;

    /**
     * @param portalId
     * @param registrationId
     * @return
     */
    UserRegistrationResponse checkRegistrationStatus(String portalId, String registrationId);

    /**
     * @param portalId
     * @param registrationId
     * @param user
     * @param password
     * @return
     */
    UserRegistrationResponse registerUser(String portalId, String registrationId, PortalUser user, String password)
            throws PortalUserServiceException;

    /**
     * @param portalId
     * @param user
     * @return
     */
    PortalUser updateUser(String portalId, PortalUser user) throws PortalUserServiceException;

    UserRegistrationResponse registerUserFromPerson(String portalId, Long personId, Long requestId)
            throws PortalUserServiceException;

    UserResetResponse requestPasswordResetForRequester(String portalId, UserResetRequest resetRequest, Long requestId) throws PortalUserServiceException;

    /**
     * @param portalId
     * @param credentials
     * @return
     */
    PortalUser authenticateUser(String portalId, String credentials) throws PortalUserServiceException;

    /**
     * @param portalId
     * @param resetRequest
     * @return
     * @throws PortalUserServiceException
     */
    UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserServiceProvider#regeneratePasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    UserResetResponse regeneratePasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException;

    UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest, String templateName, String emailTitle, Long requestId)
            throws PortalUserServiceException;

    /**
     * @param portalId
     * @param resetId
     * @return
     */
    UserResetResponse checkPasswordResetStatus(String portalId, String resetId);

    /**
     * @param portalId
     * @param resetId
     * @param password
     * @return
     * @throws PortalUserServiceException
     */
    UserResetResponse resetPassword(String portalId, String resetId, String password) throws PortalUserServiceException;

    UserResetResponse changePassword(String portalId, String userId, String acmUserId, PortalUserCredentials portalUserCredentials)
            throws PortalUserServiceException;

    PortalUser retrieveUser(String portalUserId, String portalId);

}
