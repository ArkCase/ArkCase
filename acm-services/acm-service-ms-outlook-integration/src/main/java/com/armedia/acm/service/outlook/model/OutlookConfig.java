package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public class OutlookConfig
{
    @JsonProperty("outlook.exchange.integration.default_access")
    @Value("${outlook.exchange.integration.default_access}")
    private String defaultAccess;

    @JsonProperty("outlook.exchange.integration.approver_access")
    @Value("${outlook.exchange.integration.approver_access}")
    private String approverAccess;

    @JsonProperty("outlook.exchange.integration.assignee_access")
    @Value("${outlook.exchange.integration.assignee_access}")
    private String assigneeAccess;

    @JsonProperty("outlook.exchange.integration.follower_access")
    @Value("${outlook.exchange.integration.follower_access}")
    private String followerAccess;

    @JsonProperty("outlook.exchange.integration.enable.autodiscovery")
    @Value("${outlook.exchange.integration.enable.autodiscovery}")
    private Boolean enableAutoDiscovery;

    @JsonProperty("outlook.exchange.integration.enabled")
    @Value("${outlook.exchange.integration.enabled}")
    private Boolean integrationEnabled;

    @JsonProperty("outlook.exchange.integration.system_user_id")
    @Value("${outlook.exchange.integration.system_user_id}")
    private String systemUserId;

    @JsonProperty("outlook.exchange.integration.system_user_email")
    @Value("${outlook.exchange.integration.system_user_email}")
    private String systemUserEmail;

    @JsonProperty("outlook.exchange.integration.system_user_email_password")
    @Value("${outlook.exchange.integration.system_user_email_password}")
    private String systemUserPassword;

    @JsonProperty("outlook.exchange.integration.client-access-server")
    @Value("${outlook.exchange.integration.client-access-server}")
    private String clientAccessServer;

    @JsonProperty("outlook.exchange.integration.server.version")
    @Value("${outlook.exchange.integration.server.version}")
    private String serverVersion;

    @JsonProperty("outlook.exchange.integration.calendar.use.system.user")
    @Value("${outlook.exchange.integration.calendar.use.system.user}")
    private Boolean calendarUseSystemUser;

    @JsonProperty("outlook.exchange.integration.participants-types-as-outlook-permission")
    @Value("${outlook.exchange.integration.participants-types-as-outlook-permission}")
    private String participantsTypesAsOutlookPermission;

    @JsonProperty("outlook.exchange.integration.send.notification.from.system.user")
    @Value("${outlook.exchange.integration.send.notification.from.system.user:false}")
    private Boolean sendNotificationFromSystemUser;

    public String getDefaultAccess()
    {
        return defaultAccess;
    }

    public void setDefaultAccess(String defaultAccess)
    {
        this.defaultAccess = defaultAccess;
    }

    public String getApproverAccess()
    {
        return approverAccess;
    }

    public void setApproverAccess(String approverAccess)
    {
        this.approverAccess = approverAccess;
    }

    public String getAssigneeAccess()
    {
        return assigneeAccess;
    }

    public void setAssigneeAccess(String assigneeAccess)
    {
        this.assigneeAccess = assigneeAccess;
    }

    public String getFollowerAccess()
    {
        return followerAccess;
    }

    public void setFollowerAccess(String followerAccess)
    {
        this.followerAccess = followerAccess;
    }

    public Boolean getEnableAutoDiscovery()
    {
        return enableAutoDiscovery;
    }

    public void setEnableAutoDiscovery(Boolean enableAutoDiscovery)
    {
        this.enableAutoDiscovery = enableAutoDiscovery;
    }

    public Boolean getIntegrationEnabled()
    {
        return integrationEnabled;
    }

    public void setIntegrationEnabled(Boolean integrationEnabled)
    {
        this.integrationEnabled = integrationEnabled;
    }

    public String getSystemUserId()
    {
        return systemUserId;
    }

    public void setSystemUserId(String systemUserId)
    {
        this.systemUserId = systemUserId;
    }

    public String getSystemUserEmail()
    {
        return systemUserEmail;
    }

    public void setSystemUserEmail(String systemUserEmail)
    {
        this.systemUserEmail = systemUserEmail;
    }

    public String getSystemUserPassword()
    {
        return systemUserPassword;
    }

    public void setSystemUserPassword(String systemUserPassword)
    {
        this.systemUserPassword = systemUserPassword;
    }

    public String getClientAccessServer()
    {
        return clientAccessServer;
    }

    public void setClientAccessServer(String clientAccessServer)
    {
        this.clientAccessServer = clientAccessServer;
    }

    public String getServerVersion()
    {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion)
    {
        this.serverVersion = serverVersion;
    }

    public Boolean getCalendarUseSystemUser()
    {
        return calendarUseSystemUser;
    }

    public void setCalendarUseSystemUser(Boolean calendarUseSystemUser)
    {
        this.calendarUseSystemUser = calendarUseSystemUser;
    }

    public String getParticipantsTypesAsOutlookPermission()
    {
        return participantsTypesAsOutlookPermission;
    }

    public void setParticipantsTypesAsOutlookPermission(String participantsTypesAsOutlookPermission)
    {
        this.participantsTypesAsOutlookPermission = participantsTypesAsOutlookPermission;
    }

    @JsonIgnore
    public List<String> getParticipantTypes()
    {
        return Arrays.asList(participantsTypesAsOutlookPermission.split(","));
    }

    public Boolean getSendNotificationFromSystemUser()
    {
        return sendNotificationFromSystemUser;
    }

    public void setSendNotificationFromSystemUser(Boolean sendNotificationFromSystemUser)
    {
        this.sendNotificationFromSystemUser = sendNotificationFromSystemUser;
    }
}
