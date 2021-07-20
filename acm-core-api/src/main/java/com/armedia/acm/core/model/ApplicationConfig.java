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

import com.armedia.acm.core.DynamicApplicationConfig;
import com.armedia.acm.core.UnmodifiableConfigProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = ApplicationConfig.class)
public class ApplicationConfig implements DynamicApplicationConfig
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

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.baseUrl")
    @Value("${application.properties.baseUrl}")
    private String baseUrl;

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.basePortalUrl")
    @Value("${application.properties.basePortalUrl}")
    private String basePortalUrl;

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.applicationName")
    @Value("${application.properties.applicationName}")
    private String applicationName;

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.helpUrl")
    @Value("${application.properties.helpUrl}")
    private String helpUrl;

    @JsonProperty("application.properties.defaultTimezone")
    @Value("${application.properties.defaultTimezone}")
    private String defaultTimezone;

    /*
     * alfrescoUserIdLdapAttribute controls the user id that is sent to Alfresco in the
     * X-Alfresco-Remote-User header, so that Alfresco knows who the real user is. In
     * Kerberos and CAC (smart card) environments, sometimes the ArkCase user id is not the
     * same as the Alfresco user id... The ArkCase user id could be "david.miller@armedia.com"
     * and the Alfresco user id would be some number from the smart card, e.g. "9283923892".
     * So with this attribute we can control what is sent to Alfresco.
     * Valid values: uid, samAccountName, userPrincipalName, distinguishedName
     */
    @UnmodifiableConfigProperty
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

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.logoutUrl")
    @Value("${application.properties.logoutUrl}")
    private String logoutUrl;

    @JsonProperty("application.properties.organizationName")
    @Value("${application.properties.organizationName}")
    private String organizationName;

    @JsonProperty("application.properties.organizationAddress1")
    @Value("${application.properties.organizationAddress1}")
    private String organizationAddress1;

    @JsonProperty("application.properties.organizationAddress2")
    @Value("${application.properties.organizationAddress2}")
    private String organizationAddress2;

    @JsonProperty("application.properties.organizationCity")
    @Value("${application.properties.organizationCity}")
    private String organizationCity;

    @JsonProperty("application.properties.organizationState")
    @Value("${application.properties.organizationState}")
    private String organizationState;

    @JsonProperty("application.properties.organizationZip")
    @Value("${application.properties.organizationZip}")
    private String organizationZip;

    @JsonProperty("application.properties.organizationPhone")
    @Value("${application.properties.organizationPhone}")
    private String organizationPhone;

    @JsonProperty("application.properties.organizationFax")
    @Value("${application.properties.organizationFax}")
    private String organizationFax;

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.templateGuideUrl")
    @Value("${application.properties.templateGuideUrl}")
    private String templateGuideUrl;

    @UnmodifiableConfigProperty
    @JsonProperty("application.properties.sequenceInstructionUrl")
    @Value("${application.properties.sequenceInstructionUrl}")
    private String sequenceInstructionUrl;

    @JsonProperty("application.properties.dashboardBannerEnabled")
    @Value("${application.properties.dashboardBannerEnabled}")
    private Boolean dashboardBannerEnabled;

    @JsonProperty("application.properties.listFilesInFolder")
    @Value("${application.properties.listFilesInFolder}")
    private String listFilesInFolder;

    @JsonProperty("application.properties.allowForgotUsernameAndPasswordOnLogin")
    @Value("${application.properties.allowForgotUsernameAndPasswordOnLogin}")
    private Boolean allowForgotUsernameAndPasswordOnLogin;

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

    public String getBasePortalUrl()
    {
        return basePortalUrl;
    }

    public void setBasePortalUrl(String basePortalUrl)
    {
        this.basePortalUrl = basePortalUrl;
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

    public String getLogoutUrl()
    {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl)
    {
        this.logoutUrl = logoutUrl;
    }

    public String getDefaultTimezone()
    {
        return defaultTimezone;
    }

    public void setDefaultTimezone(String defaultTimezone)
    {
        this.defaultTimezone = defaultTimezone;
    }

    public String getOrganizationName()
    {
        return organizationName;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public String getOrganizationAddress1()
    {
        return organizationAddress1;
    }

    public void setOrganizationAddress1(String organizationAddress1)
    {
        this.organizationAddress1 = organizationAddress1;
    }

    public String getOrganizationAddress2()
    {
        return organizationAddress2;
    }

    public void setOrganizationAddress2(String organizationAddress2)
    {
        this.organizationAddress2 = organizationAddress2;
    }

    public String getOrganizationCity()
    {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity)
    {
        this.organizationCity = organizationCity;
    }

    public String getOrganizationState()
    {
        return organizationState;
    }

    public void setOrganizationState(String organizationState)
    {
        this.organizationState = organizationState;
    }

    public String getOrganizationZip()
    {
        return organizationZip;
    }

    public void setOrganizationZip(String organizationZip)
    {
        this.organizationZip = organizationZip;
    }

    public String getOrganizationPhone()
    {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone)
    {
        this.organizationPhone = organizationPhone;
    }

    public String getOrganizationFax()
    {
        return organizationFax;
    }

    public void setOrganizationFax(String organizationFax)
    {
        this.organizationFax = organizationFax;
    }

    public String getTemplateGuideUrl()
    {
        return templateGuideUrl;
    }

    public void setTemplateGuideUrl(String templateGuideUrl)
    {
        this.templateGuideUrl = templateGuideUrl;
    }

    public String getSequenceInstructionUrl()
    {
        return sequenceInstructionUrl;
    }

    public void setSequenceInstructionUrl(String sequenceInstructionUrl)
    {
        this.sequenceInstructionUrl = sequenceInstructionUrl;
    }

    public Boolean getDashboardBannerEnabled()
    {
        return dashboardBannerEnabled;
    }

    public void setDashboardBannerEnabled(Boolean dashboardBannerEnabled)
    {
        this.dashboardBannerEnabled = dashboardBannerEnabled;
    }

    public String getListFilesInFolder()
    {
        return listFilesInFolder;
    }

    public void setListFilesInFolder(String listFilesInFolder)
    {
        this.listFilesInFolder = listFilesInFolder;
    }

    public Boolean getAllowForgotUsernameAndPasswordOnLogin() {
        return allowForgotUsernameAndPasswordOnLogin;
    }

    public void setAllowForgotUsernameAndPasswordOnLogin(Boolean allowForgotUsernameAndPasswordOnLogin) {
        this.allowForgotUsernameAndPasswordOnLogin = allowForgotUsernameAndPasswordOnLogin;
    }
}
