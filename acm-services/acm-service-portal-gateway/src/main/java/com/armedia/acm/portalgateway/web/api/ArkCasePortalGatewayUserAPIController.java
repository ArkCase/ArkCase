package com.armedia.acm.portalgateway.web.api;

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
import com.armedia.acm.portalgateway.service.CheckPortalUserAssignement;
import com.armedia.acm.portalgateway.service.PortalId;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;
import com.armedia.acm.portalgateway.service.PortalUserAssignementException;
import com.armedia.acm.portalgateway.service.PortalUserService;
import com.armedia.acm.portalgateway.service.PortalUserServiceException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/portalgateway/{portalId}/users", "/api/latest/service/portalgateway/{portalId}/users" })
public class ArkCasePortalGatewayUserAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalUserService portalUserService;

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserRegistrationResponse requestRegistration(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody UserRegistrationRequest registrationRequest) throws PortalUserServiceException
    {
        log.debug("Requesting registration for user with [{}] email address for portal with [{}] ID.",
                registrationRequest.getEmailAddress(), portalId);
        return portalUserService.requestRegistration(portalId, registrationRequest);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserRegistrationResponse checkRegistrationStatus(Authentication auth,
            @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestParam(value = "registrationId") String registrationId)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Checking registration [{}] at portal with [{}] ID.", registrationId, portalId);
        return portalUserService.checkRegistrationStatus(portalId, registrationId);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations/{registrationId}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserRegistrationResponse registerUser(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "registrationId") String registrationId, @RequestBody PortalUser user,
            @RequestHeader(value = "X-Arkcase-User-Password") String password)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Registering [{}] user for portal with [{}] ID.", PortalUser.composeUserName(user), portalId);
        return portalUserService.registerUser(portalId, registrationId, user, password);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/logins", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public ResponseEntity<?> authenticateUser(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody String credentials) throws PortalUserServiceException
    {
        PortalUser portalUser = new PortalUser();
        log.debug("Requesting user authentication for user at portal with [{}] ID.", portalId);
        try
        {
            portalUser = portalUserService.authenticateUser(portalId, credentials);
            return new ResponseEntity<PortalUser>(portalUser, HttpStatus.OK);
        }
        catch (PortalUserServiceException e)
        {
            return new ResponseEntity<PortalUser>(portalUser, HttpStatus.UNAUTHORIZED);
        }
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations/resets", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserResetResponse requestPasswordReset(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody UserResetRequest resetRequest) throws PortalUserServiceException
    {
        log.debug("Requesting registration for user with [{}] email address for portal with [{}] ID.",
                resetRequest.getEmailAddress(), portalId);
        return portalUserService.requestPasswordReset(portalId, resetRequest);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations/resets", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserResetResponse checkPasswordResetStatus(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestParam(value = "resetId") String resetId)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Checking reset status [{}] at portal with [{}] ID.", resetId, portalId);
        return portalUserService.checkPasswordResetStatus(portalId, resetId);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/registrations/resets/{resetId}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserResetResponse resetPassword(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "resetId") String resetId, @RequestHeader(value = "X-Arkcase-User-Password") String password)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Reseting passwrod for [{}] reset request for portal with [{}] ID.", resetId, portalId);
        return portalUserService.resetPassword(portalId, resetId, password);
    }


    @RequestMapping(value = "/registrations/{userId:.+}/changePassword", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public UserResetResponse changePassword(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
                                          @RequestBody PortalUserCredentials portalUserCredentials, @PathVariable String userId)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Changing password for [{}] user for portal with [{}] ID.", userId,  portalId);
        return portalUserService.changePassword(portalId, userId, portalUserCredentials);
    }


    @RequestMapping(method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalUser updateUser(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody PortalUser user)
            throws PortalUserAssignementException, PortalUserServiceException
    {
        log.debug("Updating [{}] user for portal with [{}] ID.", PortalUser.composeUserName(user), portalId);
        return portalUserService.updateUser(portalId, user);
    }

    @RequestMapping( value = "/{portalUserId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalUser retrieveUser(@PortalId @PathVariable(value = "portalId") String portalId, @PathVariable(value="portalUserId") String portalUserId) throws PortalUserServiceException
    {
        log.debug("Retrieve [{}] user for portal with [{}] ID.", portalUserId, portalId);
        return portalUserService.retrieveUser(portalUserId, portalId);
    }

    @ExceptionHandler(PortalUserServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserException(PortalUserServiceException se)
    {
        log.warn("Handling exception of [{}] type.", se.getClass().getName());
        PortalServiceExceptionMapper exceptionMapper = portalUserService.getExceptionMapper(se);
        Object errorDetails = exceptionMapper.mapException();
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param portalUserService
     *            the portalUserService to set
     */
    public void setPortalUserService(PortalUserService portalUserService)
    {
        this.portalUserService = portalUserService;
    }

}
