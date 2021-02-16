package com.armedia.acm.web.model;

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

import com.armedia.acm.configuration.annotations.ListValue;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class LoginConfig
{
    @Value("${login.warning.message}")
    private String warningMessage;

    @Value("${login.warning.enabled}")
    private boolean warningEnabled;

    @Value("${login.defaultTargetUrl}")
    private String defaultTargetUrl;

    private List<String> ignoredSavedUrls;

    @ListValue(value = "login.ignoredSavedUrls")
    public List<String> getIgnoredSavedUrls()
    {
        return ignoredSavedUrls;
    }

    public void setIgnoredSavedUrls(List<String> ignoredSavedUrls)
    {
        this.ignoredSavedUrls = ignoredSavedUrls;
    }

    public String getWarningMessage()
    {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
    }

    public boolean isWarningEnabled()
    {
        return warningEnabled;
    }

    public void setWarningEnabled(boolean warningEnabled)
    {
        this.warningEnabled = warningEnabled;
    }

    public String getDefaultTargetUrl()
    {
        return defaultTargetUrl;
    }

    public void setDefaultTargetUrl(String defaultTargetUrl)
    {
        this.defaultTargetUrl = defaultTargetUrl;
    }
}
