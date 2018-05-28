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

import java.util.Date;
import java.util.UUID;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.PropFindableResource;

/**
 * This resource is the response to an OPTIONS request; for an OPTIONS request, the actual resource returned has no
 * meaning, the client only wants the response headers. But Milton requires us to return a resource. So this simple
 * resource seems to work.
 */
public class AcmOptionsResource implements PropFindableResource
{
    private final AcmFileSystemResourceFactory resourceFactory;
    private String uniqueId = UUID.randomUUID().toString();
    private Date today = new Date();

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
