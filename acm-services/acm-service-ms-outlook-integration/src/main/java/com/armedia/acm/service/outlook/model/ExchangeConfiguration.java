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

}
