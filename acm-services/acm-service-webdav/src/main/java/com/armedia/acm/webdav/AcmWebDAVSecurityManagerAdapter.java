package com.armedia.acm.webdav;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import com.armedia.acm.web.api.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.SecurityManager;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.Resource;

import java.util.UUID;

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
        if ( url.contains("webdav") && url.contains("FILE"))
        {
            int webdavIdx = url.indexOf("webdav");
            int fileIdx = url.indexOf("FILE");
            String ticket = url.substring(webdavIdx + 7, fileIdx - 1);
            LOG.debug("ticket: {}", ticket);
            try
            {
                Authentication arkcaseAuth = getAuthenticationForTicket(ticket);
                LOG.debug("got auth for ticket {}, user: {}", ticket, arkcaseAuth.getName());
                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, arkcaseAuth.getName());
                MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
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