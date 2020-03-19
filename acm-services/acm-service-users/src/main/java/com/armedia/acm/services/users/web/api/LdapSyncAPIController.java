package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.LdapSyncService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
@RequestMapping({ "/api/v1/ldap", "/api/latest/ldap" })
public class LdapSyncAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder acmContextHolder;
    private LdapSyncService ldapSyncService;

    @RequestMapping(value = "/{directory:.+}/partial-sync", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity initiatePartialSync(@PathVariable String directory, Authentication authentication)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directory));
        if (ldapSyncConfig != null)
        {
            log.debug("User [{}] initiated partial sync of directory [{}]", authentication.getName(), directory);
            ldapSyncService.initiateSync(authentication.getName(), false, ldapSyncConfig);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Service for directory [ " + directory + " ] not found"),
                HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{directory:.+}/full-sync", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity initiateFullSync(@PathVariable String directory, Authentication authentication)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directory));
        if (ldapSyncConfig != null)
        {
            log.debug("User [{}] initiated full sync of directory [{}]", authentication.getName(), directory);
            ldapSyncService.initiateSync(authentication.getName(), true, ldapSyncConfig);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(Collections.singletonMap("message", "Service for directory [ " + directory + " ] not found"),
                HttpStatus.BAD_REQUEST);
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public void setLdapSyncService(LdapSyncService ldapSyncService)
    {
        this.ldapSyncService = ldapSyncService;
    }
}
