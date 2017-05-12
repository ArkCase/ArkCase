package com.armedia.acm.web.api;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 10, 2017
 *
 */
public class AcmMultipartResolver extends CommonsMultipartResolver
{
    /**
     * Constant for HTTP POST method.
     */
    private static final String POST_METHOD = "POST";

    /**
     * Constant for HTTP PUT method.
     */
    private static final String PUT_METHOD = "PUT";

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.multipart.commons.CommonsMultipartResolver#isMultipart(javax.servlet.http.
     * HttpServletRequest)
     */
    @Override
    public boolean isMultipart(HttpServletRequest request)
    {
        return (request != null && isMultipartContent(request));
    }

    private final boolean isMultipartContent(HttpServletRequest request)
    {
        if (!POST_METHOD.equalsIgnoreCase(request.getMethod()) && !PUT_METHOD.equalsIgnoreCase(request.getMethod()))
        {
            return false;
        }
        return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }
}
