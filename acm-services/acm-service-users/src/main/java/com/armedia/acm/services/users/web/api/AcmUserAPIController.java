package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.AcmSpringActiveProfile;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.ldap.syncer.AcmLdapSyncEvent;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.AcmUserEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;

import org.apache.commons.validator.ValidatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = { "/api/v1/ldap/", "/api/latest/ldap/" })
public class AcmUserAPIController extends SecureLdapController
{
    private LdapUserService ldapUserService;
    private AcmUserEventPublisher acmUserEventPublisher;
    private AcmSpringActiveProfile acmSpringActiveProfile;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{directory:.+}/editingEnabled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isEditingLdapUsersEnabled(@PathVariable String directory)
    {
        boolean enableEditingLdapUsers = isLdapManagementEnabled(directory);
        return Collections.singletonMap("enableEditingLdapUsers", enableEditingLdapUsers);
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmUser> addUsersToGroup(@RequestBody List<String> memberIds, @PathVariable String directory,
            @PathVariable String groupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        groupName = new String(Base64.getUrlDecoder().decode(groupName.getBytes()));
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.addExistingLdapUsersToGroup(memberIds, directory, groupName);
        }
        catch (Exception e)
        {
            log.error("Adding members to LDAP group [{}] failed!", groupName, e);
            throw new AcmUserActionFailedException("add LDAP user to group", null, null, "Adding members to LDAP group failed!", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser createUser(@RequestBody UserDTO ldapUserCreateRequest, @PathVariable String directory,
            HttpSession httpSession, Authentication authentication)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            AcmUser acmUser = ldapUserService.createLdapUser(ldapUserCreateRequest, directory);
            ldapUserService.publishSetPasswordEmailEvent(acmUser);
            ldapUserService.publishUserCreatedEvent(httpSession, authentication, acmUser, true);
            acmUserEventPublisher.getApplicationEventPublisher().publishEvent(new AcmLdapSyncEvent(acmUser.getUserId()));
            return acmUser;
        }
        catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate username [{}]", ldapUserCreateRequest.getUserId(), e);
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg("Username is already taken!", "USER", "username", e);
            error.putExtra("user", ldapUserCreateRequest);
            throw error;
        }
        catch (ValidatorException e)
        {
            log.error("Invalid email [{}]", ldapUserCreateRequest.getMail(), e);
            AcmAppErrorJsonMsg errorEmail = new AcmAppErrorJsonMsg("Invalid email!", "USER", "email", e);
            errorEmail.putExtra("user", ldapUserCreateRequest);
            throw errorEmail;
        }
        catch (Exception e)
        {
            log.error("Creating LDAP user [{}] failed!", ldapUserCreateRequest.getUserId(), e);
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/users/{userId:.+}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser editUser(@RequestBody AcmUser acmUser, @PathVariable String userId, @PathVariable String directory,
            HttpSession httpSession, Authentication authentication)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            AcmUser editedUser = ldapUserService.editLdapUser(acmUser, userId, directory);
            ldapUserService.publishUserUpdatedEvent(httpSession, authentication, editedUser, true);
            return editedUser;
        }
        catch (ValidatorException e)
        {
            log.error("Invalid email [{}]", acmUser.getMail(), e);
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg("Invalid email!", "USER", "email", e);
            error.putExtra("user", acmUser);
            throw error;
        }
        catch (Exception e)
        {
            log.error("Editing LDAP user [{}] failed!", userId, e);
            throw new AcmUserActionFailedException("edit LDAP user", null, null, "Editing LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/manage/{userId:.+}/groups", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser addUserInGroups(@RequestBody List<String> groupNames, @PathVariable("userId") String userId,
            @PathVariable("directory") String directory) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.addUserInGroups(userId, groupNames, directory);
        }
        catch (Exception e)
        {
            log.error("Adding groups to the user [{}] failed!", userId, e);
            throw new AcmUserActionFailedException("Adding groups to the user", null, null, "Adding groups to the user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/manage/{userId:.+}/groups", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser removeUserFromGroups(@RequestParam("groupNames") List<String> groupNames, @PathVariable("userId") String userId,
            @PathVariable("directory") String directory) throws AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.removeUserFromGroups(userId, groupNames, directory);
        }
        catch (AcmLdapActionFailedException | AcmObjectNotFoundException e)
        {
            log.error("Removing user [{}] from groups [{}] failed!", userId, groupNames, e.getMessage());
            throw new AcmAppErrorJsonMsg(e.getMessage(), "GROUP", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/users/{userId:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId, @PathVariable("directory") String directory,
            HttpSession session)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        AcmUser source = getLdapUserService().getUserDao().findByUserId(userId);
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            ldapUserService.deleteAcmUser(userId, directory);
            getAcmUserEventPublisher().publishLdapUserDeletedEvent(source, (String) session.getAttribute("acm_ip_address"));

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Deleting LDAP user [{}] failed!", userId, e);
            throw new AcmUserActionFailedException("Delete LDAP user", null, null, "Deleting LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/users/{userId:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser cloneUser(@RequestBody UserDTO ldapUserCloneRequest, @PathVariable String userId, @PathVariable String directory,
            HttpSession session, Authentication authentication)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            AcmUser acmUser = ldapUserService.cloneLdapUser(userId, ldapUserCloneRequest, directory);
            ldapUserService.publishUserCreatedEvent(session, authentication, acmUser, true);
            ldapUserService.publishSetPasswordEmailEvent(acmUser);
            acmUserEventPublisher.getApplicationEventPublisher().publishEvent(new AcmLdapSyncEvent(acmUser.getUserId()));
            return acmUser;
        }
        catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate username [{}]", ldapUserCloneRequest.getUserId(), e);
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg("Username is already taken!", "USER", "username", e);
            error.putExtra("user", ldapUserCloneRequest);
            throw error;
        }
        catch (Exception e)
        {
            log.error("Cloning user [{}] failed!", userId, e);
            throw new AcmUserActionFailedException("Clone user", null, null, "Cloning user failed!", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/users/{userId:.+}/password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changePassword(@RequestBody UserDTO credentials, @PathVariable String directory,
            @PathVariable String userId, HttpServletResponse response) throws AcmAppErrorJsonMsg
    {
        validateLdapPassword(credentials);
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            LdapAuthenticateService ldapAuthenticateService = getAcmContextHolder().getAllBeansOfType(LdapAuthenticateService.class)
                    .get(String.format("%s_ldapAuthenticateService", directory));
            ldapAuthenticateService.changeUserPassword(userId, credentials.getCurrentPassword(), credentials.getPassword());
            log.debug("User [{}] successfully updated password", userId);
            return Collections.singletonMap("message", "Password successfully changed");
        }
        catch (InvalidAttributeValueException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("passError", e.getExplanation());
        }
        catch (AuthenticationException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("authError", "Failed to authenticate! Wrong password.");
        }
        catch (AcmUserActionFailedException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("message", e.getShortMessage());
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("message", "Unknown error occurred");
        }
    }

    @RequestMapping(value = "/{directory:.+}/managePasswordEnabled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isManagePasswordsEnabled(@PathVariable String directory)
    {
        boolean enableEditingLdapUsers = isLdapManagementEnabled(directory);
        boolean managePasswordEnabled = !acmSpringActiveProfile.isSSOEnabledEnvironment() && enableEditingLdapUsers;
        return Collections.singletonMap("managePasswordEnabled", managePasswordEnabled);
    }

    public LdapUserService getLdapUserService()
    {
        return ldapUserService;
    }

    public void setLdapUserService(LdapUserService ldapUserService)
    {
        this.ldapUserService = ldapUserService;
    }

    public AcmUserEventPublisher getAcmUserEventPublisher()
    {
        return acmUserEventPublisher;
    }

    public void setAcmUserEventPublisher(AcmUserEventPublisher acmUserEventPublisher)
    {
        this.acmUserEventPublisher = acmUserEventPublisher;
    }

    public AcmSpringActiveProfile getAcmSpringActiveProfile()
    {
        return acmSpringActiveProfile;
    }

    public void setAcmSpringActiveProfile(AcmSpringActiveProfile acmSpringActiveProfile)
    {
        this.acmSpringActiveProfile = acmSpringActiveProfile;
    }
}
