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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 6, 2018
 *
 */
public class PortalRequestServiceException extends PortalServiceException
{

    private static final long serialVersionUID = 1573073696490109760L;

    public PortalRequestServiceException(String message)
    {
        super(message);
    }

    /**
     * @param string
     * @param method
     */
    public PortalRequestServiceException(String message, String method)
    {
        super(message, method);
    }

    /**
     * @param string
     * @param e
     */
    public PortalRequestServiceException(String message, Throwable t)
    {
        super(message, t);
    }

    /**
     * @param string
     * @param e
     * @param method
     */
    public PortalRequestServiceException(String message, Throwable t, String method)
    {
        super(message, t, method);
    }

}
