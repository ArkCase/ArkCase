package com.armedia.acm.webdav;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.PropFindableResource;

import java.util.Date;
import java.util.UUID;

/**
 * This resource is the response to an OPTIONS request; for an OPTIONS request, the actual resource returned has no
 * meaning, the client only wants the response headers.  But Milton requires us to return a resource.  So this simple
 * resource seems to work.
 */
public class AcmOptionsResource implements PropFindableResource
{
    private String uniqueId = UUID.randomUUID().toString();

    private Date today = new Date();
    private final AcmFileSystemResourceFactory resourceFactory;

    public AcmOptionsResource(AcmFileSystemResourceFactory resourceFactory)
    {
        this.resourceFactory = resourceFactory;
    }

    @Override
    public Date getCreateDate()
    {
        return today;
    }

    @Override
    public String getUniqueId()
    {
        return uniqueId;
    }

    @Override
    public String getName()
    {
        return uniqueId;
    }

    @Override
    public Object authenticate(String user, String password)
    {
        return resourceFactory.getSecurityManager().authenticate(user, password);
    }

    @Override
    public boolean authorise(Request request, Request.Method method, Auth auth)
    {
        return resourceFactory.getSecurityManager().authorise(request, method, auth, this);
    }

    @Override
    public String getRealm()
    {
        return "arkcase";
    }

    @Override
    public Date getModifiedDate()
    {
        return today;
    }

    @Override
    public String checkRedirect(Request request) throws NotAuthorizedException, BadRequestException
    {
        return null;
    }
}
