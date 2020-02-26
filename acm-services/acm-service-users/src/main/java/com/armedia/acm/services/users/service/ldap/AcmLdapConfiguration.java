package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.Map;

/**
 * @author mario.gjurcheski
 *
 */
public class AcmLdapConfiguration
{
    public static final String LDAP_SYNC_CONFIG_PROP_KEY = "springConfigLdapProperties";

    private Map<String, Map<String, Object>> attributes;

    @MapValue(value = LDAP_SYNC_CONFIG_PROP_KEY, convertFromTheRootKey = true, configurationName = "ldapConfiguration")
    public Map<String, Map<String, Object>> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, Map<String, Object>> attributes)
    {
        this.attributes = attributes;
    }
}
