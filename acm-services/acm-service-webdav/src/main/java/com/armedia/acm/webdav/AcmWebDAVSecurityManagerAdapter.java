package com.armedia.acm.webdav;

/*-
 * #%L
 * ACM Service: WebDAV Integration Library
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.SecurityManager;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.Resource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmWebDAVSecurityManagerAdapter implements AcmWebDAVSecurityManager
{

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private SecurityManager wrapped = new NullSecurityManager();

    private AuthenticationTokenService authenticationTokenService;

    @Override
    public Object authenticate(DigestResponse digestRequest)
    {
        LOG.debug("authenticate digest called");
        return true;
    }

    @Override
    public Object authenticate(String user, String password)
    {
        LOG.debug("authenticate user password called: {}", user);
        return true;
    }

    @Override
    public boolean authorise(Request request, Method method, Auth auth, Resource resource)
    {
        LOG.debug("authorize called for request {}", request.getAbsoluteUrl());
        String url = request.getAbsoluteUrl();
        if (url.contains("webdav") && url.contains("FILE"))
        {
            int webdavIdx = url.indexOf("webdav");
            int fileIdx = url.indexOf("FILE");
            String ticket = url.substring(webdavIdx + 7, fileIdx - 1);
            LOG.debug("ticket: {}", ticket);
            try
            {
                Authentication arkcaseAuth = getAuthenticationForTicket(ticket);
                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, arkcaseAuth.getName());
                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
                if (arkcaseAuth.getDetails() != null && arkcaseAuth.getDetails() instanceof AcmAuthenticationDetails)
                {
                    AcmAuthenticationDetails acmAuthenticationDetails = (AcmAuthenticationDetails) arkcaseAuth.getDetails();
                    String cmisUserId = acmAuthenticationDetails.getCmisUserId();
                    LOG.debug("got CMIS user id from auth: {}", cmisUserId);
                    MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, cmisUserId);

                }
                return true;
            }
            catch (IllegalArgumentException e)
            {
                LOG.debug("no auth for ticket {}", ticket);
                return false;
            }
        }

        LOG.debug("not a file URL: {}", url);
        return true;
    }

    @Override
    public String getRealm(String host)
    {
        return wrapped.getRealm(host);
    }

    @Override
    public boolean isDigestAllowed()
    {
        return wrapped.isDigestAllowed();
    }

    @Override
    public Authentication getAuthenticationForTicket(String acmTicket)
    {
        return getAuthenticationTokenService().getAuthenticationForToken(acmTicket);
    }

    @Override
    public void removeAuthenticationForTicket(String acmTicket)
    {
        LOG.debug("We are called for ticket {}", acmTicket);
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

}
