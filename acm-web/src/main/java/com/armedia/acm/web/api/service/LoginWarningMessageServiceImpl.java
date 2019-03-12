package com.armedia.acm.web.api.service;

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

import com.armedia.acm.web.model.LoginConfig;

import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
public class LoginWarningMessageServiceImpl implements LoginWarningMessageService
{
    private LoginConfig loginConfig;
    private ApplicationMetaInfoService applicationMetaInfoService;

    @Override
    public boolean isEnabled()
    {
        return loginConfig.isWarningEnabled();
    }

    @Override
    public String getMessage()
    {
        return loginConfig.getWarningMessage() == null ? "" : loginConfig.getWarningMessage();
    }

    @Override
    public Map<String, Object> getWarning()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", isEnabled());
        map.put("message", getMessage());
        return map;
    }

    @Override
    public void buildModel(Model model)
    {
        model.addAttribute("warningEnabled", isEnabled());
        model.addAttribute("warningMessage", getMessage());
        model.addAttribute("version", applicationMetaInfoService.getVersion());
    }

    public LoginConfig getLoginConfig()
    {
        return loginConfig;
    }

    public void setLoginConfig(LoginConfig loginConfig)
    {
        this.loginConfig = loginConfig;
    }

    public ApplicationMetaInfoService getApplicationMetaInfoService()
    {
        return applicationMetaInfoService;
    }

    public void setApplicationMetaInfoService(ApplicationMetaInfoService applicationMetaInfoService)
    {
        this.applicationMetaInfoService = applicationMetaInfoService;
    }
}
