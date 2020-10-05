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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.portalgateway.model.PortalConfig;
import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.service.PortalAdminService;
import com.armedia.acm.portalgateway.service.PortalAdminServiceException;
import com.armedia.acm.portalgateway.service.PortalConfigurationService;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

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

    private PortalConfigurationService portalConfigurationService;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    @RequestMapping(value = "/portals", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<PortalInfoDTO> listRegisteredPortals(Authentication auth)
    {
        log.debug("User [{}] is listing registered portals.", auth.getName());
        return portalAdminService.listRegisteredPortals().stream().map(pi -> new PortalInfoDTO(pi)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/portals/{portalId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO getPortalInfo(Authentication auth, @PathVariable(value = "portalId") String portalId)
            throws PortalAdminServiceException
    {
        log.debug("User [{}] is retrieving portal info for portal with [{}] ID.", auth.getName(), portalId);
        return new PortalInfoDTO(portalAdminService.getPortalInfo(portalId));
    }

    @RequestMapping(value = "/portals", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO registerPortal(Authentication auth, @RequestBody PortalInfoDTO portalInfoDTO)
    {
        log.debug("User [{}] is regitering portal for [{}] URL with [{}] user.", auth.getName(), portalInfoDTO.getPortalUrl(),
                portalInfoDTO.getFullName());
        PortalInfo portalInfo = new PortalInfo();
        portalAdminService.updatePortalInfo(portalInfo, portalInfoDTO);
        return new PortalInfoDTO(portalAdminService.registerPortal(portalInfo, portalInfoDTO.getUserId(), portalInfoDTO.getGroupName()));
    }

    @RequestMapping(value = "/portals", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO updatePortal(Authentication auth, @RequestBody PortalInfoDTO portalInfoDTO)
            throws PortalAdminServiceException, AcmAppErrorJsonMsg, AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        log.debug("User [{}] is updating portal for [{}] URL with [{}] user.", auth.getName(), portalInfoDTO.getPortalUrl(),
                portalInfoDTO.getFullName());
        PortalInfo portalInfo = portalAdminService.getPortalInfo(portalInfoDTO.getPortalId());
        PortalInfo oldPortalInfo = new PortalInfo();
        oldPortalInfo.setId(portalInfo.getId());
        oldPortalInfo.setPortalId(portalInfo.getPortalId());
        oldPortalInfo.setPortalDescription(portalInfo.getPortalDescription());
        oldPortalInfo.setPortalUrl(portalInfo.getPortalUrl());
        oldPortalInfo.setUser(portalInfo.getUser());
        oldPortalInfo.setGroup(portalInfo.getGroup());
        oldPortalInfo.setPortalAuthenticationFlag(portalInfo.getPortalAuthenticationFlag());

        checkIfLdapManagementIsAllowed(directoryName);
        portalAdminService.moveExistingLdapUsersToGroup(portalInfoDTO.getGroupName(), oldPortalInfo, directoryName, auth);
        portalAdminService.updatePortalInfo(portalInfo, portalInfoDTO);
        return new PortalInfoDTO(portalAdminService.updatePortal(portalInfo, portalInfoDTO.getUserId()));
    }

    @RequestMapping(value = "/portals/revert", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO revertPortalConfiguration(Authentication auth, @RequestBody PortalInfo portalInfo)
            throws PortalAdminServiceException
    {
        log.debug("User [{}] is reverting old portal info [{}] configuration.", auth.getName(), portalInfo);
        return new PortalInfoDTO(portalAdminService.updatePortal(portalInfo, portalInfo.getUser().getUserId()));
    }

    @RequestMapping(value = "/portals/{portalId}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO unregisterPortal(Authentication auth, @PathVariable(value = "portalId") String portalId)
            throws PortalAdminServiceException
    {
        log.debug("User [{}] is unregistering portal info for portal with [{}] ID.", auth.getName(), portalId);
        return new PortalInfoDTO(portalAdminService.unregisterPortal(portalId));
    }

    @RequestMapping(value = "/portals/authenticatedMode", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PortalConfig getPortalAuthenticatedMode(Authentication auth)
    {
        log.debug("User [{}] is retrieving an authenticated portal mode.", auth.getName());
        return getPortalConfigurationService().getPortalConfiguration();
    }

    @RequestMapping(value = "/portals/authenticatedMode", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void savePortalAuthenticatedMode(Authentication auth, @RequestBody PortalConfig properties)
    {
        log.debug("User [{}] is updating an authenticated portal mode.", auth.getName());
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

    public PortalConfigurationService getPortalConfigurationService()
    {
        return portalConfigurationService;
    }

    public void setPortalConfigurationService(PortalConfigurationService portalConfigurationService)
    {
        this.portalConfigurationService = portalConfigurationService;
    }

}
