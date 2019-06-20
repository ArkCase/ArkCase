package com.armedia.acm.web.api;

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
import com.armedia.acm.web.api.service.LoginWarningMessageService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Map;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
@Controller
public class LoginController
{
    private LoginWarningMessageService loginWarningMessageService;
    private AcmSpringActiveProfile acmSpringActiveProfile;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = { "/login", "/login.html" }, method = RequestMethod.GET)
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
            model.addAttribute("isSsoEnv", acmSpringActiveProfile.isSAMLEnabledEnvironment());
            loginWarningMessageService.buildModel(model);
            return "login";
        }
    }

    /**
     * Retrieve login warning configuration
     *
     * @return login warning configuration
     */
    @RequestMapping(value = "/warning", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Object> getWarning()
    {
        return loginWarningMessageService.getWarning();
    }

    public void setLoginWarningMessageService(LoginWarningMessageService loginWarningMessageService)
    {
        this.loginWarningMessageService = loginWarningMessageService;
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
