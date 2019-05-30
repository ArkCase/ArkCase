package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import com.armedia.acm.plugins.admin.model.LdapConfigurationProperties;
import com.armedia.acm.plugins.admin.service.LdapConfigurationService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class LdapConfigurationCreateDirectory
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;

    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String createDirectory(@RequestBody String resource) throws AcmLdapConfigurationException
    {
        JSONObject newLdapObject = new JSONObject(resource);
        String id = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID);
        String directoryType = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_DIRECTORY_TYPE);

        if (id == null)
        {
            throw new AcmLdapConfigurationException("ID is undefined");
        }

        Map<String, Object> props;
        try
        {
            props = ldapConfigurationService.getProperties(newLdapObject);
        }
        catch (AcmEncryptionException e)
        {
            throw new AcmLdapConfigurationException("Encryption failed. Cause is: ", e);
        }
        ldapConfigurationService.createLdapDirectoryConfigurations(id, directoryType, props);
        return newLdapObject.toString();
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService)
    {
        this.ldapConfigurationService = ldapConfigurationService;
    }

    public LdapConfigurationService getLdapConfigurationService()
    {
        return ldapConfigurationService;
    }
}
