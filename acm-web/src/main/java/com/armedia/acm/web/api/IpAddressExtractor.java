package com.armedia.acm.web.api;

/*-
 * #%L
 * ACM Shared Web Artifacts
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

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

/**
 * Created by dmiller on 8/16/2016.
 */
public class IpAddressExtractor
{
    public String extractIpAddress(HttpServletRequest httpServletRequest)
    {
        String ipAddress = getRealClientIpAddress(httpServletRequest);

        ipAddress = ipAddress == null ? httpServletRequest.getRemoteAddr() : ipAddress;

        return ipAddress;
    }

    /**
     * The general format of the x-forwarded-for header is:
     * X-Forwarded-For: client, proxy1, proxy2
     * where the value is a comma+space separated list of IP addresses, the left-most being the original client,
     * and each successive proxy that passed the request adding the IP address where it received the request
     * from. In this example, the request passed through proxy1, proxy2, and then proxy3 (not shown in the header).
     * proxy3 appears as remote address of the request.
     */
    private String getRealClientIpAddress(HttpServletRequest httpServletRequest)
    {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String header = headerNames.nextElement();
            if ("x-forwarded-for".equalsIgnoreCase(header))
            {
                String value = httpServletRequest.getHeader(header);
                int comma = value.indexOf(",");
                if (comma > 0)
                {
                    value = value.substring(0, comma);
                }
                return value;
            }
        }

        return null;
    }
}
