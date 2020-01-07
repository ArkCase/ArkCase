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

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
public interface PortalRequestService
{

    String PROVIDER_NOT_PRESENT = "PROVIDER_IMPLEMENTATIN_NOT_FOUND";

    /**
     * @param portalId
     * @param externalUserId
     *            TODO
     * @param request
     * @return
     * @throws PortalRequestServiceException
     */
    PortalResponse submitRequest(String portalId, String externalUserId, PortalRequest request) throws PortalRequestServiceException;

    /**
     * @param portalId
     * @param portalUserId
     * @param requestType
     * @return
     * @throws PortalRequestServiceException
     */
    List<PortalResponse> listRequests(String portalId, String portalUserId, String requestType) throws PortalRequestServiceException;

    /**
     * @param portalId
     * @param portalUserId
     * @param requestType
     * @param requestId
     * @return
     * @throws PortalRequestServiceException
     */
    PortalResponse getRequestStatus(String portalId, String portalUserId, String requestType, String requestId)
            throws PortalRequestServiceException;

    /**
     * @param se
     * @return
     */
    PortalServiceExceptionMapper getExceptionMapper(PortalRequestServiceException se);

}
