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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * By default, form login will answer a successful authentication request with a 301 MOVED PERMANENTLY status code; this
 * makes sense in the
 * context of an actual login form which needs to redirect after login. For a RESTful web service however, the desired
 * response for a
 * successful authentication should be 200 OK.
 * <p>
 * Created by Bojan Milenkoski on 06.4.2016
 */
public class AcmRestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{
    private Logger log = LogManager.getLogger(getClass());

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        log.debug("REST authentication successful. Authentication details is of type: {}",
                (authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName()));

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null)
        {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam))))
        {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    /**
     * @param requestCache
     *            the requestCache to set
     */
    public void setRequestCache(RequestCache requestCache)
    {
        this.requestCache = requestCache;
    }
}
