package com.armedia.acm.plagins.analytics.model;

/*-
 * #%L
 * ACM Default Plugin: analytics
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class AnalyticsServerConfig
{
    @JsonProperty("elk.server.url")
    @Value("${elk.server.url}")
    private String elkUrl;

    @JsonProperty("elk.server.port")
    @Value("${elk.server.port}")
    private Integer elkPort;

    @JsonProperty("elk.server.internal.url")
    @Value("${elk.server.internal.url}")
    private String elkInternalUrl;

    @JsonProperty("elk.server.user")
    @Value("${elk.server.user}")
    private String elkUser;

    @JsonProperty("elk.server.password")
    @Value("${elk.server.password}")
    private String elkPassword;

    @JsonProperty("elk.dashboard.url")
    @Value("${elk.dashboard.url}")
    private String elkDashboardUrl;

    @JsonProperty("slk.server.internal.url")
    @Value("${slk.server.internal.url}")
    private String slkInternalUrl;

    @JsonProperty("slk.server.internal.port")
    @Value("${slk.server.internal.port}")
    private int slkInternalPort;

    @JsonProperty("slk.server.external.url")
    @Value("${slk.server.external.url}")
    private String slkExternalUrl;

    @JsonProperty("slk.server.dashboard.url")
    @Value("${slk.server.dashboard.url}")
    private String slkDashboardUrl;

    @JsonProperty("slk.server.dashboardfile.url")
    @Value("${slk.server.dashboardfile.url}")
    private String slkDashboardFileUrl;

    public String getElkUrl()
    {
        return elkUrl;
    }

    public void setElkUrl(String elkUrl)
    {
        this.elkUrl = elkUrl;
    }

    public int getElkPort()
    {
        return elkPort;
    }

    public void setElkPort(int elkPort)
    {
        this.elkPort = elkPort;
    }

    public String getElkInternalUrl()
    {
        return elkInternalUrl;
    }

    public void setElkInternalUrl(String elkInternalUrl)
    {
        this.elkInternalUrl = elkInternalUrl;
    }

    public String getElkUser()
    {
        return elkUser;
    }

    public void setElkUser(String elkUser)
    {
        this.elkUser = elkUser;
    }

    @JsonProperty
    public String getElkPassword()
    {
        return elkPassword;
    }

    public void setElkPassword(String elkPassword)
    {
        this.elkPassword = elkPassword;
    }

    public String getElkDashboardUrl()
    {
        return elkDashboardUrl;
    }

    public void setElkDashboardUrl(String elkDashboardUrl)
    {
        this.elkDashboardUrl = elkDashboardUrl;
    }

    public String getSlkInternalUrl()
    {
        return slkInternalUrl;
    }

    public void setSlkInternalUrl(String slkInternalUrl)
    {
        this.slkInternalUrl = slkInternalUrl;
    }

    public Integer getSlkInternalPort()
    {
        return slkInternalPort;
    }

    public void setSlkInternalPort(Integer slkInternalPort)
    {
        this.slkInternalPort = slkInternalPort;
    }

    public String getSlkExternalUrl()
    {
        return slkExternalUrl;
    }

    public void setSlkExternalUrl(String slkExternalUrl)
    {
        this.slkExternalUrl = slkExternalUrl;
    }

    public String getSlkDashboardUrl()
    {
        return slkDashboardUrl;
    }

    public void setSlkDashboardUrl(String slkDashboardUrl)
    {
        this.slkDashboardUrl = slkDashboardUrl;
    }

    public String getSlkDashboardFileUrl()
    {
        return slkDashboardFileUrl;
    }

    public void setSlkDashboardFileUrl(String slkDashboardFileUrl)
    {
        this.slkDashboardFileUrl = slkDashboardFileUrl;
    }
}
