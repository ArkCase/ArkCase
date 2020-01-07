/**
 *
 */
package com.armedia.acm.portalgateway.service;

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

import com.armedia.acm.portalgateway.web.api.PortalRequest;
import com.armedia.acm.portalgateway.web.api.PortalResponse;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 30, 2018
 *
 */
public class DefaultPluggablePortalRequestService implements PortalRequestService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder springContextHolder;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestService#submitRequest(java.lang.String,
     * com.armedia.acm.portalgateway.web.api.PortalRequest)
     */
    @Override
    public PortalResponse submitRequest(String portalId, String portalUserId, PortalRequest request) throws PortalRequestServiceException
    {
        log.debug("Submitting request from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                request.getRequestType());
        return getServiceProvider(request.getRequestType()).submitRequest(portalId, portalUserId, request);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestService#listRequests(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<PortalResponse> listRequests(String portalId, String portalUserId, String requestType) throws PortalRequestServiceException
    {
        log.debug("Listing requests from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                requestType);
        return getServiceProvider(requestType).listRequests(portalId, portalUserId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestService#getRequestStatus(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public PortalResponse getRequestStatus(String portalId, String portalUserId, String requestType, String requestId)
            throws PortalRequestServiceException
    {
        log.debug("Getting request status from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                requestType);
        return getServiceProvider(requestType).getRequestStatus(portalId, portalUserId, requestId);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestService#getExceptionMapper(com.armedia.acm.portalgateway.
     * service.PortalRequestServiceException)
     */
    @Override
    public PortalRequestServiceExceptionMapper getExceptionMapper(PortalRequestServiceException se)
    {
        return new PortalRequestServiceExceptionMapper(se);
    }

    private PortalRequestServiceProvider getServiceProvider(String requestType) throws PortalRequestServiceException
    {
        log.debug("Trying to find implementation of [{}] for [{}] request type.", PortalRequestServiceProvider.class.getName(),
                requestType);

        Map<String, PortalRequestServiceProvider> providers = springContextHolder.getAllBeansOfType(PortalRequestServiceProvider.class);

        Optional<PortalRequestServiceProvider> provider = providers.values().stream()
                .filter(p -> p.providesServiceForRequestType().equals(requestType)).findFirst();
        return provider.orElseThrow(() -> {
            log.warn("Could not find [{}] implementations for [{}] request type.", PortalRequestServiceProvider.class.getName(),
                    requestType);
            return new PortalRequestServiceException(String.format("Could not find [%s] implementations for [%s] request type.",
                    PortalRequestServiceProvider.class.getName(), requestType), PROVIDER_NOT_PRESENT);
        });
    }

    /**
     * @param springContextHolder
     *            the springContextHolder to set
     */
    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

}
