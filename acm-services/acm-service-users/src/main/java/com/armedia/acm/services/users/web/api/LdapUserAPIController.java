package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.LdapUserCreateRequest;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.armedia.acm.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.security.core.Authentication;
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
@RequestMapping(value = {"/api/v1/users/ldap", "/api/latest/users/ldap"})
public class LdapUserAPIController
{
    private SpringContextHolder acmContextHolder;

    private LdapAuthenticateService ldapAuthenticateService;

    private LdapUserService ldapUserService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/editingEnabled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isEditingLdapUsersEnabled()
    {
        ldapAuthenticateService =
                acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class).get("armedia_ldapAuthenticateService");
        boolean enableEditingLdapUsers = ldapAuthenticateService.getLdapAuthenticateConfig().getEnableEditingLdapUsers();
        return Collections.singletonMap("enableEditingLdapUsers", enableEditingLdapUsers);
    }

    @RequestMapping(value = "/add/{groupName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmUser> addLdapUsersToGroup(
            @RequestBody List<AcmUser> members, @PathVariable String groupName) throws AcmUserActionFailedException
    {
        try
        {
            return ldapUserService.addExistingLdapUsersToGroup(members, groupName);
        } catch (Exception e)
        {
            log.error("Adding members to LDAP group:{} failed!", groupName, e);
            throw new AcmUserActionFailedException("add LDAP user to group", null, null, "Adding members to LDAP group failed!", e);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser addLdapUser(
            @RequestBody LdapUserCreateRequest ldapUserCreateRequest) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        try
        {
            return ldapUserService.createLdapUser(ldapUserCreateRequest.getAcmUser(),
                    ldapUserCreateRequest.getGroupNames(), ldapUserCreateRequest.getPassword());
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

    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser editLdapUser(
            @RequestBody AcmUser acmUser) throws AcmUserActionFailedException
    {
        return ldapUserService.editLdapUser(acmUser);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> credentials,
            Authentication authentication,
            HttpServletResponse response)
    {
        try
        {
            ldapAuthenticateService =
                    acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class).get("armedia_ldapAuthenticateService");
            String userId = authentication.getName();
            ldapAuthenticateService.changeUserPassword(userId, credentials.get("password"));
            return Collections.singletonMap("message", "Password successfully changed");
        } catch (InvalidAttributeValueException e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password failed!", e);
            return Collections.singletonMap("message", e.getExplanation());
        } catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("Changing password failed!", e);
            return Collections.singletonMap("message", "Unknown error occurred");
        }
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
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
