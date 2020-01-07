package com.armedia.acm.auth.web;

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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.armedia.acm.services.users.service.ldap.PasswordValidationService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping("/reset-password")
public class ResetPasswordUIController
{
    private LdapUserService ldapUserService;
    private SpringContextHolder acmContextHolder;
    private PasswordValidationService passwordValidationService;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getResetPassword(@RequestParam String token)
    {
        AcmUser acmUser = ldapUserService.findByPasswordResetToken(token);
        ModelAndView model = new ModelAndView();
        boolean isTokenValid = false;
        if (acmUser != null)
        {
            log.debug("Check token: [{}] validity for user: [{}]", token, acmUser.getUserId());
            isTokenValid = isTokenValid(acmUser.getPasswordResetToken());
            model.addObject("token", token);
            model.addObject("directory", acmUser.getUserDirectoryName());
            model.setViewName("reset-password");
        }
        model.addObject("isTokenValid", isTokenValid);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView setPassword(@RequestParam String token, @RequestParam String password,
            @RequestParam String confirmPassword, @RequestParam String directory)
    {
        ModelAndView modelAndView = getModelAndView(token, directory);
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword))
        {
            return setError(modelAndView, "Please enter valid password!");
        }
        if (!Objects.equals(password, confirmPassword))
        {
            return setError(modelAndView, "Passwords do not match!");
        }

        AcmUser acmUser = ldapUserService.findByPasswordResetToken(token);
        List<String> errorMessages = passwordValidationService.validate(acmUser.getUserId(), password);
        if (!errorMessages.isEmpty())
        {
            String errorMessageHtml = getErrorMessageHtml(errorMessages);
            return setError(modelAndView, errorMessageHtml);
        }
        try
        {
            LdapAuthenticateService ldapAuthenticateService = acmContextHolder.getAllBeansOfType(LdapAuthenticateService.class)
                    .get(String.format("%s_ldapAuthenticateService", directory));
            ldapAuthenticateService.resetUserPassword(token, password);
        }
        catch (Exception e)
        {
            log.error("Changing password failed!", e);
            return setError(modelAndView, "Changing password failed!");
        }
        return setSuccess(modelAndView);
    }

    private ModelAndView setError(ModelAndView model, String errorMessage)
    {
        model.addObject("error", true);
        model.addObject("errorMsg", errorMessage);
        return model;
    }

    private ModelAndView setSuccess(ModelAndView model)
    {
        model.addObject("success", true);
        return model;
    }

    private ModelAndView getModelAndView(String token, String directory)
    {
        ModelAndView model = new ModelAndView();
        model.setViewName("reset-password");
        model.addObject("isTokenValid", true);
        model.addObject("token", token);
        model.addObject("directory", directory);
        return model;
    }

    private String getErrorMessageHtml(List<String> errorMessages)
    {
        return errorMessages
                .stream()
                .map(it -> String.format("<p>%s</p>", it))
                .collect(Collectors.joining());
    }

    private boolean isTokenValid(PasswordResetToken token)
    {
        return token.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void setLdapUserService(LdapUserService ldapUserService)
    {
        this.ldapUserService = ldapUserService;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public void setPasswordValidationService(PasswordValidationService passwordValidationService)
    {
        this.passwordValidationService = passwordValidationService;
    }
}
