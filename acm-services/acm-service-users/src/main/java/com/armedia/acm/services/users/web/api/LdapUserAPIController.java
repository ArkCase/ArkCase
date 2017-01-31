package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
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
@RequestMapping(value = {"/api/v1/users", "/api/latest/users"})
public class LdapUserAPIController
{
    private SpringContextHolder acmContextHolder;

    private LdapAuthenticateService ldapAuthenticateService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> isChangePasswordEnabled()
    {
        ldapAuthenticateService =
                acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class).get("armedia_ldapAuthenticateService");
        boolean exposeChangePassword = ldapAuthenticateService.getLdapAuthenticateConfig().getChangePasswordExposed();
        return Collections.singletonMap("exposeChangePassword", exposeChangePassword);
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
}
