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

import static com.armedia.acm.portalgateway.service.PortalAdminService.GET_INFO_METHOD;
import static com.armedia.acm.portalgateway.service.PortalAdminService.UNREGISTER_METHOD;
import static com.armedia.acm.portalgateway.service.PortalAdminService.UPDATE_METHOD_PORTAL;
import static com.armedia.acm.portalgateway.service.PortalAdminService.UPDATE_METHOD_USER;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 25, 2018
 *
 */
public class PortalAdminServiceExceptionMapper implements PortalServiceExceptionMapper
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalAdminServiceException exception;

    /**
     * @param se
     */
    public PortalAdminServiceExceptionMapper(PortalAdminServiceException se)
    {
        exception = se;
    }

    /**
     * @return
     */
    @Override
    public Object mapException()
    {
        log.debug("Mapping exception of [{}] type.", exception.getClass().getName());

        Map<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("error_cause", exception.getMethod());
        errorDetails.put("error_message", exception.getMessage());
        return errorDetails;
    }

    /**
     * @return
     */
    @Override
    public HttpStatus getStatusCode()
    {
        log.debug("Creating status code for exception of [{}] type.", exception.getClass().getName());

        if (GET_INFO_METHOD.equals(exception.getMethod()))
        {
            return HttpStatus.NOT_FOUND;
        }
        else if (UPDATE_METHOD_USER.equals(exception.getMethod()))
        {
            return HttpStatus.NOT_FOUND;
        }
        else if (UPDATE_METHOD_PORTAL.equals(exception.getMethod()))
        {
            return HttpStatus.NOT_FOUND;
        }
        else if (UNREGISTER_METHOD.equals(exception.getMethod()))
        {
            return HttpStatus.NOT_FOUND;
        }
        else
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
