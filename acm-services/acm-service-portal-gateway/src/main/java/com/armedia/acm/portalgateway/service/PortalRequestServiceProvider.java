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
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2018
 *
 */
public interface PortalRequestServiceProvider
{

    String SUBMIT_REQUEST_METHOD_DESERIALIZE = "SUBMIT_REQUEST_DESERIALIZE";

    String SUBMIT_REQUEST_METHOD_CREATE_REQUEST = "SUBMIT_REQUEST_CREATE_REQUEST";

    String LIST_REQUESTS_METHOD_RETRIEVE = "LIST_REQUESTS_RETRIEVE";

    String LIST_REQUESTS_METHOD_SERIALIZE = "LIST_REQUESTS_SERIALIZE";

    String GET_REQUEST_STATUS_METHOD_RETRIEVE = "GET_REQUEST_STATUS_RETRIEVE";

    String GET_REQUEST_STATUS_METHOD_SERIALIZE = "GET_REQUEST_STATUS_SERIALIZE";

    String providesServiceForRequestType();

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
     * @return
     * @throws PortalRequestServiceException
     */
    List<PortalResponse> listRequests(String portalId, String portalUserId) throws PortalRequestServiceException;

    /**
     * @param portalId
     * @param portalUserId
     * @param requestId
     * @return
     * @throws PortalRequestServiceException
     */
    PortalResponse getRequestStatus(String portalId, String portalUserId, String requestId)
            throws PortalRequestServiceException;


    /**
     * @param request
     * @return
     * @throws PortalRequestServiceException
     */
    void submitInquiry(PortalRequest request) throws PortalRequestServiceException;
}
