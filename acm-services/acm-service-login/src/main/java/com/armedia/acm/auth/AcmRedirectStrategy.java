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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AcmRedirectStrategy implements RedirectStrategy
{
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException
    {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        String redirectUrl = request.getContextPath() + url;
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        // try to identify ajax request to set custom header so the UI shows proper message
        // instead of normal redirect
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
        {
            response.setHeader("acm_concurrent_session_redirect", redirectUrl);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else
        {
            response.sendRedirect(redirectUrl);
        }
    }
}
