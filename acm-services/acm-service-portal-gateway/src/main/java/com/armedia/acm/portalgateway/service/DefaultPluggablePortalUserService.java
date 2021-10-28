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
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 30, 2018
 *
 */
public class DefaultPluggablePortalUserService implements PortalUserService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder springContextHolder;

    private UserDao userDao;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#requestRegistration(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserRegistrationRequest)
     */
    @Override
    public UserRegistrationResponse requestRegistration(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException
    {
        log.debug("Requesting registration for user with [{}] email address for portal with [{}] ID.",
                registrationRequest.getEmailAddress(), portalId);
        return getServiceProvider().requestRegistration(portalId, registrationRequest);
    }

    @Override
    public UserRegistrationResponse regenerateRegistrationRequest(String portalId, UserRegistrationRequest registrationRequest)
            throws PortalUserServiceException
    {
        log.debug("Regenerating registration for user with [{}] email address for portal with [{}] ID.",
                registrationRequest.getEmailAddress(), portalId);
        return getServiceProvider().regenerateRegistrationRequest(portalId, registrationRequest);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#checkRegistrationStatus(java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserRegistrationResponse checkRegistrationStatus(String portalId, String registrationId) throws PortalUserServiceException
    {
        log.debug("Checking registration [{}] at portal with [{}] ID.", registrationId, portalId);
        return getServiceProvider().checkRegistrationStatus(portalId, registrationId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#registerUser(java.lang.String, java.lang.String,
     * com.armedia.acm.portalgateway.model.PortalUser, java.lang.String)
     */
    @Override
    public UserRegistrationResponse registerUser(String portalId, String registrationId, PortalUser user, String password)
            throws PortalUserServiceException
    {
        log.debug("Registering user with email [{}] at portal with ID [{}].", user.getEmail(), portalId);
        return getServiceProvider().registerUser(portalId, registrationId, user, password);
    }

    @Override
    public UserRegistrationResponse registerUserFromPerson(String portalId, Long personId, Long requestId)
            throws PortalUserServiceException
    {
        log.debug("Registering user for person with ID [{}] at portal with ID [{}] from Arkcase.",
                personId, portalId);
        return getServiceProvider().registerUserFromPerson(portalId, personId, requestId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#updateUser(java.lang.String,
     * com.armedia.acm.portalgateway.model.PortalUser)
     */
    @Override
    public PortalUser updateUser(String portalId, PortalUser user) throws PortalUserServiceException
    {
        log.debug("Updating user with email [{}] at portal with ID [{}].", user.getEmail(), portalId);
        return getServiceProvider().updateUser(portalId, user);
    }

    @Override
    public PortalUser retrieveUser(String portalUserId, String portalId) throws PortalUserServiceException
    {
        log.debug("Retrieving user [{}] at portal with ID [{}].", portalUserId, portalId);
        return getServiceProvider().retrieveUser(portalUserId, portalId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#authenticateUser(java.lang.String, java.lang.String)
     */
    @Override
    public PortalUser authenticateUser(String portalId, String credentials) throws PortalUserServiceException
    {
        log.debug("Attempting user authentication for user at portal with [{}] ID.", portalId);
        return getServiceProvider().authenticateUser(portalId, credentials);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#requestPasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    @Override
    public UserResetResponse requestPasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException
    {
        log.debug("Password change requested by [{}] user at portal with [{}] ID.", resetRequest.getEmailAddress(), portalId);
        return getServiceProvider().requestPasswordReset(portalId, resetRequest);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#regeneratePasswordReset(java.lang.String,
     * com.armedia.acm.portalgateway.model.UserResetRequest)
     */
    @Override
    public UserResetResponse regeneratePasswordReset(String portalId, UserResetRequest resetRequest) throws PortalUserServiceException
    {
        log.debug("Regenerating password reset requested by [{}] user at portal with [{}] ID.", resetRequest.getEmailAddress(), portalId);
        return getServiceProvider().regeneratePasswordReset(portalId, resetRequest);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#checkPasswordResetStatus(java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserResetResponse checkPasswordResetStatus(String portalId, String resetId) throws PortalUserServiceException
    {
        log.debug("Checking reset status [{}] at portal with [{}] ID.", resetId, portalId);
        return getServiceProvider().checkPasswordResetStatus(portalId, resetId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalUserService#resetPassword(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserResetResponse resetPassword(String portalId, String resetId, String password) throws PortalUserServiceException
    {
        log.debug("Reseting passwrod for [{}] reset request for portal with [{}] ID.", resetId, portalId);
        return getServiceProvider().resetPassword(portalId, resetId, password);
    }

    @Override
    public UserResetResponse changePassword(String portalId, String userId, String acmUserId, PortalUserCredentials portalUserCredentials)
            throws PortalUserServiceException
    {
        log.debug("Changing password for [{}] [{}] for portal with [{}] ID.", userId, acmUserId, portalId);
        Optional<AcmUser> acmPortalUser = userDao.findByEmailAddressAndDirectoryName(userId, directoryName);
        if (!acmPortalUser.isPresent())
        {
            log.debug(String.format("User %s does not exist. Using configured system user %s.", userId, acmUserId));
            throw new PortalUserServiceException(String.format("Couldn't update password for user %s.", userId));
        }
        return getServiceProvider().changePassword(userId, acmPortalUser.get().getUserId(), acmUserId, portalUserCredentials);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.portalgateway.service.PortalUserService#getExceptionMapper(com.armedia.acm.portalgateway.service.
     * PortalUserServiceException)
     */
    @Override
    public PortalUserServiceExceptionMapper getExceptionMapper(PortalUserServiceException se)
    {
        return new PortalUserServiceExceptionMapper(se);
    }

    private PortalUserServiceProvider getServiceProvider() throws PortalUserServiceException
    {
        log.debug("Trying to find implementation of [{}].", PortalRequestServiceProvider.class.getName());

        Map<String, PortalUserServiceProvider> providers = springContextHolder.getAllBeansOfType(PortalUserServiceProvider.class);

        Optional<PortalUserServiceProvider> provider = providers.values().stream().findFirst();
        return provider.orElseThrow(() -> {
            log.warn("Could not find [{}] implementations.", PortalUserServiceProvider.class.getName());
            return new PortalUserServiceException(String.format("Could not find [%s] implementations.",
                    PortalUserServiceProvider.class.getName()), PROVIDER_NOT_PRESENT);
        });
    }

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
