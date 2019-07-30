package com.armedia.acm.tools.activemq.model;

/*-
 * #%L
 * Tool Integrations: ActiveMQ Configuration
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

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ivana.shekerova on 7/25/2019.
 */
public class ActiveMqConfig
{
    @Value("#{'${ark.activemq.trustedPackages}'.split(',')}")
    private List<String> trustedPackages;

    @Value("#{'${ark.activemq.defaultTrustedPackages}'.split(',')}")
    private List<String> defaultTrustedPackages;

    @Value("${ark.activemq.transportConnectorURI}")
    private String transportConnectorURI;

    @Value("${ark.activemq.maxConnections}")
    private String maxConnections;

    @Value("${ark.activemq.username}")
    private String username;

    @Value("${ark.activemq.password}")
    private String password;

    public ArrayList<String> mergeTrustedPackages()
    {
        ArrayList<String> mergedPackages = new ArrayList<>();
        mergedPackages.addAll(getDefaultTrustedPackages());
        mergedPackages.addAll(getTrustedPackages());
        return mergedPackages;
    }

    public List<String> getTrustedPackages()
    {
        return trustedPackages;
    }

    public void setTrustedPackages(List<String> trustedPackages)
    {
        this.trustedPackages = trustedPackages;
    }

    public List<String> getDefaultTrustedPackages()
    {
        return defaultTrustedPackages;
    }

    public void setDefaultTrustedPackages(List<String> defaultTrustedPackages)
    {
        this.defaultTrustedPackages = defaultTrustedPackages;
    }

    public String getTransportConnectorURI()
    {
        return transportConnectorURI;
    }

    public void setTransportConnectorURI(String transportConnectorURI)
    {
        this.transportConnectorURI = transportConnectorURI;
    }

    public String getMaxConnections()
    {
        return maxConnections;
    }

    public void setMaxConnections(String maxConnections)
    {
        this.maxConnections = maxConnections;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
