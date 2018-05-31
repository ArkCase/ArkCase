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

import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragan.simonovski on 05/11/2016.
 */
public class LoginWarningMessageServiceImpl implements LoginWarningMessageService
{
    private Boolean warningEnabled;
    private String warningMessage;
    private ApplicationMetaInfoService applicationMetaInfoService;

    @Override
    public boolean isEnabled()
    {
        return warningEnabled == null ? false : warningEnabled.booleanValue();
    }

    @Override
    public String getMessage()
    {
        return warningMessage == null ? "" : warningMessage;
    }

    @Override
    public Map<String, Object> getWarning()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("enabled", warningEnabled);
        map.put("message", warningMessage);
        return map;
    }

    @Override
    public void buildModel(Model model)
    {
        model.addAttribute("warningEnabled", isEnabled());
        model.addAttribute("warningMessage", getMessage());
        model.addAttribute("version", applicationMetaInfoService.getVersion());
    }

    public void setWarningEnabled(String warningEnabled)
    {
        this.warningEnabled = Boolean.parseBoolean(warningEnabled == null ? "false" : warningEnabled);
    }

    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
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
