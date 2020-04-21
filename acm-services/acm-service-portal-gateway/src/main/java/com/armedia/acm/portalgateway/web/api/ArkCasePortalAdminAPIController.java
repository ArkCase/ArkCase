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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.service.PortalAdminService;
import com.armedia.acm.portalgateway.service.PortalAdminServiceException;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/portalgateway/admin", "/api/latest/service/portalgateway/admin" })
public class ArkCasePortalAdminAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalAdminService portalAdminService;

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/portals", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
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
        updatePortalInfo(portalInfo, portalInfoDTO);
        return new PortalInfoDTO(portalAdminService.registerPortal(portalInfo, portalInfoDTO.getUserId(), portalInfoDTO.getGroupName()));
    }

    @RequestMapping(value = "/portals", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalInfoDTO updatePortal(Authentication auth, @RequestBody PortalInfoDTO portalInfoDTO) throws PortalAdminServiceException
    {
        log.debug("User [{}] is updating portal for [{}] URL with [{}] user.", auth.getName(), portalInfoDTO.getPortalUrl(),
                portalInfoDTO.getFullName());
        PortalInfo portalInfo = portalAdminService.getPortalInfo(portalInfoDTO.getPortalId());
        updatePortalInfo(portalInfo, portalInfoDTO);
        return new PortalInfoDTO(portalAdminService.updatePortal(portalInfo, portalInfoDTO.getUserId()));
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

    @ExceptionHandler(PortalAdminServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(PortalAdminServiceException se)
    {
        log.warn("Handling exception of [{}] type.", se.getClass().getName());
        PortalServiceExceptionMapper exceptionMapper = portalAdminService.getExceptionMapper(se);
        Object errorDetails = exceptionMapper.mapException();
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    @RequestMapping(value = "/portal/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listPortalUsers(Authentication auth, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {

        String query = String.format("object_type_s:PERSON AND object_sub_type_s:PORTAL_FOIA_PERSON");

        try
        {
            String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
            return solrResponse;

        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Portal Users", null, "Could not retrieve portal users.", e);
        }

    }

    /**
     * @param portalInfoDTO
     * @return
     */
    private void updatePortalInfo(PortalInfo portalInfo, PortalInfoDTO portalInfoDTO)
    {
        portalInfo.setPortalDescription(portalInfoDTO.getPortalDescription());
        portalInfo.setPortalUrl(portalInfoDTO.getPortalUrl());
        portalInfo.setPortalAuthenticationFlag(portalInfoDTO.getPortalAuthenticationFlag());
    }

    /**
     * @param portalAdminService
     *            the portalAdminService to set
     */
    public void setPortalAdminService(PortalAdminService portalAdminService)
    {
        this.portalAdminService = portalAdminService;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
