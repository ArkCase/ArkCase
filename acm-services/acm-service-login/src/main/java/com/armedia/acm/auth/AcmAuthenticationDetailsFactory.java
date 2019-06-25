package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

public class AcmAuthenticationDetailsFactory
        implements AuthenticationDetailsSource<HttpServletRequest, AcmAuthenticationDetails>
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public AcmAuthenticationDetails buildDetails(HttpServletRequest context)
    {

        AcmAuthenticationDetails retval = new AcmAuthenticationDetails(context);
        log.debug("initial ip address: " + retval.getRemoteAddress());

        findXForwardedHeader(retval, context);

        return retval;
    }

    private void findXForwardedHeader(AcmAuthenticationDetails details, HttpServletRequest context)
    {

        /*
         * The general format of the x-forwarded-for header is:
         * X-Forwarded-For: client, proxy1, proxy2
         * where the value is a comma+space separated list of IP addresses, the left-most being the original client,
         * and each successive proxy that passed the request adding the IP address where it received the request
         * from. In this example, the request passed through proxy1, proxy2, and then proxy3 (not shown in the header).
         * proxy3 appears as remote address of the request.
         */
        Enumeration<String> headerNames = context.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String header = headerNames.nextElement();
            if ("x-forwarded-for".equalsIgnoreCase(header))
            {
                String value = context.getHeader(header);
                int comma = value.indexOf(",");
                if (comma > 0)
                {
                    value = value.substring(0, comma);
                }
                details.setUserIpAddress(value);
                log.debug("Remote IP: " + details.getUserIpAddress());

                break;
            }
        }
    }
}
