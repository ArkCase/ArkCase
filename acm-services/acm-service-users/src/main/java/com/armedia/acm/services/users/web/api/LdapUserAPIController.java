package com.armedia.acm.services.users.web.api;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
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

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmUser addLdapUser(
            @RequestBody LdapUserCreateRequest ldapUserCreateRequest,
            HttpServletResponse response) throws AcmUserActionFailedException
    {
        try
        {
            return ldapUserService.createLdapUser(ldapUserCreateRequest.getAcmUser(),
                    ldapUserCreateRequest.getGroupName(), ldapUserCreateRequest.getPassword());
        } catch (Exception e)
        {
            log.error("Creating LDAP user failed!", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new AcmUserActionFailedException("create LDAP user", null, null, "Creating LDAP user failed!", e);
        }
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
