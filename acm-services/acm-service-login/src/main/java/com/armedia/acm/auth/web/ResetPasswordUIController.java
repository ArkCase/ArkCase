package com.armedia.acm.auth.web;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.armedia.acm.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@RequestMapping("/reset-password")
public class ResetPasswordUIController
{
    private LdapUserService ldapUserService;
    private SpringContextHolder acmContextHolder;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getResetPassword(@RequestParam String token)
    {
        AcmUser acmUser = ldapUserService.findByToken(token);
        ModelAndView model = new ModelAndView();
        boolean isTokenValid = false;
        if (acmUser != null)
        {
            log.debug("Check token: [{}] validity for user: [{}]", token, acmUser.getUserId());
            isTokenValid = ldapUserService.isTokenValid(acmUser.getPasswordResetToken());
            model.addObject("token", token);
            model.addObject("directory", acmUser.getUserDirectoryName());
            model.setViewName("reset-password");
        }
        model.addObject("isTokenValid", isTokenValid);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Object setPassword(@RequestParam String token, @RequestParam String password, @RequestParam String confirmPassword,
            @RequestParam String directory)
    {
        ModelAndView model = new ModelAndView();
        model.setViewName("reset-password");
        model.addObject("isTokenValid", true);
        model.addObject("token", token);
        model.addObject("directory", directory);
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword))
        {
            model.addObject("error", true);
            model.addObject("errorMsg", "Please enter valid password!");
            return model;
        }
        if (!Objects.equals(password, confirmPassword))
        {
            model.addObject("error", true);
            model.addObject("errorMsg", "Passwords do not match!");
            return model;
        }
        try
        {
            LdapAuthenticateService ldapAuthenticateService = acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class).
                    get(String.format("%s_ldapAuthenticateService", directory));
            ldapAuthenticateService.resetUserPassword(token, password);
            model.addObject("success", true);
            return model;
        } catch (Exception e)
        {
            log.error("Changing password failed!", e);
            model.addObject("error", true);
            model.addObject("errorMsg", "Changing password failed!");
            return model;
        }
    }

    public void setLdapUserService(LdapUserService ldapUserService)
    {
        this.ldapUserService = ldapUserService;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }
}
