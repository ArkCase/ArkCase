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
import io.milton.resource.LockableResource;

/**
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public abstract class AcmFileSystemResource implements LockableResource
{

    private final String host;

    private final AcmFileSystemResourceFactory resourceFactory;

    private String userId;
    private String containerObjectType;
    private String containerObjectId;

    public AcmFileSystemResource(String host, AcmFileSystemResourceFactory resourceFactory, String userId, String containerObjectType,
            String containerObjectId)
    {
        this.host = host;
        this.resourceFactory = resourceFactory;

        this.userId = userId;
        this.containerObjectType = containerObjectType;
        this.containerObjectId = containerObjectId;
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

    // LockableResource interface methods implementation

    @Override
    public LockResult lock(LockTimeout timeout, LockInfo lockInfo)
            throws NotAuthorizedException, PreConditionFailedException, LockedException
    {
        return resourceFactory.getLockManager().lock(timeout, lockInfo, this);
    }

    @Override
    public LockResult refreshLock(String token, LockTimeout lockTimeout) throws NotAuthorizedException, PreConditionFailedException
    {
        return resourceFactory.getLockManager().refresh(token, lockTimeout, this);
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

    public String getUserId()
    {
        return userId;
    }

    public String getContainerObjectType()
    {
        return containerObjectType;
    }

    public String getContainerObjectId()
    {
        return containerObjectId;
    }
}
