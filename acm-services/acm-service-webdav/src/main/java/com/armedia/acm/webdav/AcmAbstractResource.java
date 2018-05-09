package com.armedia.acm.webdav;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.DigestResource;
import io.milton.resource.PropFindableResource;

/**
 * Created by nebojsha on 13.08.2016.
 */
public abstract class AcmAbstractResource implements DigestResource, PropFindableResource
{

    private final AcmFileSystemResourceFactory resourceFactory;
    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmAbstractResource(AcmFileSystemResourceFactory resourceFactory)
    {
        this.resourceFactory = resourceFactory;
    }

    @Override
    public Object authenticate(String user, String requestedPassword)
    {
        return resourceFactory.getSecurityManager().authenticate(user, requestedPassword);
    }

    @Override
    public Object authenticate(DigestResponse digestRequest)
    {
        return resourceFactory.getSecurityManager().authenticate(digestRequest);
    }

    @Override
    public String getUniqueId()
    {
        return null;
    }

    @Override
    public String checkRedirect(Request request)
    {
        return null;
    }

    @Override
    public boolean authorise(Request request, Request.Method method, Auth auth)
    {
        return resourceFactory.getSecurityManager().authorise(request, method, auth, this);
    }

    @Override
    public String getRealm()
    {
        return "armedia";
    }

    @Override
    public Date getModifiedDate()
    {
        return null;
    }

    @Override
    public Date getCreateDate()
    {
        return null;
    }

    @Override
    public boolean isDigestAllowed()
    {
        return true;
    }
}