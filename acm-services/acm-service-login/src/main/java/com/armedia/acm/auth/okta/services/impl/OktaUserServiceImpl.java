package com.armedia.acm.auth.okta.services.impl;

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

import com.armedia.acm.auth.okta.exceptions.OktaException;
import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.auth.okta.model.user.OktaUser;
import com.armedia.acm.auth.okta.model.user.OktaUserCredentials;
import com.armedia.acm.auth.okta.model.user.OktaUserProfile;
import com.armedia.acm.auth.okta.model.user.OktaUserStatus;
import com.armedia.acm.auth.okta.services.OktaUserService;
import com.google.common.base.Preconditions;

import org.json.simple.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by dwu on 11/9/2017.
 */
public class OktaUserServiceImpl implements OktaUserService
{
    private Logger LOGGER = LogManager.getLogger(OktaUserServiceImpl.class);
    private OktaRestService oktaRestService;

    @Override
    public OktaUser createUser(OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(user, "User must not be null to create user");

        JSONObject userRequestBody = new JSONObject();

        JSONObject userProfileRequestBody = new JSONObject();
        OktaUserProfile profile = user.getProfile();
        boolean profileComplete = isProfileComplete(profile, userProfileRequestBody);
        if (!profileComplete)
        {
            throw new OktaException("User profile is invalid, can't create user.");
        }

        userRequestBody.put("profile", userProfileRequestBody);

        JSONObject credentialsRequestBody = new JSONObject();
        OktaUserCredentials credentials = user.getCredentials();
        boolean credentialsComplete = isCredentialsComplete(credentials, credentialsRequestBody);

        if (!credentialsComplete)
        {
            throw new OktaException("User requires credentials to create active user");
        }

        userRequestBody.put("credentials", credentialsRequestBody);

        String apiPath = OktaAPIConstants.CREATE_USER;
        return simpleUserOperation(apiPath, HttpMethod.POST, userRequestBody.toString());
    }

    @Override
    public OktaUser createUser(OktaUserProfile userProfile) throws OktaException
    {
        JSONObject profileData = new JSONObject();

        // need to check whether it has all the data it needs
        boolean profileComplete = isProfileComplete(userProfile, profileData);

        if (userProfile == null || !profileComplete)
        {
            throw new OktaException("User profile is null or profile data are incomplete");
        }
        else
        {
            JSONObject body = new JSONObject();
            body.put("profile", profileData);

            String apiPath = OktaAPIConstants.CREATE_USER;
            return simpleUserOperation(apiPath, HttpMethod.POST, body.toString());
        }
    }

    private boolean isCredentialsComplete(OktaUserCredentials credentials, JSONObject credentialsRequestBody)
    {
        if (credentials == null)
        {
            LOGGER.debug("Invalid credentials");
            return false;
        }

        if (credentials.getPassword() == null)
        {
            LOGGER.debug("Password is required for valid credentials");
            return false;
        }

        JSONObject password = new JSONObject();
        password.put("value", credentials.getPassword());
        credentialsRequestBody.put("password", password);

        if (credentials.getProvider() == null || credentials.getProviderName() == null)
        {
            LOGGER.debug("Credentials require a provider.");
            return false;
        }

        JSONObject provider = new JSONObject();
        provider.put("type", credentials.getProvider().getProviderType());
        provider.put("name", credentials.getProviderName());
        credentialsRequestBody.put("provider", provider);

        return true;
    }

    /**
     * There might be a better way to do these checks. But if any of the user profile data is invalid,
     * This is no go.
     *
     * @param userProfile
     * @param profileData
     * @return
     */
    private boolean isProfileComplete(OktaUserProfile userProfile, JSONObject profileData)
    {
        String fName = userProfile.getFirstName();
        if (fName == null)
        {
            return false;
        }
        else
        {
            profileData.put(OktaAPIConstants.FIRST_NAME, fName);
        }

        String lName = userProfile.getLastName();
        if (lName == null)
        {
            return false;
        }
        else
        {
            profileData.put(OktaAPIConstants.LAST_NAME, lName);
        }

        String email = userProfile.getEmail();
        if (email == null)
        {
            return false;
        }
        else
        {

            profileData.put(OktaAPIConstants.EMAIL, userProfile.getEmail());
        }

        String login = userProfile.getLogin();
        if (login != null)
        {
            profileData.put(OktaAPIConstants.LOGIN_NAME, login);
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    public OktaUser updateUser(Map<String, String> profile, String userId)
    {
        Preconditions.checkNotNull(profile, "User profile for update is null");
        Preconditions.checkNotNull(userId, "To update a user, the userId can't be null");

        JSONObject profileData = new JSONObject();
        profileData.putAll(profile);

        JSONObject body = new JSONObject();
        body.put("profile", profileData);

        String apiPath = String.format(OktaAPIConstants.USER_OPERATION, userId);
        return simpleUserOperation(apiPath, HttpMethod.POST, body.toString());
    }

    @Override
    public OktaUser activateUser(String userId)
    {
        Preconditions.checkNotNull(userId, "To activate a user, the userId can't be null");

        String apiPath = String.format(OktaAPIConstants.ACTIVATE_USER, userId);
        return simpleUserOperation(apiPath, HttpMethod.POST, "{}");
    }

    @Override
    public OktaUser getUser(String userId)
    {
        Preconditions.checkNotNull(userId, "To get a user, the userId can't be null");

        String apiPath = String.format(OktaAPIConstants.USER_OPERATION, userId);
        return simpleUserOperation(apiPath, HttpMethod.GET, "{}");
    }

    @Override
    public boolean deleteUser(String userId) throws OktaException
    {
        Preconditions.checkNotNull(userId, "UserId can't empty to delete user");

        OktaUser user = getUser(userId);
        if (user == null)
        {
            throw new OktaException("Can't delete user that doesn't exist.");
        }

        return deleteUser(user);
    }

    @Override
    public boolean deleteUser(OktaUser user) throws OktaException
    {
        Preconditions.checkNotNull(user, "User is required to delete user");
        Preconditions.checkNotNull(user.getId(), "userId is null");

        String apiPath = String.format(OktaAPIConstants.USER_OPERATION, user.getId());

        if (!OktaUserStatus.DEPROVISIONED.equals(user.getStatus()))
        {
            LOGGER.warn("User is not deactivated, will deactivate user first before deleting");
            ResponseEntity<Object> deprovisionResult = getOktaRestService().doRestCall(apiPath, HttpMethod.DELETE, Object.class, null);
            if (!deprovisionResult.getStatusCode().is2xxSuccessful())
            {
                throw new OktaException("Couldn't deactivate user");
            }
        }

        ResponseEntity<Object> deleteResult = getOktaRestService().doRestCall(apiPath, HttpMethod.DELETE, Object.class, null);
        return deleteResult.getStatusCode().is2xxSuccessful();
    }

    /**
     * This method consolidate the above user operations.
     *
     * @param apiPath
     * @param httpMethod
     * @param body
     * @return
     */
    private OktaUser simpleUserOperation(String apiPath, HttpMethod httpMethod, String body)
    {
        ResponseEntity<OktaUser> exchange = oktaRestService.doRestCall(apiPath, httpMethod, OktaUser.class, body);
        if (HttpStatus.OK.equals(exchange.getStatusCode()))
        {
            return exchange.getBody();
        }
        return null;
    }

    public OktaRestService getOktaRestService()
    {
        return oktaRestService;
    }

    public void setOktaRestService(OktaRestService oktaRestService)
    {
        this.oktaRestService = oktaRestService;
    }
}
