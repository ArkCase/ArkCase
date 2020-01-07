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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private Logger log = LogManager.getLogger(getClass());

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
