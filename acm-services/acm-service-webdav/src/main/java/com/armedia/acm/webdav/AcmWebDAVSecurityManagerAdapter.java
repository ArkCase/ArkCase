package com.armedia.acm.webdav;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public Authentication getSpringAuthentication()
    {
        // can't obtain authentication like this
        // though, authentication is not used later, so is it necessary, or there is a security problem?
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        return currentUser;
    }

}