package com.armedia.acm.webdav;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;

import org.springframework.security.core.Authentication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.Resource;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public class AcmWebDAVSecurityManagerAdapter implements AcmWebDAVSecurityManager
{

    private io.milton.http.SecurityManager wrapped = new NullSecurityManager();

    private AuthenticationTokenService authenticationTokenService;

    private ConcurrentMap<String, Authentication> acmTicketToAuthentication = new ConcurrentHashMap<String, Authentication>();

    @Override
    public Object authenticate(DigestResponse digestRequest)
    {
        return wrapped.authenticate(digestRequest);
    }

    @Override
    public Object authenticate(String user, String password)
    {
        return wrapped.authenticate(user, password);
    }

    @Override
    public boolean authorise(Request request, Method method, Auth auth, Resource resource)
    {
        return wrapped.authorise(request, method, auth, resource);
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
        return acmTicketToAuthentication.get(acmTicket);
    }

    @Override
    public Authentication addAuthenticationForTicket(String acmTicket)
    {
        Authentication authentication = getAuthenticationTokenService().getAuthenticationForToken(acmTicket);
        acmTicketToAuthentication.put(acmTicket, authentication);
        return authentication;
    }

    @Override
    public void removeAuthenticationForTicket(String acmTicket)
    {
        acmTicketToAuthentication.remove(acmTicket);
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