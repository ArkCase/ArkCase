package com.armedia.acm.auth;

import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AcmRedirectStrategy implements RedirectStrategy
{
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException
    {
        String acceptHeader = request.getHeader("Accept");

        String  redirectUrl = request.getContextPath() + url;
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        if (acceptHeader != null && acceptHeader.contains("application/json"))
        {
            response.setHeader("concurrent_session_redirect", redirectUrl);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else
        {
            response.sendRedirect(redirectUrl);
        }
    }
}
