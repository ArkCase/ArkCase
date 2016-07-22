package com.armedia.acm.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AcmRedirectStrategy implements RedirectStrategy
{
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException
    {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        String redirectUrl = request.getContextPath() + url;
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        // try to identify ajax request to set custom header so the UI shows proper message
        // instead of normal redirect
        if (acceptHeader != null && acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
        {
            response.setHeader("acm_concurrent_session_redirect", redirectUrl);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else
        {
            response.sendRedirect(redirectUrl);
        }
    }
}
