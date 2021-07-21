package com.armedia.acm.auth.web;

/*-
 * #%L
 * ACM Shared Web Artifacts
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
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.users.model.OAuth2ClientRegistrationConfig;
import com.armedia.acm.web.api.service.LoginWarningMessageService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Map;

@Controller
public class LoginController
{
    private final LoginWarningMessageService loginWarningMessageService;
    private final AcmSpringActiveProfile acmSpringActiveProfile;
    private final OAuth2ClientRegistrationConfig oAuth2ClientRegistrationConfig;
    private final ApplicationConfig applicationConfig;

    private final Logger log = LogManager.getLogger(getClass());

    public LoginController(LoginWarningMessageService loginWarningMessageService,
                           AcmSpringActiveProfile acmSpringActiveProfile,
                           OAuth2ClientRegistrationConfig oAuth2ClientRegistrationConfig, ApplicationConfig applicationConfig)
    {
        this.loginWarningMessageService = loginWarningMessageService;
        this.acmSpringActiveProfile = acmSpringActiveProfile;
        this.oAuth2ClientRegistrationConfig = oAuth2ClientRegistrationConfig;
        this.applicationConfig = applicationConfig;
    }

    @GetMapping(value = { "/login", "/login.html" })
    public String getLogin(Model model, HttpSession httpSession)
    {
        Object loggedUser = httpSession.getAttribute("acm_username");

        if (loggedUser != null)
        {
            log.info("User [{}] is already logged in.", loggedUser);
            return "redirect:/#!/welcome";
        }
        else
        {
            boolean showForgotUsernameAndPasswordLink = applicationConfig.getAllowForgotUsernameAndPasswordOnLogin()
                    && !acmSpringActiveProfile.isSSOEnabledEnvironment();
            model.addAttribute("showForgotUsernameAndPasswordLink", showForgotUsernameAndPasswordLink);

            loginWarningMessageService.buildModel(model);
            return "login";
        }
    }

    /**
     * Retrieve login warning configuration
     *
     * @return login warning configuration
     */
    @GetMapping(value = "/warning")
    @ResponseBody
    public Map<String, Object> getWarning()
    {
        return loginWarningMessageService.getWarning();
    }

    @GetMapping(value = "/oauth-login")
    public String getOAuth2Login(Model model)
    {
        model.addAttribute("oidcRegistration", oAuth2ClientRegistrationConfig.getRegistrationId());
        return "oauth-login";
    }
}
