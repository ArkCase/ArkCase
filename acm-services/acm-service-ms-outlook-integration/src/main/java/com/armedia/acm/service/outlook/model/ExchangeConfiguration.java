package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author sasko.tanaskoski
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
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
