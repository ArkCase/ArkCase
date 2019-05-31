package com.armedia.acm.services.alfresco.ldap.syncer;

/*-
 * #%L
 * ACM Service: Alfresco LDAP Syncer
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

import org.springframework.beans.factory.annotation.Value;

public class AlfrescoLdapSyncerConfig
{
    @Value("${alfresco.admin.baseurl:https://acm-arkcase/alfresco/s/enterprise/admin/admin-sync}")
    private String adminBaseUrl;

    @Value("${alfresco.admin.username:admin}")
    private String adminUsername;

    @Value("${alfresco.admin.password:admin}")
    private String adminPassword;

    public String getAdminBaseUrl()
    {
        return adminBaseUrl;
    }

    public void setAdminBaseUrl(String adminBaseUrl)
    {
        this.adminBaseUrl = adminBaseUrl;
    }

    public String getAdminUsername()
    {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername)
    {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword)
    {
        this.adminPassword = adminPassword;
    }
}
