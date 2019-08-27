package com.armedia.acm.core.model;

/*-
 * #%L
 * ACM Core API
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

@JsonSerialize(as = ApplicationConfig.class)
public class ApplicationConfig
{

    @JsonProperty("application.properties.idleLimit")
    @Value("${application.properties.idleLimit}")
    private Integer idleLimit;

    @JsonProperty("application.properties.idlePull")
    @Value("${application.properties.idlePull}")
    private Integer idlePull;

    @JsonProperty("application.properties.idleConfirm")
    @Value("${application.properties.idleConfirm}")
    private Integer idleConfirm;

    @JsonProperty("application.properties.displayUserName")
    @Value("${application.properties.displayUserName}")
    private String displayUserName;

    @JsonProperty("application.properties.historyDays")
    @Value("${application.properties.historyDays}")
    private Integer historyDays;

    @JsonProperty("application.properties.baseUrl")
    @Value("${application.properties.baseUrl}")
    private String baseUrl;

    @JsonProperty("application.properties.applicationName")
    @Value("${application.properties.applicationName}")
    private String applicationName;

    @JsonProperty("application.properties.helpUrl")
    @Value("${application.properties.helpUrl}")
    private String helpUrl;

    /*
     * alfrescoUserIdLdapAttribute controls the user id that is sent to Alfresco in the
     * X-Alfresco-Remote-User header, so that Alfresco knows who the real user is. In
     * Kerberos and CAC (smart card) environments, sometimes the ArkCase user id is not the
     * same as the Alfresco user id... The ArkCase user id could be "david.miller@armedia.com"
     * and the Alfresco user id would be some number from the smart card, e.g. "9283923892".
     * So with this attribute we can control what is sent to Alfresco.
     * Valid values: uid, samAccountName, userPrincipalName, distinguishedName
     */
    @JsonIgnore
    @Value("${application.properties.alfrescoUserIdLdapAttribute}")
    private String alfrescoUserIdLdapAttribute;

    /*
     * Set "issueCollectorFlag" flag as "true" only if ArkCase instance is inside Armedia network or on the Armedia VPN
     * or ArkCase instance can connect to Armedia's internal JIRA server (http://internal.armedia.com/jira).
     * JIRA issue collector can be utilized only if above criteria are met. If those criteria cannot be met,
     * ,set "issueCollectorFlag" flag as "false".
     */
    @JsonProperty("application.properties.issueCollectorFlag")
    @Value("${application.properties.issueCollectorFlag}")
    private Boolean issueCollectorFlag;

    private HashMap<Object, String> roles;

    public Integer getIdleLimit()
    {
        return idleLimit;
    }

    public void setIdleLimit(Integer idleLimit)
    {
        this.idleLimit = idleLimit;
    }

    public String getDisplayUserName()
    {
        return displayUserName;
    }

    public void setDisplayUserName(String displayUserName)
    {
        this.displayUserName = displayUserName;
    }

    public Integer getHistoryDays()
    {
        return historyDays;
    }

    public void setHistoryDays(Integer historyDays)
    {
        this.historyDays = historyDays;
    }

    public Integer getIdlePull()
    {
        return idlePull;
    }

    public void setIdlePull(Integer idlePull)
    {
        this.idlePull = idlePull;
    }

    public Integer getIdleConfirm()
    {
        return idleConfirm;
    }

    public void setIdleConfirm(Integer idleConfirm)
    {
        this.idleConfirm = idleConfirm;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public String getHelpUrl()
    {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl)
    {
        this.helpUrl = helpUrl;
    }

    public String getAlfrescoUserIdLdapAttribute()
    {
        return alfrescoUserIdLdapAttribute;
    }

    public void setAlfrescoUserIdLdapAttribute(String alfrescoUserIdLdapAttribute)
    {
        this.alfrescoUserIdLdapAttribute = alfrescoUserIdLdapAttribute;
    }

    public Boolean getIssueCollectorFlag()
    {
        return issueCollectorFlag;
    }

    public void setIssueCollectorFlag(Boolean issueCollectorFlag)
    {
        this.issueCollectorFlag = issueCollectorFlag;
    }

    public HashMap<Object, String> getRoles()
    {
        return roles;
    }

    public void setRoles(HashMap<Object, String> roles)
    {
        this.roles = roles;
    }
}
