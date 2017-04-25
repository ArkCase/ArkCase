package com.armedia.acm.service.outlook.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author sasko.tanaskoski
 *
 */
@JsonInclude(Include.NON_NULL)
public class ExchangeConfiguration
{

    private boolean integrationEnabled;

    private String serverVersion;

    private boolean enableAutodiscovery;

    private String clientAccessServer;

    private String defaultAccess;

    private String systemUserEmail = "no-such-mailbox@armedia.com";

    private String systemUserEmailPassword = "";

    private String systemUserId = "no-such-mailbox@armedia.com";

    /**
     * @return the integrationEnabled
     */
    public boolean isIntegrationEnabled()
    {
        return integrationEnabled;
    }

    /**
     * @param integrationEnabled
     *            the integrationEnabled to set
     */
    public void setIntegrationEnabled(boolean integrationEnabled)
    {
        this.integrationEnabled = integrationEnabled;
    }

    /**
     * @return the serverVersion
     */
    public String getServerVersion()
    {
        return serverVersion;
    }

    /**
     * @param serverVersion
     *            the serverVersion to set
     */
    public void setServerVersion(String serverVersion)
    {
        this.serverVersion = serverVersion;
    }

    /**
     * @return the enableAutodiscovery
     */
    public boolean isEnableAutodiscovery()
    {
        return enableAutodiscovery;
    }

    /**
     * @param enableAutodiscovery
     *            the enableAutodiscovery to set
     */
    public void setEnableAutodiscovery(boolean enableAutodiscovery)
    {
        this.enableAutodiscovery = enableAutodiscovery;
    }

    /**
     * @return the clientAccessServer
     */
    public String getClientAccessServer()
    {
        return clientAccessServer;
    }

    /**
     * @param clientAccessServer
     *            the clientAccessServer to set
     */
    public void setClientAccessServer(String clientAccessServer)
    {
        this.clientAccessServer = clientAccessServer;
    }

    /**
     * @return the defaultAccess
     */
    public String getDefaultAccess()
    {
        return defaultAccess;
    }

    /**
     * @param defaultAccess
     *            the defaultAccess to set
     */
    public void setDefaultAccess(String defaultAccess)
    {
        this.defaultAccess = defaultAccess;
    }

    /**
     * @return the systemUserEmail
     */
    public String getSystemUserEmail()
    {
        return systemUserEmail;
    }

    /**
     * @param systemUserEmail
     *            the systemUserEmail to set
     */
    public void setSystemUserEmail(String systemUserEmail)
    {
        this.systemUserEmail = systemUserEmail;
    }

    /**
     * @return the systemUserEmailPassword
     */
    public String getSystemUserEmailPassword()
    {
        return systemUserEmailPassword;
    }

    /**
     * @param systemUserEmailPassword
     *            the systemUserEmailPassword to set
     */
    public void setSystemUserEmailPassword(String systemUserEmailPassword)
    {
        this.systemUserEmailPassword = systemUserEmailPassword;
    }

    /**
     * @return the systemUserId
     */
    public String getSystemUserId()
    {
        return systemUserId;
    }

    /**
     * @param systemUserId
     *            the systemUserId to set
     */
    public void setSystemUserId(String systemUserId)
    {
        this.systemUserId = systemUserId;
    }

}
