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

import com.armedia.acm.portalgateway.service.CheckPortalUserAssignement;
import com.armedia.acm.portalgateway.service.PortalId;
import com.armedia.acm.portalgateway.service.PortalRequestService;
import com.armedia.acm.portalgateway.service.PortalRequestServiceException;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/portalgateway/{portalId}/requests", "/api/latest/service/portalgateway/{portalId}/requests" })
public class ArkCasePortalGatewayRequestAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalRequestService portalRequestService;

    @CheckPortalUserAssignement
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalResponse submitRequest(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody PortalRequest request) throws PortalRequestServiceException
    {
        log.debug("Submitting request from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, auth.getName(),
                request.getRequestType());
        return portalRequestService.submitRequest(portalId, auth.getName(), request);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalUserId}/{requestType:.+}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<PortalResponse> listRequests(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "portalUserId") String portalUserId, @PathVariable(value = "requestType") String requestType)
            throws PortalRequestServiceException
    {
        log.debug("Listing requests from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                requestType);
        return portalRequestService.listRequests(portalId, portalUserId, requestType);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalUserId}/{requestType}/{requestId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalResponse getRequestStatus(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "portalUserId") String portalUserId, @PathVariable(value = "requestType") String requestType,
            @PathVariable(value = "requestId") String requestId)
            throws PortalRequestServiceException
    {
        log.debug("Getting request status from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                requestType);
        return portalRequestService.getRequestStatus(portalId, portalUserId, requestType, requestId);
    }

    @ExceptionHandler(PortalRequestServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleRequestException(PortalRequestServiceException se)
    {
        log.warn("Handling exception of [{}] type.", se.getClass().getName());
        PortalServiceExceptionMapper exceptionMapper = portalRequestService.getExceptionMapper(se);
        Object errorDetails = exceptionMapper.mapException();
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param portalRequestService
     *            the portalRequestService to set
     */
    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

}
