package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateManager;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = {"/api/v1/ldap/", "/api/latest/ldap/"})
public class LdapUserAPIController extends SecureLdapController
{
    private LdapUserService ldapUserService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{directory:.+}/editingEnabled", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isEditingLdapUsersEnabled(@PathVariable String directory)
    {
        boolean enableEditingLdapUsers = isLdapManagementEnabled(directory);
        return Collections.singletonMap("enableEditingLdapUsers", enableEditingLdapUsers);
    }

    @RequestMapping(value = "/{directory:.+}/groups/{groupName:.+}/users", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmUser> addLdapUsersToGroup(
            @RequestBody List<AcmUser> members, @PathVariable String directory, @PathVariable String groupName)
            throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.addExistingLdapUsersToGroup(members, directory, groupName);
        } catch (Exception e)
        {
            log.error("Adding members to LDAP group:{} failed!", groupName, e);
            throw new AcmUserActionFailedException("add LDAP user to group", null, null, "Adding members to LDAP group failed!", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser addLdapUser(
            @RequestBody LdapUser ldapUserCreateRequest, @PathVariable String directory) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.createLdapUser(ldapUserCreateRequest.getAcmUser(),
                    ldapUserCreateRequest.getGroupNames(), ldapUserCreateRequest.getPassword(), directory);
        } catch (NameAlreadyBoundException e)
        {
            log.error("Duplicate username: {}", ldapUserCreateRequest.getAcmUser().getUserId(), e);
            AcmAppErrorJsonMsg error = new AcmAppErrorJsonMsg("Username is already taken!", "USER",
                    "username", e);
            error.putExtra("user", ldapUserCreateRequest.getAcmUser());
            throw error;
        } catch (Exception e)
        {
            log.error("Creating LDAP user failed!", e);
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "{directory:.+}/users/{userId:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser editLdapUser(@RequestBody AcmUser acmUser, @PathVariable String userId,
                                @PathVariable String directory) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            return ldapUserService.editLdapUser(acmUser, userId, directory);
        } catch (Exception e)
        {
            log.error("Editing LDAP user failed!", e);
            throw new AcmUserActionFailedException("edit LDAP user", null, null, "Editing LDAP user failed!", e);
        }
    }

    @RequestMapping(value = "/{directory:.+}/users/{userId:.+}/password",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> credentials, @PathVariable String directory, @PathVariable String userId,
            HttpServletResponse response) throws AcmAppErrorJsonMsg
    {
        checkIfLdapManagementIsAllowed(directory);
        try
        {
            LdapAuthenticateService ldapAuthenticateService = getAcmContextHolder().getAllBeansOfType(LdapAuthenticateService.class).
                    get(String.format("%s_ldapAuthenticateService", directory));
            ldapAuthenticateService.changeUserPassword(userId, credentials.get("password"));
            return Collections.singletonMap("message", "Password successfully changed");
        } catch (InvalidAttributeValueException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password failed!", e);
            return Collections.singletonMap("message", e.getExplanation());
        } catch (AcmUserActionFailedException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Collections.singletonMap("message", e.getMessage());
        } catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password failed!", e);
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

}
