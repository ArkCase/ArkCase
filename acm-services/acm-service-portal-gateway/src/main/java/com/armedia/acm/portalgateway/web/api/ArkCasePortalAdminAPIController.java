package com.armedia.acm.portalgateway.web.api;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

import com.armedia.acm.portalgateway.model.ArkcasePortalConfig;
import com.armedia.acm.portalgateway.model.PortalConfig;
import com.armedia.acm.portalgateway.service.ArkcasePortalConfigurationService;
import com.armedia.acm.portalgateway.service.PortalConfigurationService;
import com.armedia.acm.portalgateway.service.PortalAdminService;
import com.armedia.acm.portalgateway.service.PortalAdminServiceException;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;
import com.armedia.acm.services.users.web.api.SecureLdapController;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/portalgateway/admin", "/api/latest/service/portalgateway/admin" })
public class ArkCasePortalAdminAPIController extends SecureLdapController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalAdminService portalAdminService;

    private ArkcasePortalConfigurationService arkcasePortalConfigurationService;

    private PortalConfigurationService portalConfigurationService;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    @RequestMapping(value = "/arkcase/portal/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArkcasePortalConfig getArkcasePortalConfiguration(Authentication auth)
    {
        log.debug("User [{}] is retrieving a portal configuration.", auth.getName());
        return getArkcasePortalConfigurationService().getPortalConfiguration();
    }

    @RequestMapping(value = "/arkcase/portal/config", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void saveArkcasePortalConfiguration(Authentication auth, @RequestBody ArkcasePortalConfig properties)
    {
        log.debug("User [{}] is updating a portal configuration.", auth.getName());
        getArkcasePortalConfigurationService().writeConfiguration(properties);
    }

    @RequestMapping(value = "/portal/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PortalConfig getPortalConfiguration(Authentication auth)
    {
        log.debug("User [{}] is retrieving a portal configuration.", auth.getName());
        return getPortalConfigurationService().getPortalConfiguration();
    }

    @RequestMapping(value = "/portal/config", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void savePortalConfiguration(Authentication auth, @RequestBody PortalConfig properties)
    {
        log.debug("User [{}] is updating a portal configuration.", auth.getName());
        getPortalConfigurationService().writeConfiguration(properties);
    }

    @ExceptionHandler(PortalAdminServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(PortalAdminServiceException se)
    {
        log.warn("Handling exception of [{}] type.", se.getClass().getName());
        PortalServiceExceptionMapper exceptionMapper = portalAdminService.getExceptionMapper(se);
        Object errorDetails = exceptionMapper.mapException();
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param portalAdminService
     *            the portalAdminService to set
     */
    public void setPortalAdminService(PortalAdminService portalAdminService)
    {
        this.portalAdminService = portalAdminService;
    }

    @Override
    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    @Override
    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public ArkcasePortalConfigurationService getArkcasePortalConfigurationService()
    {
        return arkcasePortalConfigurationService;
    }

    public void setArkcasePortalConfigurationService(ArkcasePortalConfigurationService arkcasePortalConfigurationService)
    {
        this.arkcasePortalConfigurationService = arkcasePortalConfigurationService;
    }

    public PortalConfigurationService getPortalConfigurationService()
    {
        return portalConfigurationService;
    }

    public void setPortalConfigurationService(PortalConfigurationService portalConfigurationService)
    {
        this.portalConfigurationService = portalConfigurationService;
    }

}
