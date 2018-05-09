package com.armedia.acm.webdav.handler;

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

import java.util.List;

import io.milton.http.Request;
import io.milton.resource.Resource;

/**
 * Created by nebojsha on 11.08.2016.
 */
public class AcmBasicAuthHandler extends io.milton.http.http11.auth.BasicAuthHandler
{
    public AcmBasicAuthHandler()
    {
        super();
    }

    @Override
    public void appendChallenges(Resource resource, Request request, List<String> challenges)
    {
        super.appendChallenges(resource, request, challenges);
    }

    @Override
    public Object authenticate(Resource resource, Request request)
    {
        Object authenticate = super.authenticate(resource, request);
        return authenticate;
    }

    @Override
    public boolean credentialsPresent(Request request)
    {
        boolean credentialsPresent = super.credentialsPresent(request);
        return credentialsPresent;
    }

    @Override
    public boolean supports(Resource r, Request request)
    {
        boolean supports = super.supports(r, request);
        return supports;
    }

    @Override
    public boolean isCompatible(Resource resource, Request request)
    {
        boolean compatible = super.isCompatible(resource, request);
        return compatible;
    }
}
