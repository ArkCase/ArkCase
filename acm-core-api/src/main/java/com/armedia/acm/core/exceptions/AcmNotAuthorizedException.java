package com.armedia.acm.core.exceptions;

/**
 * Created by armdev on 7/11/14.
 */
public class AcmNotAuthorizedException extends Exception
{
    private String url;

    public AcmNotAuthorizedException(String url)
    {
        super();

        this.url = url;
    }

    @Override
    public String getMessage()
    {
        return "User is not authorized for the URL '" + getUrl() + "'";
    }

    public String getUrl()
    {
        return url;
    }
}
