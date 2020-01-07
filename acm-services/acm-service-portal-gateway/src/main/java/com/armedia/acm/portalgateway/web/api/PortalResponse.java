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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
public class PortalResponse
{

    private String responseType;

    private String rawResponse;

    /**
     * @return the responseType
     */
    public String getRequestId()
    {
        return responseType;
    }

    /**
     * @param responseType
     *            the responseType to set
     */
    public void setResponseType(String requestId)
    {
        responseType = requestId;
    }

    /**
     * @return the rawResponse
     */
    public String getRawResponse()
    {
        return rawResponse;
    }

    /**
     * @param rawResponse
     *            the rawResponse to set
     */
    public void setRawResponse(String rawResponse)
    {
        this.rawResponse = rawResponse;
    }

}
