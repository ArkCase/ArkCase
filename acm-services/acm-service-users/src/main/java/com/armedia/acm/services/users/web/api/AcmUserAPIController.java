package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.UserDTO;
import com.armedia.acm.services.users.service.AcmUserEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = { "/api/v1/ldap/", "/api/latest/ldap/" })
public class AcmUserAPIController extends SecureLdapController
{
    private LdapUserService ldapUserService;
    private AcmUserEventPublisher acmUserEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{directory:.+}/editingEnabled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isEditingLdapUsersEnabled(@PathVariable String directory)
    {
        boolean enableEditingLdapUsers = isLdapManagementEnabled(directory);
        return Collections.singletonMap("enableEditingLdapUsers", enableEditingLdapUsers);
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}/users", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmUser> addUsersToGroup(@RequestBody List<AcmUser> members, @PathVariable String directory, @PathVariable String groupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.addExistingLdapUsersToGroup(members, directory, groupName);
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
            return acmUser;
        }
        catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate username [{}]", ldapUserCreateRequest.getUserId(), e);
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg("Username is already taken!", "USER", "username", e);
            error.putExtra("user", ldapUserCreateRequest);
            throw error;
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
        catch (Exception e)
        {
            log.error("Editing LDAP user [{}] failed!", userId, e);
            throw new AcmUserActionFailedException("edit LDAP user", null, null, "Editing LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/manage/{userId:.+}/groups", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "{directory:.+}/manage/{userId:.+}/groups", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser removeUserFromGroups(@RequestParam("groupNames") List<String> groupNames, @PathVariable("userId") String userId,
                                        @PathVariable("directory") String directory) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.removeUserFromGroups(userId, groupNames, directory);
        }
        catch (Exception e)
        {
            log.error("Removing user [{}] from groups [{}] failed!", userId, groupNames, e);
            throw new AcmUserActionFailedException("Removing user from groups", null, null,
                    "Removing the user from groups: [{}] failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/users/{userId:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("userId") String userId, @PathVariable("directory") String directory)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        AcmUser source = getLdapUserService().getUserDao().findByUserId(userId);
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            ldapUserService.deleteAcmUser(userId, directory);
            getAcmUserEventPublisher().publishLdapUserDeletedEvent(source);

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
    public AcmUser cloneUser(@RequestBody @Valid UserDTO ldapUserCloneRequest, @PathVariable String userId, @PathVariable String directory)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.cloneLdapUser(userId, ldapUserCloneRequest, directory);
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

    @RequestMapping(value = "/{directory:.+}/users/{userId:.+}/password", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changePassword(@RequestBody Map<String, String> credentials, @PathVariable String directory,
                                              @PathVariable String userId, HttpServletResponse response) throws AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            LdapAuthenticateService ldapAuthenticateService = getAcmContextHolder().getAllBeansOfType(LdapAuthenticateService.class)
                    .get(String.format("%s_ldapAuthenticateService", directory));
            ldapAuthenticateService.changeUserPassword(userId, credentials.get("currentPassword"), credentials.get("newPassword"));
            log.debug("User [{}] successfully updated password", userId);
            return Collections.singletonMap("message", "Password successfully changed");
        }
        catch (InvalidAttributeValueException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("message", e.getExplanation());
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
            return Collections.singletonMap("message", e.getMessage());
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password for user [{}] failed!", userId, e);
            return Collections.singletonMap("message", "Unknown error occurred");
        }
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
}
