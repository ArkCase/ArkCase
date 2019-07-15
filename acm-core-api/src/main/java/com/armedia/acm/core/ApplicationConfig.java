package com.armedia.acm.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

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

    @JsonIgnore
    @Value("${application.properties.baseUrl}")
    private String baseUrl;

    @JsonProperty("application.properties.applicationName")
    @Value("${application.properties.applicationName}")
    private String applicationName;

    @JsonIgnore
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
     * or ArkCase instance can connect to Armedia's internal JIRA server (***REMOVED***).
     * JIRA issue collector can be utilized only if above criteria are met. If those criteria cannot be met,
     * ,set "issueCollectorFlag" flag as "false".
     */
    @JsonProperty("application.properties.issueCollectorFlag")
    @Value("${application.properties.issueCollectorFlag}")
    private Boolean issueCollectorFlag;

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

}
