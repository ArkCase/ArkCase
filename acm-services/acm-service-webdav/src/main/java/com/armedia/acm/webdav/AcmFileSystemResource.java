package com.armedia.acm.webdav;

import io.milton.http.Auth;
import io.milton.http.LockInfo;
import io.milton.http.LockResult;
import io.milton.http.LockTimeout;
import io.milton.http.LockToken;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.LockedException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.PreConditionFailedException;
import io.milton.resource.GetableResource;
import io.milton.resource.LockableResource;

/**
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public abstract class AcmFileSystemResource implements GetableResource, LockableResource
{

    private final String host;

    private final AcmFileSystemResourceFactory resourceFactory;

    public AcmFileSystemResource(String host, AcmFileSystemResourceFactory resourceFactory)
    {
        this.host = host;
        this.resourceFactory = resourceFactory;
    }

    public String getHost()
    {
        return host;
    }

    protected AcmFileSystemResourceFactory getResourceFactory()
    {
        return resourceFactory;
    }

    // Resource interface methods implementation
    @Override
    public Object authenticate(String user, String password)
    {
        return resourceFactory.getSecurityManager().authenticate(user, password);
    }

    @Override
    public boolean authorise(Request request, Method method, Auth auth)
    {
        return resourceFactory.getSecurityManager().authorise(request, method, auth, this);
    }

    @Override
    public String getRealm()
    {
        return resourceFactory.getSecurityManager().getRealm(host);
    }

    @Override
    public String checkRedirect(Request request) throws NotAuthorizedException, BadRequestException
    {
        return null;
    }

    // GetableResource interface methods implementation

    @Override
    public Long getContentLength()
    {
        return null;
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth)
    {
        return getResourceFactory().getMaxAgeSeconds();
    }

    // LockableResource interface methods implementation

    @Override
    public LockResult lock(LockTimeout timeout, LockInfo lockInfo)
            throws NotAuthorizedException, PreConditionFailedException, LockedException
    {
        return resourceFactory.getLockManager().lock(timeout, lockInfo, this);
    }

    @Override
    public LockResult refreshLock(String token) throws NotAuthorizedException, PreConditionFailedException
    {
        return resourceFactory.getLockManager().refresh(token, this);
    }

    @Override
    public void unlock(String tokenId) throws NotAuthorizedException, PreConditionFailedException
    {
        resourceFactory.getLockManager().unlock(tokenId, this);
    }

    @Override
    public LockToken getCurrentLock()
    {
        return resourceFactory.getLockManager().getCurrentToken(this);
    }

}
