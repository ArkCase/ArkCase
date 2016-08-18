package com.armedia.acm.webdav.handler;

import io.milton.http.Request;
import io.milton.resource.Resource;

import java.util.List;

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
