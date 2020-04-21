package com.armedia.acm.configuration.core.propertysource;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.core.LdapConfigurationContainer;

import org.springframework.core.env.PropertySource;

/**
 * @author mario.gjurcheski
 *
 */
public class LdapConfigServerPropertyResource extends PropertySource<LdapConfigurationContainer>
{

    private final static String CONFIGURATION_SERVER_SOURCE_NAME = "ldap-configuration-server";

    public LdapConfigServerPropertyResource(String name, LdapConfigurationContainer source)
    {
        super(name, source);
    }

    public LdapConfigServerPropertyResource(String name)
    {
        super(name);
    }

    public LdapConfigServerPropertyResource(LdapConfigurationContainer source)
    {
        super(CONFIGURATION_SERVER_SOURCE_NAME, source);
    }

    @Override
    public Object getProperty(String s)
    {
        return null;
    }
}
